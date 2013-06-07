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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public abstract class ServiceElement {

	private String methodName;
	private int methodModifier;
	private String returnType;
	private Map<String, String> constraints;
	private List<Parameter> parameters;
	private IMethod methodReference;

	/*public IMethod getMethodReference() {
		return methodReference;
	}*/

	public void setMethodReference(IMethod methodReference) {
		this.methodReference = methodReference;
	}

	public ServiceElement(String methodName, int modifier, String returnType,
			IMethod methodReference) {
		this.methodName = methodName;
		this.methodModifier = modifier;
		setReturnType(returnType);
		this.methodReference = methodReference;
		constraints = new HashMap<String, String>();
		parameters = new ArrayList<Parameter>();
	}

	public void setMethodModifier(int modifier) {
		this.methodModifier = modifier;

	}

	public ServiceElement() {

	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getMethodModifier() {
		return methodModifier;
	}

	/*
	 * public void setMethodModifier(String modifier) { this.methodModifier =
	 * modifier; }
	 */
	public String getReturnType() {
		String str = Signature.toString(this.returnType);
		System.out.println("Getting return type " + str + " as "
				+ this.returnType);
		return str;
	}

	public String getReturnTypeAsSignature() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = Signature.createTypeSignature(returnType, false);
		System.out.println("Setting return type " + returnType + " as "
				+ this.returnType);
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setConstraints(Map<String, String> constraints) {
		this.constraints = constraints;
	}

	public Map<String, String> getConstraints() {
		return constraints;
	}

	public abstract String getLabel();

	public IMethod generateElementMethodInType(IType t, boolean isInterface,
			IProgressMonitor arg0) throws JavaModelException {

		String signature = generateAnnotations(t, arg0);
		if (isInterface)
			signature = signature.concat(Flags.toString(Flags.AccPublic) + " ");
		else
			signature = signature.concat(Flags.toString(getMethodModifier()) + " ");
		
		String[] parameterTypes = new String[getParameters().size()];
		String[] parameterNames = new String[getParameters().size()];
		generateParametersAndReturn(t, parameterTypes, parameterNames, arg0);
		/*for (int i = 0; i < getParameters().size(); i++) {
			parameterNames[i] = getParameters().get(i).getName();
			parameterTypes[i] = getParameters().get(i).getTypeAsSignature();
			if (!parameterTypes[i].equals("Type.FILE")) {
				if (Signature.getTypeSignatureKind(parameterTypes[i]) == Signature.CLASS_TYPE_SIGNATURE) {
					if (!Signature.getQualifier(parameterTypes[i])
							.equalsIgnoreCase("java.lang")&& Signature.getQualifier(parameterTypes[i]).length()>0)) {
						if (t.getCompilationUnit().getImport(
								Signature.toString(parameterTypes[i])) == null) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else if (!t
								.getCompilationUnit()
								.getImport(
										Signature.toString(parameterTypes[i]))
								.exists()) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else
							System.out.println(Signature
									.toString(parameterTypes[i])
									+ " already exist in the import container");
					}
				}
			}
		}
		if (Signature.getTypeSignatureKind(getReturnTypeAsSignature()) == Signature.CLASS_TYPE_SIGNATURE) {
			if (!Signature.getQualifier(getReturnTypeAsSignature())
					.equalsIgnoreCase("java.lang")) {
				System.out.println("Return type: " + getReturnType());
				if (t.getCompilationUnit().getImport(getReturnType()) == null) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				} else if (!t.getCompilationUnit().getImport(getReturnType())
						.exists()) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				}
			}
		}*/

		String[] paramAnnotations = generateParametersAnnotations(t,
				parameterTypes, parameterNames, null, arg0);
		signature = signature.concat(createMethodSignature(
				getReturnTypeAsSignature(), this.getMethodName(),
				paramAnnotations, parameterTypes, parameterNames, true));
		if (isInterface) {
			signature = signature.concat(";");
		} else {
			signature = signature.concat("{\n\n}");
		}
		IMethod m = t.createMethod(signature, null, true, arg0);
		this.setMethodReference(m);
		return m;
	}

	public IMethod generateSimpleMethodInType(IType t, boolean isInterface, String methodCode,
			IProgressMonitor arg0) throws JavaModelException {
		String signature = new String();
		if (isInterface)
			signature = signature.concat(Flags.toString(Flags.AccPublic) + " ");
		else
			signature = signature.concat(Flags.toString(getMethodModifier()) + " ");
		String[] parameterTypes = new String[getParameters().size()];
		String[] parameterNames = new String[getParameters().size()];
		generateParametersAndReturn(t, parameterTypes, parameterNames, arg0);
		/*for (int i = 0; i < getParameters().size(); i++) {
			parameterNames[i] = getParameters().get(i).getName();
			parameterTypes[i] = getParameters().get(i).getTypeAsSignature();
			if (!parameterTypes[i].equals("Type.FILE")) {
				if (Signature.getTypeSignatureKind(parameterTypes[i]) == Signature.CLASS_TYPE_SIGNATURE) {
					System.out.println("Type qualifier for parameter " + parameterNames[i] +" is "+ Signature.getQualifier(parameterTypes[i]));
					if (!Signature.getQualifier(parameterTypes[i])
							.equalsIgnoreCase("java.lang")&& Signature.getQualifier(parameterTypes[i]).length()>0)  {
						if (t.getCompilationUnit().getImport(
								Signature.toString(parameterTypes[i])) == null) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else if (!t
								.getCompilationUnit()
								.getImport(
										Signature.toString(parameterTypes[i]))
								.exists()) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else
							System.out.println(Signature
									.toString(parameterTypes[i])
									+ " already exist in the import container");
					}
				}
			}
		}
		if (Signature.getTypeSignatureKind(getReturnTypeAsSignature()) == Signature.CLASS_TYPE_SIGNATURE) {
			System.out.println("Type qualifier for parameter return type is "+ Signature.getQualifier(getReturnTypeAsSignature()));
			if (!Signature.getQualifier(getReturnTypeAsSignature())
					.equalsIgnoreCase("java.lang")&& Signature.getQualifier(getReturnTypeAsSignature()).length()>0) {
				System.out.println("Return type: " + getReturnType());
				if (t.getCompilationUnit().getImport(getReturnType()) == null) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				} else if (!t.getCompilationUnit().getImport(getReturnType())
						.exists()) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				}
			}
		}*/
		signature = signature.concat(createMethodSignature(
				getReturnTypeAsSignature(), this.getMethodName(), null,
				parameterTypes, parameterNames, true));
		if (isInterface) {
			signature = signature.concat(";");
		} else {
			signature = signature.concat("{\n" + methodCode + "\n}");
		}
		IMethod m = t.createMethod(signature, null, true, arg0);
		this.setMethodReference(m);
		return m;
	}

	private void generateParametersAndReturn(IType t,String[] parameterTypes, String[] parameterNames, IProgressMonitor arg0 ) throws JavaModelException{
		
		for (int i = 0; i < getParameters().size(); i++) {
			parameterNames[i] = getParameters().get(i).getName();
			parameterTypes[i] = getParameters().get(i).getTypeAsSignature();
			if (!parameterTypes[i].equals("Type.FILE")) {
				if (Signature.getTypeSignatureKind(parameterTypes[i]) == Signature.CLASS_TYPE_SIGNATURE) {
					System.out.println("Type qualifier for parameter " + parameterNames[i] +" is "+ Signature.getQualifier(parameterTypes[i]));
					if (!Signature.getQualifier(parameterTypes[i])
							.equalsIgnoreCase("java.lang")&& Signature.getQualifier(parameterTypes[i]).length()>0)  {
						if (t.getCompilationUnit().getImport(
								Signature.toString(parameterTypes[i])) == null) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else if (!t
								.getCompilationUnit()
								.getImport(
										Signature.toString(parameterTypes[i]))
								.exists()) {
							t.getCompilationUnit().createImport(
									Signature.toString(parameterTypes[i]),
									null, Flags.AccDefault, arg0);
						} else
							System.out.println(Signature
									.toString(parameterTypes[i])
									+ " already exist in the import container");
					}
				}
			}
		}
		if (Signature.getTypeSignatureKind(getReturnTypeAsSignature()) == Signature.CLASS_TYPE_SIGNATURE) {
			System.out.println("Type qualifier for parameter return type is "+ Signature.getQualifier(getReturnTypeAsSignature()));
			if (!Signature.getQualifier(getReturnTypeAsSignature())
					.equalsIgnoreCase("java.lang")&& Signature.getQualifier(getReturnTypeAsSignature()).length()>0) {
				System.out.println("Return type: " + getReturnType());
				if (t.getCompilationUnit().getImport(getReturnType()) == null) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				} else if (!t.getCompilationUnit().getImport(getReturnType())
						.exists()) {
					t.getCompilationUnit().createImport(getReturnType(), null,
							Flags.AccDefault, arg0);
				}
			}
		}
	}
	
	public String generateReturnCode() {
		if (getReturnTypeAsSignature().startsWith("Q"))
			return ("\t return null;");
		else if (getReturnTypeAsSignature().startsWith("Z"))
			return ("\t return false;");
		else if (getReturnTypeAsSignature().startsWith("V"))
			return ("");
		else
			return ("\t return 0;");
	}

	protected abstract String[] generateParametersAnnotations(IType t,
			String[] parameterTypes, String[] parameterNames,
			String[] paramAnnotations, IProgressMonitor arg0)
			throws JavaModelException;

	protected abstract String generateAnnotations(IType t, IProgressMonitor arg0)
			throws JavaModelException;

	private String createMethodSignature(String returnTypeSignature,
			String methodName, String[] paramAnnotations,
			String[] parameterTypes, String[] parameterNames, boolean b)
			throws JavaModelException {

		String str = new String();
		if (returnTypeSignature != null) {
			if (b) {
				str = str.concat(Signature
						.getSignatureSimpleName(returnTypeSignature) + " ");
			} else {
				str = str.concat(Signature.toString(returnTypeSignature) + " ");
			}
		}
		if (methodName != null) {
			str = str.concat(methodName + "(");
			if (parameterTypes != null && parameterNames != null) {
				if (parameterNames.length == parameterTypes.length) {
					for (int i = 0; i < parameterTypes.length; i++) {
						if (i != 0) {
							str = str.concat(", ");
						}
						if (paramAnnotations != null) {
							str = str.concat(paramAnnotations[i] + " ");
						}
						if (!parameterTypes[i].equals("Type.FILE")) {
							if (b) {
								str = str
										.concat(Signature
												.getSignatureSimpleName(parameterTypes[i])
												+ " ");
							} else {
								str = str.concat(Signature
										.toString(parameterTypes[i]) + " ");
							}
						} else {
							if (b) {
								str = str.concat("String ");
							} else {
								str = str.concat("java.lang.String ");
							}
						}
						str = str.concat(parameterNames[i]);

					}
				} else {
					throw new JavaModelException(
							new Exception(
									"Method number of method types and names are incorrect"),
							IJavaModelStatus.ERROR);
				}
			}
			str = str.concat(")");
		} else {
			throw new JavaModelException(new Exception("Method name is null"),
					IJavaModelStatus.ERROR);
		}
		return str;
	}
	

}
