/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sun.codemodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.ProgressCodeWriter;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;

import es.bsc.servicess.ide.model.ElementClass;
import es.bsc.servicess.ide.model.Parameter;
import es.bsc.servicess.ide.model.ServiceCoreElement;

public class ClassGenerator {

	/*
	 * private HashMap<String, ReferenceList> collectedReferences;
	 * 
	 * private HashSet<JClass> importedClasses;
	 */

	private JPackage javaLang;

	private final String indentSpace;

	private final PrintStream pw;

	private IFolder generated;

	private IJavaProject project;

	private IPackageFragmentRoot pfr;

	private static enum Mode {
		/**
		 * Collect all the type names and identifiers. In this mode we don't
		 * actually generate anything.
		 */
		COLLECTING,
		/**
		 * Print the actual source code.
		 */
		PRINTING
	}

	public ClassGenerator(PrintStream s, String space, IFolder generated,
			IJavaProject project) {
		pw = s;
		indentSpace = space;
		/*
		 * collectedReferences = new HashMap<String, ReferenceList>(); //ids =
		 * new HashSet<String>(); importedClasses = new HashSet<JClass>();
		 */
		this.project = project;
		this.generated = generated;
		pfr = project.getPackageFragmentRoot(generated);
	}

	public ClassGenerator(PrintStream s, IFolder generated, IJavaProject project) {
		this(s, "    ", generated, project);
	}

	private boolean supressImport(JClass clazz, JClass c) {
		if (clazz._package().isUnnamed())
			return true;

		final String packageName = clazz._package().name();
		if (packageName.equals("java.lang"))
			return true; // no need to explicitly import java.lang classes

		if (clazz._package() == c._package()) {
			// inner classes require an import stmt.
			// All other pkg local classes do not need an
			// import stmt for ref.
			if (clazz.outer() == null) {
				return true; // no need to explicitly import a class into itself
			}
		}
		return false;
	}

	public void generateClassesCode(List<S2JJAXBModel> schModels,
			IProgressMonitor pm) throws IOException, CoreException {
		IPackageFragmentRoot pfr = project.getPackageFragmentRoot(generated);
		if (pfr != null && pfr.exists()) {
			CodeWriter src = new FileCodeWriter(generated.getLocation()
					.toFile());
			if (pw != null) {
				src = new ProgressCodeWriter(src, pw);
			}
			for (S2JJAXBModel m : schModels) {
				JCodeModel jcm = m.generateCode(null, null);
				jcm.build(src);
			}
		} else
			throw new JavaModelException(new Exception(
					"Error package root does not exists"),
					JavaModelStatus.ERROR);
	}

	public void generateClassesCode(Map<String, ElementClass> schClasses,
			List<S2JJAXBModel> schModels, IProgressMonitor pm)
			throws IOException, CoreException {
		IPackageFragmentRoot pfr = project.getPackageFragmentRoot(generated);
		if (pfr != null && pfr.exists()) {
			CodeWriter src = new FileCodeWriter(generated.getLocation()
					.toFile());
			if (pw != null) {
				src = new ProgressCodeWriter(src, pw);
			}

			Collection<ElementClass> elements = getElementsFromModels(
					schClasses, schModels);
			// for (ElementClass ec : schClasses.values()) {
			for (ElementClass ec : elements) {
				JDefinedClass c = ec.getjDefClass();
				if (c != null) {
					if (!alreadyExists(c.fullName())) {
						if ((!c.isPrimitive())
								&& (!c.fullName().startsWith("java.lang"))) {
							System.out.println("Generating Class: "
									+ c.fullName());
							if (c.isHidden())
								continue; // don't generate this file
							JFormatter f = createJavaSourceFileWriter(src,
									c.name(), c.getPackage());
							f.write(c);
							f.close();
							generated.refreshLocal(IFolder.DEPTH_INFINITE, pm);
							// HashMap<String, ElementClass> extraClasses =
							// getReferencedClasses(c, schClasses, schModels);
							// generateClassesCode(extraClasses, schModels, pm);
						}
						modifyObjectFactory(ec, pm);
					}
				}

			}

		} else
			throw new JavaModelException(new Exception(
					"Error package root does not exists"),
					JavaModelStatus.ERROR);
	}

