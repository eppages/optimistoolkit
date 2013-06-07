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

package es.bsc.servicess.ide.model;

import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;

import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;

public class MethodCoreElement extends ServiceElement {
	private String declaringClass;
	/** isModifier method property, the object of the method is not modified **/
	private boolean modifier;
	/**
	 * isInit method property, task scheduling is round robin it is used for
	 * allocation
	 **/
	private boolean init;
	
	private static Logger log = Logger.getLogger(MethodCoreElement.class);
	
	public String getOLDLabel() {
		String str = new String(getMethodName() + "(");
		boolean first = true;
		for (Parameter p : getParameters()) {
			if (first) {
				first = false;
			} else {
				str = str.concat(", ");
			}
			if (p.getType().equalsIgnoreCase(CoreElementParameter.FILE))
				str = str.concat("java.lang.String");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.STRING))
				str = str.concat("java.lang.String");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.CHAR))
				str = str.concat("char");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BYTE))
				str = str.concat("byte");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BOOLEAN))
				str = str.concat("boolean");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.SHORT))
				str = str.concat("short");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.INT))
				str = str.concat("int");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.LONG))
				str = str.concat("long");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.DOUBLE))
				str = str.concat("double");
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.FLOAT))
				str = str.concat("float");
			else
				str = str.concat(p.getType());
		}
		str = str.concat(")" + getDeclaringClass());
		return str;
	}

	public String getLabel() {
		String str = new String(getMethodName() + "(");
		boolean first = true;
		for (Parameter p : getParameters()) {
			if (first) {
				first = false;
			} else {
				str = str.concat(",");
			}
			if (p.getType().equalsIgnoreCase(CoreElementParameter.FILE))
				str = str.concat(CoreElementParameter.FILE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.STRING))
				str = str.concat(CoreElementParameter.STRING_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.CHAR)
					|| p.getType().equals("char"))
				str = str.concat(CoreElementParameter.CHAR_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BYTE)
					|| p.getType().equals("byte"))
				str = str.concat(CoreElementParameter.BYTE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.BOOLEAN)
					|| p.getType().equals("boolean"))
				str = str.concat(CoreElementParameter.BOOLEAN_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.SHORT)
					|| p.getType().equals("short"))
				str = str.concat(CoreElementParameter.SHORT_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.INT)
					|| p.getType().equals("int"))
				str = str.concat(CoreElementParameter.INT_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.LONG)
					|| p.getType().equals("long"))
				str = str.concat(CoreElementParameter.LONG_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.DOUBLE)
					|| p.getType().equals("double"))
				str = str.concat(CoreElementParameter.DOUBLE_T);
			else if (p.getType().equalsIgnoreCase(CoreElementParameter.FLOAT)
					|| p.getType().equals("float"))
				str = str.concat(CoreElementParameter.FLOAT_T);
			else
				str = str.concat(CoreElementParameter.OBJECT_T);
		}
		str = str.concat(")" + getDeclaringClass());
		return str;
	}

	public MethodCoreElement(String methodName, int modifier,
			String returnType, IMethod method, String declaringClass,
			boolean isModifier, boolean init) {
		super(methodName, modifier, returnType, method);
		this.declaringClass = declaringClass;
		this.modifier = isModifier;
		this.init = init;
	}

	public MethodCoreElement(String elementName, int flags, String returnType,
			IMethod method) {
		super(elementName, flags, returnType, method);
		this.declaringClass = "";
		this.modifier = true;
		this.init = false;
	}

	public String getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(String declaringClass) {
		this.declaringClass = declaringClass;
	}

	public boolean isModifier() {
		return modifier;
	}

	public void setModifier(boolean modifier) {
		this.modifier = modifier;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	@Override
	protected String[] generateParametersAnnotations(IType t,
			String[] parameterTypes, String[] parameterNames,
			String[] currentParamAnnotations, IProgressMonitor arg0)
			throws JavaModelException {

		String[] paramAnnotations = null;
		if (parameterNames != null) {
			if (parameterNames.length == getParameters().size()) {
				String[] paramDir = new String[getParameters().size()];
				for (int i = 0; i < getParameters().size(); i++) {
					CoreElementParameter p = (CoreElementParameter) getParameters()
							.get(i);
					paramDir[i] = p.getDirection();
				}
				if (t.getCompilationUnit().getImport(
						"integratedtoolkit.types.annotations.Parameter") == null) {
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter",
							null, Flags.AccDefault, arg0);
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter.*",
							null, Flags.AccDefault, arg0);
				} else if (!t
						.getCompilationUnit()
						.getImport(
								"integratedtoolkit.types.annotations.Parameter")
						.exists()) {
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter",
							null, Flags.AccDefault, arg0);
					t.getCompilationUnit().createImport(
							"integratedtoolkit.types.annotations.Parameter.*",
							null, Flags.AccDefault, arg0);
				} else {
					log.debug("Parameter already exists in the import container");
				}
				paramAnnotations = new String[parameterNames.length];
				if (currentParamAnnotations == null) {
					for (int i = 0; i < parameterNames.length; i++) {
						if (parameterTypes[i].startsWith("Type.")) {
							paramAnnotations[i] = "@Parameter(type = "
									+ parameterTypes[i]
									+ ", direction = Direction." + paramDir[i]
									+ ") ";
						} else {
							paramAnnotations[i] = "@Parameter(direction = Direction."
									+ paramDir[i] + ") ";
						}
					}
				} else if (currentParamAnnotations.length == parameterNames.length) {
					for (int i = 0; i < parameterNames.length; i++) {
						if (parameterTypes[i].startsWith("Type.")) {
							paramAnnotations[i] = "@Parameter(type = "
									+ parameterTypes[i]
									+ ", direction = Direction." + paramDir[i]
									+ ") " + currentParamAnnotations[i];
						} else {
							paramAnnotations[i] = "@Parameter(direction = Direction."
									+ paramDir[i]
									+ ") "
									+ currentParamAnnotations[i];
						}
					}
				} else
					throw new JavaModelException(
							new Exception(
									"Method number of annotions and names are different"),
							IJavaModelStatus.ERROR);
			} else
				throw new JavaModelException(new Exception(
						"Method number of directions and names are different"),
						IJavaModelStatus.ERROR);
		}
		return paramAnnotations;
	}

	@Override
	protected String generateAnnotations(IType t, IProgressMonitor arg0)
			throws JavaModelException {
		@SuppressWarnings("restriction")
		String lineDelimiter = StubUtility.getLineDelimiterUsed(t
				.getJavaProject());
		String str = new String();

		if (this.getConstraints().size() > 0) {
			if (t.getCompilationUnit().getImport(
					"integratedtoolkit.types.annotations.Constraints") == null) {
				t.getCompilationUnit().createImport(
						"integratedtoolkit.types.annotations.Constraints",
						null, Flags.AccDefault, arg0);
			} else if (!t
					.getCompilationUnit()
					.getImport(
							"integratedtoolkit.types.annotations.Constraints")
					.exists()) {
				t.getCompilationUnit().createImport(
						"integratedtoolkit.types.annotations.Constraints",
						null, Flags.AccDefault, arg0);
			} else {
				log.debug("Orchestration already exists in the import container");
			}
			str = str.concat("@Constraints");
			boolean first = true;
			for (Entry<String, String> e : this.getConstraints().entrySet()) {
				if (first) {
					str = str.concat("("
							+ e.getKey()
							+ " = "
							+ ConstraintsUtils.convertValueToAnnotationString(e.getKey(),
									e.getValue()));
					first = false;
				} else {
					str = str.concat(", "
							+ e.getKey()
							+ " = "
							+ ConstraintsUtils.convertValueToAnnotationString(e.getKey(),
									e.getValue()));
				}
			}
			str = str.concat(")" + lineDelimiter);
		}
		if (t.getCompilationUnit().getImport(
				"integratedtoolkit.types.annotations.Method") == null) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Method", null,
					Flags.AccDefault, arg0);
		} else if (!t.getCompilationUnit()
				.getImport("integratedtoolkit.types.annotations.Method")
				.exists()) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Method", null,
					Flags.AccDefault, arg0);
		} else {
			log.debug("Method already exists in the import container");
		}
		str = str.concat("@Method(declaringClass = \"" + declaringClass
				+ "\", isModifier = " + modifier + ", isInit = " + init + ")"
				+ lineDelimiter);

		return str;
	}

}
