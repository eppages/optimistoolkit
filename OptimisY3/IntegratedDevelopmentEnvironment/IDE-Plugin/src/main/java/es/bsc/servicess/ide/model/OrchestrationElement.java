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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebMethod;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;

import es.bsc.servicess.ide.ConstraintsUtils;
import es.bsc.servicess.ide.Logger;
import es.bsc.servicess.ide.TitlesAndConstants;

public class OrchestrationElement extends ServiceElement {
	boolean isPartOfServiceItf;
	String serviceClass;
	private static Logger log = Logger.getLogger(OrchestrationElement.class);

	public OrchestrationElement(String methodName, int modifier,
			String returnType, IMethod method, String serviceClass, boolean isPartOfServiceItf) {
		super(methodName, modifier, returnType, method);
		this.isPartOfServiceItf = isPartOfServiceItf;
		this.serviceClass= serviceClass;
	}

	public OrchestrationElement(String methodName, int modifier,
			String returnType, IMethod method, String serviceClass) {
		super(methodName, modifier, returnType, method);
		this.isPartOfServiceItf = false;
		this.serviceClass= serviceClass;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	public boolean isPartOfServiceItf() {
		return isPartOfServiceItf;
	}

	public void setPartOfServiceItf(boolean isPartOfServiceItf) {
		this.isPartOfServiceItf = isPartOfServiceItf;
	}

	public String getLabel() {
		String str = new String(getMethodName() + "(");
		boolean first = true;
		for (Parameter p : getParameters()) {
			if (first) {
				first = false;
			} else {
				str = str.concat(", ");
			}
			str = str.concat(p.getType());
		}
		str = str.concat(")"+serviceClass);
		return str;
	}
	
	public static List<String> getParametersTypeFromLabel(String label) throws Exception{
		int begin = label.indexOf("(");
		int end = label.indexOf(")");
		if (begin>0 && end>0 && end>begin){
			String parsString = label.substring(begin+1,end);
			List<String> parameters = new LinkedList<String>();
			if(parsString!=null && parsString.trim().length()>0){
				String[] parametersArray = parsString.split(", ");
				if (parametersArray!=null && parametersArray.length>0){
					for (String parameter:parametersArray){
							parameters.add(parameter);
						
					}
				}
			}
			return parameters ;
		}else
			throw(new Exception("Error incorrect label "+ label));
	}
	
	public static String getMethodNamefromLabel(String label) throws Exception{
		int i = label.indexOf("(");
		if (i>0)
			return label.substring(0,i);
		else
			throw(new Exception("Error method name from label "+ label));
	}
	
	public static String getClassFromLabel(String label) throws Exception{
		int i = label.indexOf(")");
		if (i>0)
			return label.substring(i+1);
		else
			throw(new Exception("Error getting class from label "+ label));
	}
	
	public static String[] getClassesFromLabels(String[] labels) throws Exception{
		if (labels!=null&& labels.length>0){
			String[] classes = new String[labels.length];
			for (int i=0; i<labels.length; i++){
				classes[i] = getClassFromLabel(labels[i]);
			}
			return classes;
		}else
			return new String[0];
	}
	
	

	protected String[] generateParametersAnnotations(IType t,
			String[] parameterTypes, String[] parameterNames,
			String[] currentParamAnnotations, IProgressMonitor arg0)
			throws JavaModelException {
		String[] paramAnnotations = null;
		if (isPartOfServiceItf()) {
			if (parameterNames != null) {
				if (t.getCompilationUnit().getImport("javax.jws.WebParam") == null) {
					t.getCompilationUnit().createImport("javax.jws.WebParam",
							null, Flags.AccDefault, arg0);
				} else if (!t.getCompilationUnit()
						.getImport("javax.jws.WebParam").exists()) {
					t.getCompilationUnit().createImport("javax.jws.WebParam",
							null, Flags.AccDefault, arg0);
				} else {
					log.debug("javax.jws.WebMethod already exists in the import container");
				}
				paramAnnotations = new String[parameterNames.length];
				if (currentParamAnnotations == null) {
					for (int i = 0; i < parameterNames.length; i++) {
						paramAnnotations[i] = "@WebParam(name=\""
								+ parameterNames[i] + "\")";
					}
				} else if (currentParamAnnotations.length == parameterNames.length) {
					for (int i = 0; i < parameterNames.length; i++) {
						paramAnnotations[i] = "@WebParam(name=\""
								+ parameterNames[i] + "\")"
								+ currentParamAnnotations[i];
					}
				} else
					throw new JavaModelException(
							new Exception(
									"Method number of annotions and names are different"),
							IJavaModelStatus.ERROR);
			}
		}
		return paramAnnotations;
	}

	protected String generateAnnotations(IType t, IProgressMonitor arg0)
			throws JavaModelException {
		@SuppressWarnings("restriction")
		String lineDelimiter = StubUtility.getLineDelimiterUsed(t
				.getJavaProject());
		String str = new String();
		if (this.isPartOfServiceItf()) {
			if (t.getCompilationUnit().getImport("javax.jws.WebMethod") == null) {
				t.getCompilationUnit().createImport("javax.jws.WebMethod",
						null, Flags.AccDefault, arg0);
			} else if (!t.getCompilationUnit().getImport("javax.jws.WebMethod")
					.exists()) {
				t.getCompilationUnit().createImport("javax.jws.WebMethod",
						null, Flags.AccDefault, arg0);
			} else {
				log.debug("javax.jws.WebMethod already exists in the import container");
			}
			str = str.concat("@WebMethod" + lineDelimiter);
		}
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
				"integratedtoolkit.types.annotations.Orchestration") == null) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Orchestration", null,
					Flags.AccDefault, arg0);
		} else if (!t.getCompilationUnit()
				.getImport("integratedtoolkit.types.annotations.Orchestration")
				.exists()) {
			t.getCompilationUnit().createImport(
					"integratedtoolkit.types.annotations.Orchestration", null,
					Flags.AccDefault, arg0);
		} else {
			log.debug("Orchestration already exists in the import container");
		}
		str = str.concat("@Orchestration" + lineDelimiter);
		return str;
	}

	public static OrchestrationElement extractElement(String label,
			int modifier, String return_type, IType orchClass) throws Exception {
		String methodName = OrchestrationElement.getMethodNamefromLabel(label);
		List<String> parTypes = OrchestrationElement.getParametersTypeFromLabel(label);
		IMethod[] methods = orchClass.getMethods();
		for (IMethod method:methods){
			if (method.getElementName().equals(methodName)){
				if (method.getFlags()==modifier){
					if (Signature.toString(method.getReturnType()).equals(return_type)){
						String[] types = method.getParameterTypes();
						if(parTypes.size()==types.length){
							boolean same = true;
							for(int i=0; i<types.length; i++)
								if (!Signature.toString(types[i]).equals(parTypes.get(i))){
									same =false;
									break;
							}
							if (same){
								OrchestrationElement oe = new OrchestrationElement(
										methodName, modifier, return_type, method, 
										orchClass.getFullyQualifiedName());
								String[] paramNames = method.getParameterNames();
								for (int i = 0; i < types.length; i++) {
									Parameter p = new Parameter(
											Signature.toString(types[i]), paramNames[i]);
									oe.getParameters().add(p);
								}
								return oe;
							}
						}
					}
				}
			}
		}
		throw new Exception("Method " + label +" in " + orchClass.getFullyQualifiedName()+" not found");
		
	}
	
	

}