	private void modifyObjectFactory(ElementClass ec, IProgressMonitor pm)
			throws JavaModelException {
		JDefinedClass c = ec.getjDefClass();
		if (ec.getElementType() != null) {
			IPackageFragmentRoot pfr = project
					.getPackageFragmentRoot(generated);
			if (pfr != null && pfr.exists()) {
				IPackageFragment pf = pfr.getPackageFragment(c.getPackage()
						.name());
				if (pf != null && pf.exists()) {
					ICompilationUnit of = pf
							.getCompilationUnit("ObjectFactory.java");
					if (of != null && !of.exists()) {
						of = pf.createCompilationUnit("ObjectFactory.java", "",
								true, pm);
						of.createPackageDeclaration(c.getPackage().name(), pm);
						of.createImport(
								"javax.xml.bind.annotation.XmlRegistry", null,
								Flags.AccDefault, pm);
						String lineDelimiter = StubUtility
								.getLineDelimiterUsed(project);
						IType t = of.createType(
								generateObjectFactory(lineDelimiter), null,
								false, pm);
						t.createMethod("public ObjectFactory(){}"
								+ lineDelimiter, null, false, pm);
					}
					of = addClass(ec, of, pm);

				} else
					throw new JavaModelException(new Exception("Error package "
							+ c.getPackage().name() + " does not exists"),
							JavaModelStatus.ERROR);

			} else
				throw new JavaModelException(new Exception(
						"Error package root " + generated.getName()
								+ " does not exists"), JavaModelStatus.ERROR);
		}
	}

	private ICompilationUnit addClass(ElementClass ec, ICompilationUnit of,
			IProgressMonitor pm) throws JavaModelException {
		JDefinedClass c = ec.getjDefClass();
		IImportDeclaration imp = of.getImport("javax.xml.namespace.QName");
		if (imp == null || (!imp.exists())) {
			of.createImport("javax.xml.namespace.QName", null,
					Flags.AccDefault, pm);
		}
		imp = of.getImport("javax.xml.bind.annotation.XmlElementDecl");
		if (imp == null || (!imp.exists())) {
			of.createImport("javax.xml.bind.annotation.XmlElementDecl", null,
					Flags.AccDefault, pm);
		}
		imp = of.getImport("javax.xml.bind.JAXBElement");
		if (imp == null || (!imp.exists())) {
			of.createImport("javax.xml.bind.JAXBElement", null,
					Flags.AccDefault, pm);
		}
		IType t = of.getType("ObjectFactory");
		if (t != null && t.exists()) {
			String lineDelimiter = StubUtility.getLineDelimiterUsed(project);
			t.createField("private final static QName _" + c.name()
					+ "_QNAME= new QName(\""
					+ ec.getElementType().getNamespaceURI() + "\", \""
					+ ec.getElementType().getLocalPart() + "\");"
					+ lineDelimiter, null, false, pm);

			t.createMethod(generateFirstMethod(c, lineDelimiter), null, false,
					pm);
			t.createMethod(generateSecondMethod(ec, lineDelimiter), null,
					false, pm);
		} else
			throw new JavaModelException(
					new Exception(
							"Error ObjectFactory class does not exists in compilation unit"),
					JavaModelStatus.ERROR);

		return of;
	}

	private String generateSecondMethod(ElementClass ec, String lineDelimiter) {
		String str = new String("@XmlElementDecl(namespace = \""
				+ ec.getElementType().getNamespaceURI() + "\", name = \""
				+ ec.getElementType().getLocalPart() + "\")" + lineDelimiter);
		str = str.concat("public JAXBElement<" + ec.getjDefClass().name()
				+ "> create" + ec.getjDefClass().name() + "("
				+ ec.getjDefClass().name() + " value){" + lineDelimiter);
		str = str.concat("\t return new JAXBElement<"
				+ ec.getjDefClass().name() + ">(_" + ec.getjDefClass().name()
				+ "_QNAME, " + ec.getjDefClass().name()
				+ ".class, null, value);" + lineDelimiter);
		str = str.concat("}");
		return str;
	}

	private String generateFirstMethod(JDefinedClass c, String lineDelimiter) {
		String str = new String("public " + c.name() + " create" + c.name()
				+ "(){" + lineDelimiter);
		str = str.concat("\t return new " + c.name() + "();" + lineDelimiter);
		str = str.concat("}");
		return str;
	}

	private String generateObjectFactory(String lineDelimiter) {
		String str = new String("@XmlRegistry");
		str = str.concat(lineDelimiter);
		str = str.concat("public class ObjectFactory {");
		str = str.concat(lineDelimiter);
		str = str.concat(lineDelimiter);
		str = str.concat("}" + lineDelimiter);
		return str;
	}

	private HashMap<String, ElementClass> getReferencedClasses(JDefinedClass c,
			Map<String, ElementClass> schClasses, List<S2JJAXBModel> schModels) {

		HashMap<String, ElementClass> extraClasses = new HashMap<String, ElementClass>();

		for (JFieldVar fv : c.fields().values()) {
			System.out.println("Evaluating type: " + fv.type().fullName());
			if ((!schClasses.containsKey(fv.type().fullName()))
					&& (!extraClasses.containsKey(fv.type().fullName()))) {
				if (!alreadyExists(fv.type().fullName())) {
					ElementClass nc = getClassFromModels(fv.type(), schModels);
					if (nc != null) {
						extraClasses.put(nc.getjDefClass().fullName(), nc);
						System.out.println("Adding type: "
								+ fv.type().fullName());

					} else {
						System.out.println("Discarding type: "
								+ fv.type().fullName()
								+ " because type not found");
					}

				} else {
					System.out.println("Discarding type: "
							+ fv.type().fullName()
							+ " because type already exists");
				}
			} else {
				System.out.println("Discarding type: " + fv.type().fullName()
						+ " because type is going to be treated in the future");
			}

		}
		Iterator<JMethod> it = c.constructors();
		while (it.hasNext()) {
			JMethod m = it.next();
			for (JType t : m.listParamTypes()) {
				System.out.println("Evaluating type: " + t.fullName());
				if ((!t.isPrimitive())
						&& (!t.fullName().startsWith("java.lang"))
						&& (!schClasses.containsKey(t.fullName()))
						&& (!extraClasses.containsKey(t.fullName()))) {
					if (!alreadyExists(t.fullName())) {
						ElementClass nc = getClassFromModels(t, schModels);
						if (nc != null) {
							extraClasses.put(nc.getjDefClass().fullName(), nc);
							System.out.println("Adding type: " + t.fullName());
						} else {
							System.out.println("Discarding type: "
									+ t.fullName() + " because type not found");
						}
					} else {
						System.out.println("Discarding type: " + t.fullName()
								+ " because type already exists");
					}
				} else {
					System.out
							.println("Discarding type: "
									+ t.fullName()
									+ " because type is going to be treated in the future");
				}
			}
		}

		for (JMethod m : c.methods()) {
			for (JType t : m.listParamTypes()) {
				System.out.println("Evaluating type: " + t.fullName());
				if ((!t.isPrimitive())
						&& (!t.fullName().startsWith("java.lang"))
						&& (!schClasses.containsKey(t.fullName()))
						&& (!extraClasses.containsKey(t.fullName()))) {
					if (!alreadyExists(t.fullName())) {
						ElementClass nc = getClassFromModels(t, schModels);
						if (nc != null) {
							extraClasses.put(nc.getjDefClass().fullName(), nc);
							System.out.println("Adding type: " + t.fullName());
						} else {
							System.out.println("Discarding type: "
									+ t.fullName() + " because type not found");
						}
					} else {
						System.out.println("Discarding type: " + t.fullName()
								+ " because type already exists");
					}
				} else {
					System.out
							.println("Discarding type: "
									+ t.fullName()
									+ " because type is going to be treated in the future");
				}
			}

		}

		JClass t = c._extends();
		if ((t != null) && (!t.isPrimitive())
				&& (!t.fullName().startsWith("java.lang"))
				&& (!schClasses.containsKey(t.fullName()))
				&& (!extraClasses.containsKey(t.fullName()))) {
			if (!alreadyExists(t.fullName())) {
				ElementClass nc = getClassFromModels(t, schModels);
				if (nc != null) {
					extraClasses.put(nc.getjDefClass().fullName(), nc);
					System.out.println("Adding type: " + t.fullName());
				} else {
					System.out.println("Discarding type: " + t.fullName()
							+ " because type not found");
				}
			} else {
				System.out.println("Discarding type: " + t.fullName()
						+ " because type already exists");
			}
		} else {
			System.out.println("Discarding type: " + t.fullName()
					+ " because type is going to be treated in the future");
		}

		Iterator<JClass> it_impl = c._implements();
		if (it_impl != null) {
			while (it_impl.hasNext()) {
				JClass clss = it_impl.next();
				if ((clss != null) && (!clss.isPrimitive())
						&& (!clss.fullName().startsWith("java.lang"))
						&& (!schClasses.containsKey(clss.fullName()))
						&& (!extraClasses.containsKey(clss.fullName()))) {
					if (!alreadyExists(clss.fullName())) {
						ElementClass nc = getClassFromModels(clss, schModels);
						if (nc != null) {
							extraClasses.put(nc.getjDefClass().fullName(), nc);
							System.out.println("Adding type: "
									+ clss.fullName());
						} else {
							System.out.println("Discarding type: "
									+ clss.fullName()
									+ " because type not found");
						}
					} else {
						System.out.println("Discarding type: "
								+ clss.fullName()
								+ " because type already exists");
					}
				} else {
					System.out
							.println("Discarding type: "
									+ clss.fullName()
									+ " because type is going to be treated in the future");
				}
			}
		}

		return extraClasses;

	}

	private ElementClass getClassFromModels(JType type,
			List<S2JJAXBModel> schModels) {
		for (S2JJAXBModel m : schModels) {
			JCodeModel jcm = m.generateCode(null, null);
			JDefinedClass jdc = jcm._getClass(type.fullName());
			if (jdc != null) {
				for (Mapping map : m.getMappings()) {
					System.out.println("Evaluating Mapping (" + type.fullName()
							+ "): " + map.getElement().toString() + "-to-"
							+ map.getType().getTypeClass().fullName());
					if (map.getType().getTypeClass().fullName()
							.equals(type.fullName())) {
						System.out.println("Returning type");
						return new ElementClass(map.getElement(), jdc);
					}

				}
				return (new ElementClass(null, jdc));

			} else {
				System.out.println("Class not found for model" + m.toString());
			}

		}
		return null;
	}

	private Collection<ElementClass> getElementsFromModels(
			Map<String, ElementClass> list, List<S2JJAXBModel> schModels) {
		for (S2JJAXBModel m : schModels) {

			JCodeModel jcm = m.generateCode(null, null);
			for (Mapping map : m.getMappings()) {
				String s = map.getType().getTypeClass().fullName();
				JDefinedClass jdc = jcm._getClass(s);
				list.put(s, new ElementClass(map.getElement(), jdc));
			}
			JPackage pack = jcm.rootPackage();
			Iterator<JPackage> it = jcm.packages();
			if (pack == null) {
				if (it != null && it.hasNext()) {
					pack = it.next();
				} else
					pack = null;
			}
			while (pack != null) {
				Iterator<JDefinedClass> it_cl = pack.classes();
				while (it_cl.hasNext()) {
					JDefinedClass cls = it_cl.next();
					if (!cls.fullName().contains("ObjectFactory")) {
						if (list.containsKey(cls.fullName())) {
							list.put(cls.fullName(),
									new ElementClass(null, cls));
						}
					}

				}
				if (it != null && it.hasNext()) {
					pack = it.next();
				} else
					pack = null;
			}
		}
		return list.values();
	}

	private ElementClass getClassFromModels(JClass type,
			List<S2JJAXBModel> schModels) {
		for (S2JJAXBModel m : schModels) {
			for (Mapping map : m.getMappings()) {
				System.out.println("Evaluating Mapping (" + type.fullName()
						+ "): " + map.getElement().toString() + "-to-"
						+ map.getType().getTypeClass().fullName());
				if (map.getType().getTypeClass().fullName()
						.equals(type.fullName())) {
					JCodeModel jcm = m.generateCode(null, null);
					JDefinedClass jdc = jcm._getClass(type.fullName());
					if (jdc != null) {
						System.out.println("Returning type");
						return new ElementClass(map.getElement(), jdc);
					} else {
						System.out.println("Mapping found but not the class");
					}

				}

			}
		}
		return null;
	}

	private boolean alreadyExists(String fullName) {
		String localname = Signature.getSimpleName(fullName);
		String pack = Signature.getQualifier(fullName);
		IPackageFragment pf = pfr.getPackageFragment(pack);
		if (pf != null && pf.exists()) {
			ICompilationUnit cu = pf.getCompilationUnit(localname + ".java");
			if (cu != null && cu.exists()) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	/*
	 * public JFormatter write(JDefinedClass c, JFormatter f) {
	 * 
	 * // first collect all the types and identifiers Mode mode =
	 * Mode.COLLECTING; declared(f, c);
	 * 
	 * JPackage javaLang = c.owner()._package("java.lang");
	 * 
	 * // collate type names and identifiers to determine which types can be
	 * imported for (ReferenceList tl : collectedReferences.values()) { if
	 * (!tl.collisions(c) && !tl.isId()) { assert tl.getClasses().size() == 1;
	 * 
	 * // add to list of collected types
	 * importedClasses.add(tl.getClasses().get(0)); }
	 * 
	 * }
	 * 
	 * // the class itself that we will be generating is always accessible
	 * importedClasses.add(c);
	 * 
	 * // then print the declaration mode = Mode.PRINTING;
	 * 
	 * assert c.parentContainer().isPackage() :
	 * "this method is only for a pacakge-level class"; JPackage pkg =
	 * (JPackage) c.parentContainer(); if (!pkg.isUnnamed()) { f =
	 * f.nl().d(pkg); f = f.nl(); }
	 * 
	 * // generate import statements JClass[] imports = importedClasses
	 * .toArray(new JClass[importedClasses.size()]); Arrays.sort(imports); for
	 * (JClass clazz : imports) { // suppress import statements for primitive
	 * types, built-in types, // types in the root package, and types in // the
	 * same package as the current type if (!supressImport(clazz, c)) { f =
	 * f.p("import").p(clazz.fullName()).p(';').nl(); } } f = f.nl();
	 * 
	 * f = f.d(c); return f; }
	 * 
	 * 
	 * 
	 * private void declared(JFormatter f, JDefinedClass c) { String id =
	 * c.name(); if (collectedReferences.containsKey(id)) { if
	 * (!collectedReferences.get(id).getClasses().isEmpty()) { for (JClass type
	 * : collectedReferences.get(id) .getClasses()) { if (type.outer() != null)
	 * { collectedReferences.get(id).setId(false); return; } } }
	 * collectedReferences.get(id).setId(true); } else { // not a type, but we
	 * need to create a place holder to // see if there might be a collision
	 * with a type ReferenceList tl = new ReferenceList(); tl.setId(true);
	 * collectedReferences.put(id, tl); }
	 * 
	 * }
	 */

	private JFormatter createJavaSourceFileWriter(CodeWriter src,
			String className, JPackage pack) throws IOException {
		Writer bw = new BufferedWriter(
				src.openSource(pack, className + ".java"));
		return new JFormatter(new PrintWriter(bw));
	}

	final class ReferenceList {
		private final ArrayList<JClass> jclasses = new ArrayList<JClass>();

		/** true if this name is used as an identifier (like a variable name.) **/
		private boolean id;

		/**
		 * Returns true if the symbol represented by the short name is
		 * "importable".
		 */
		public boolean collisions(JDefinedClass enclosingClass) {
			// special case where a generated type collides with a type in
			// package java

			// more than one type with the same name
			if (jclasses.size() > 1)
				return true;

			// an id and (at least one) type with the same name
			if (id && jclasses.size() != 0)
				return true;

			for (JClass c : jclasses) {
				System.out.println("************* referenced class: "
						+ c.fullName());
				if (c._package() == javaLang) {
					// make sure that there's no other class with this name
					// within the same package
					Iterator itr = enclosingClass._package().classes();
					while (itr.hasNext()) {
						// even if this is the only "String" class we use,
						// if the class called "String" is in the same package,
						// we still need to import it.
						JDefinedClass n = (JDefinedClass) itr.next();
						if (n.name().equals(c.name()))
							return true; // collision
					}
				}
				if (c.outer() != null)
					return true; // avoid importing inner class to work around
									// 6431987. Also see jaxb issue 166
			}

			return false;
		}

		public void add(JClass clazz) {
			if (!jclasses.contains(clazz))
				jclasses.add(clazz);
		}

		public List<JClass> getClasses() {
			return jclasses;
		}

		public void setId(boolean value) {
			id = value;
		}

		/**
		 * Return true iff this is strictly an id, meaning that there are no
		 * collisions with type names.
		 */
		public boolean isId() {
			return id && jclasses.size() == 0;
		}
	}

	public void generateDummy(ServiceCoreElement element, String orchClassName,
			IProgressMonitor pm) throws JavaModelException {
		// IPackageFragmentRoot root =
		// project.getPackageFragmentRoot(generated);
		String packName = getPackageName(element);
		IPackageFragment pack = pfr.getPackageFragment(packName);
		if (pack == null || !pack.exists()) {
			pack = pfr.createPackageFragment(packName, true, pm);
		}
		String classname = getDummyClassName(element, orchClassName);
		ICompilationUnit cu = pack.getCompilationUnit(classname + ".java");
		if (cu == null || !cu.exists()) {
			cu = pack.createCompilationUnit(classname + ".java", "", true, pm);
		}
		cu.createPackageDeclaration(packName, pm);
		IType staticType = null;
		IType type = cu.getType(classname);
		if (type == null || !type.exists()) {
			type = cu.createType(generateDummyClassContent(classname), null,
					true, pm);
			staticType = type.createType(generateDummyStaticContent(), null,
					true, pm);
		} else {
			staticType = type.getType("Static");
			if (staticType == null || !staticType.exists()) {
				staticType = type.createType(generateDummyStaticContent(),
						null, true, pm);
			}
		}
		String methodContent = generateMethodContent(element);
		type.createMethod("public " + methodContent, null, true, pm);
		staticType.createMethod("public static " + methodContent, null, true,
				pm);

	}

	private String generateMethodContent(ServiceCoreElement element) {
		String str = new String(element.getReturnType() + " "
				+ element.getMethodName() + "(");
		boolean first = true;
		for (Parameter p : element.getParameters()) {
			if (first) {
				first = false;
			} else
				str = str.concat(", ");
			str = str.concat(p.getType() + " " + p.getName());
		}
		str = str.concat("){\n\n");
		if (element.getReturnTypeAsSignature().startsWith("Q"))
			str = str.concat("\t return null;");
		else if (element.getReturnTypeAsSignature().startsWith("Z"))
			str = str.concat("\t return false;");
		else if (element.getReturnTypeAsSignature().startsWith("V"))
			str = str.concat("");
		str = str.concat("\n}\n");
		return str;
	}

	private String generateDummyStaticContent() {
		String str = new String("public static class Static {\n}");
		return str;
	}

	private String generateDummyClassContent(String classname) {
		String str = new String("public class " + classname + "{\n}");
		return str;

	}

	private String getDummyClassName(ServiceCoreElement element,
			String orchClassName) {
		return element.getServiceName().substring(0, 1).toUpperCase()
				+ element.getServiceName().substring(1);
	}

	private String getPackageName(ServiceCoreElement element) {
		String namespace = element.getNamespace();
		
		String service = element.getServiceName()/* .toLowerCase() */;
		if (service.startsWith("."))
			service = service.substring(1);
		if (service.endsWith("."))
			service = service.substring(0, service.length()-1);
		
		String port = element.getPort()/*.toLowerCase() */;
		if (port.startsWith("."))
			port = port.substring(1);
		if (port.endsWith("."))
			port = port.substring(0, port.length()-1);

		int startIndex = namespace.indexOf("//www.");
		if (startIndex < 0) {
			startIndex = namespace.indexOf("http://");
			if (startIndex >= 0)
				startIndex += "http://".length();
			else
				startIndex = 0;
		} else
			startIndex += "//www.".length();

		namespace = namespace// .substring(0, namespace.indexOf(".xsd")) //
								// remove .xsd at the end
				.substring(startIndex) // remove http://www.
				.replace('/', '.') // replace / by .
				.replace('-', '.') // replace - by .
				.replace(':', '.'); // replace : by .
		if (namespace.startsWith("."))
			namespace = namespace.substring(1);
		if (namespace.endsWith("."))
			namespace = namespace.substring(0, namespace.length()-2);
				
		return  "dummy."+ namespace + '.' + service + '.' + port;
	}

}