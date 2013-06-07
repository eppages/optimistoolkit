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

package es.bsc.servicess.ide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.StringLiteral;

/**Class for making operations with Service Element constraints.
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class ConstraintsUtils {
	
	/* Element constraint definitions*/
	public static final ConstraintDef PROC_ARCH = new ConstraintDef(
			"processorArchitecture", ConstraintDef.STRING);
	public static final ConstraintDef PROC_CPU_COUNT = new ConstraintDef(
			"processorCPUCount", ConstraintDef.INT);
	public static final ConstraintDef PROC_SPEED = new ConstraintDef(
			"processorSpeed", ConstraintDef.FLOAT); // in GHz
	public static final ConstraintDef MEM_SIZE = new ConstraintDef(
			"memoryPhysicalSize", ConstraintDef.FLOAT); // in GB
	public static final ConstraintDef STORAGE_SIZE = new ConstraintDef(
			"storageElemSize", ConstraintDef.FLOAT); // in GB
	public static final ConstraintDef OS = new ConstraintDef(
			"operatingSystemType", ConstraintDef.STRING);
	public static final ConstraintDef SOFTWARE = new ConstraintDef(
			"appSoftware", ConstraintDef.STRING);
	public static final ConstraintDef LICENSE = new ConstraintDef(
			"licenseTokens", ConstraintDef.STRING);
	public static final ConstraintDef ENC_STORAGE = new ConstraintDef(
			"protectedStorageSpace", ConstraintDef.FLOAT);
	public static final ConstraintDef SHARED_STORAGE = new ConstraintDef(
			"sharedStorageSpace", ConstraintDef.FLOAT);
	
	/**Array for storing all the element constraints supported by the plug-in. */
	public static final ConstraintDef[] SUPPORTED_CONS = { PROC_ARCH,
			PROC_CPU_COUNT, PROC_SPEED, MEM_SIZE, STORAGE_SIZE, OS, SOFTWARE,
			LICENSE, ENC_STORAGE, SHARED_STORAGE };
	/**Array for storing all the element constraints whose values have to be 
	 * scaled with the number of concurrent executions. */
	public static final List<String> SCALABLE_CONS = Arrays
			.asList(new String[] { PROC_CPU_COUNT.getName(),
					MEM_SIZE.getName(), STORAGE_SIZE.getName(), 
					ENC_STORAGE.getName(), SHARED_STORAGE.getName() });
	
	/**Array for storing all the element constraints whose values can be uncompatible. */
	public static final List<String> INCOMPATIBLE_CONS = Arrays
			.asList(new String[] { PROC_ARCH.getName(),OS.getName()});
	
	/*If a new element constraint is implemented add here its definition and include 
	 * it in the SUPPORTED_CONS array and SCALABLE_CONS if it must be scaled with 
	 * the number of concurrent executions or INCOMPATIBLE if its values can produce a 
	 * incompatibility */
		

	private static Logger log = Logger.getLogger(ConstraintsUtils.class);
	
	/** Check the consistency between the constraints values of the elements in a package.
	 *
	 * @param constraints Set of element constraints belonging to a package
	 * @return true if element constraints are compatible, false if not. 
	 */
	public static boolean checkConsistency(Set<Map<String, String>> constraints) {
		/* Currently, it checks if all the Architecture or OS constraints are the same 
		 * for each element in the package.
		 * If a new constraint is added to the plug-in and the user can not define 
		 * different values of this constraint in the elements of the package, it must be
		 * added to INCOMPATIBLE_CONS set */
	 	String previous_Cons = null;
	 	for (Map<String, String> m : constraints) {
	 		for (String consName:INCOMPATIBLE_CONS){	
	 			String current_Cons = m.get(consName);
	 			if (previous_Cons != null && previous_Cons.trim().length()>0) {
	 				if (current_Cons != null && current_Cons.trim().length()>0
	 						&& !current_Cons.equalsIgnoreCase(previous_Cons)) {
	 					log.error("Incompatible types for " + consName +"("+previous_Cons.trim()+"!="+ current_Cons.trim()+")");
	 					return false;
	 				}
	 			} else {
	 				if (current_Cons != null && current_Cons.trim().length()>0) {
	 					previous_Cons = current_Cons;
	 				}
	 			}
	 		}
		}
		return true;
	}

	/** Get the maximum constraints of the different elements of a package. 
	 * @param constraints Set of Element constraints of the package
	 * @return maximum values for each of the constraints
	 */
	public static Map<String, String> getMaxConstraints(
			Set<Map<String, String>> constraints) {
		/* If new constraints are implemented. Add in this function
		 *  the treatement of maximum value of the new constraint
		 */
		Map<String, String> max = new HashMap<String, String>();
		if (constraints != null && constraints.size() > 0) {
			for (ConstraintDef d : SUPPORTED_CONS) {
				String previous_Value = null;
				for (Map<String, String> m : constraints) {
					String current_Value = m.get(d.getName());
					if (current_Value != null) {
						if (previous_Value != null) {
							if (d.getName()
									.equalsIgnoreCase(SOFTWARE.getName())
									|| d.getName().equalsIgnoreCase(
											LICENSE.getName())) {
								previous_Value = "\""
										+ previous_Value.replaceAll("\"", "")
										+ ", "
										+ current_Value.replaceAll("\"", "")
										+ "\"";
							} else {
								try {
									switch (d.getType()) {
									case ConstraintDef.INT:
										if (Integer.parseInt(current_Value) > Integer
												.parseInt(previous_Value)) {
											previous_Value = current_Value;
											log.debug("Setting value for "
															+ d.getName()
															+ " to "
															+ previous_Value);
										}
										break;
									case ConstraintDef.FLOAT:
										if (Float.parseFloat(current_Value) > Float
												.parseFloat(previous_Value)) {
											previous_Value = current_Value;
											log.debug("Setting value for "
															+ d.getName()
															+ " to "
															+ previous_Value);
										}
										break;
									default:
										previous_Value = current_Value;
										log.debug("Setting value for "
												+ d.getName() + " to "
												+ previous_Value);
										break;
									}
								} catch (NumberFormatException e) {
									log.error("Error reading number");
								}
							}
						} else {
							try {
								switch (d.getType()) {
								case ConstraintDef.INT:
									previous_Value = new Integer(current_Value)
											.toString();
									log.debug("Setting value for "
											+ d.getName() + " to "
											+ previous_Value);
									break;
								case ConstraintDef.FLOAT:
									previous_Value = new Float(current_Value)
											.toString();
									log.debug("Setting value for "
											+ d.getName() + " to "
											+ previous_Value);
									break;
								default:
									previous_Value = current_Value;
									log.debug("Setting value for "
											+ d.getName() + " to "
											+ previous_Value);
									break;
								}
								if (d.getName().equalsIgnoreCase(OS.getName())
										|| d.getName().equalsIgnoreCase(
												PROC_ARCH.getName())) {
									break;
								}
							} catch (NumberFormatException e) {
								log.error("Error reading number");
							}
						}
					}
				}
				if (previous_Value != null) {
					max.put(d.getName(), previous_Value);
					log.debug("Setting final value for " + d.getName()
							+ " to " + previous_Value);
				}
			}
		}
		return max;
	}

	/** Looks for the value type of an element constraint between all the supported constraints.
	 * @param constraintName Name of the element constraint to find their datatype
	 * @return datatype of the constraint
	 */
	public static int getConstraintType(String constraintName) {
		for (ConstraintDef d : SUPPORTED_CONS) {
			if (d.getName().equalsIgnoreCase(constraintName)) {
				return d.getType();
			}
		}
		return -1;
	}

	/** Get the minimum constraints for a Service Element.
	 * The process all the constraint of a Service Element increasing those constraints
	 * which require more resources when the number of concurrent element is increased.  
	 * @param constraints
	 * @param minNumber
	 * @return
	 */
	public static Map<String, String> getMinConstraints(
			Map<String, String> constraints, int minNumber) {
		Map<String, String> newConstraints = new HashMap<String, String>();
		for (Entry<String, String> e : constraints.entrySet()) {
			if (SCALABLE_CONS.contains(e.getKey())) {
				switch (getConstraintType(e.getKey())) {
				case ConstraintDef.INT:
					int iValue = Integer.parseInt(e.getValue()) * minNumber;
					newConstraints.put(e.getKey(), Integer.toString(iValue));
					break;

				case ConstraintDef.FLOAT:
					float fValue = Float.parseFloat(e.getValue()) * minNumber;
					newConstraints.put(e.getKey(), Float.toString(fValue));
					break;
				default:
					newConstraints.put(e.getKey(), e.getValue());
					break;
				}
			} else {
				newConstraints.put(e.getKey(), e.getValue());
			}
		}
		return newConstraints;
	}

	/**Get the names of the constraints supported by the Plug-in
	 * @return A list of constraint names
	 */
	public static List<String> getSupportedConstraintNames() {
		ArrayList<String> contraintNames = new ArrayList<String>();
		for (ConstraintDef d : SUPPORTED_CONS) {
			contraintNames.add(d.getName());
		}
		return contraintNames;
	}
	
	/* Not currently used
	public static List<String> getConstraintsValuesFromAnnotationClass(
			IJavaProject project) throws JavaModelException {
		System.out.println("Getting Constraints Values");
		ArrayList<String> vals = new ArrayList<String>();
		IType cons = project
				.findType("integratedtoolkit.types.annotations.Constraints");
		if (cons != null) {
			System.out.println("Type found " + cons.getFullyQualifiedName());
			IMethod[] fields = cons.getMethods();
			cons.getMethods();
			if (fields != null) {
				System.out.println(fields.length + " fields found");
				for (IMethod f : fields) {
					System.out.println("Adding field " + f.getElementName());
					vals.add(f.getElementName());
				}
			} else {
				System.err.println("Constraints type not found");
				throw new JavaModelException(new Exception(
						"No fields found in type"), 1);
			}
		} else {
			System.err.println("Constraints type not found");
			throw new JavaModelException(new Exception(
					"Constraints type not found"), 1);
		}
		return vals;
	}*/

	/**Create a Expression from the value of an element constraint. 
	 * @param constraintName Name of the constraint
	 * @param constraintValue Value of the constraint
	 * @param ast AST node where the expression has to be created
	 * @return Expression with the constraint value
	 */
	public static Expression convertValueToExpression(String constraintName,
			String constraintValue, AST ast) {
		Expression convertedValue;
		switch (getConstraintType(constraintName)) {
		case ConstraintDef.INT:
			convertedValue = ast.newNumberLiteral(constraintValue);
			break;
		case ConstraintDef.FLOAT:
			convertedValue = ast.newNumberLiteral(constraintValue + "f");
			break;
		default:
			StringLiteral tmp = ast.newStringLiteral();
			tmp.setLiteralValue(constraintValue);
			convertedValue = tmp;
			break;
		}
		return convertedValue;
	}

	/** Create the string in the format required by constraints annotation of the Core Element Interface.
	 * 
	 * Add comas, float simbols, etc...
	 * 
	 * @param constraintName Name of the constraint
	 * @param constraintValue Value of the constraint
	 * @return constraint value in the annotation format
	 */
	public static String convertValueToAnnotationString(String constraintName,
			String constraintValue) {
		String convertedValue;
		switch (getConstraintType(constraintName)) {
		case ConstraintDef.STRING:
			convertedValue = "\"" + constraintValue + "\"";
			break;
		case ConstraintDef.FLOAT:
			convertedValue = constraintValue + "f";
			break;
		default:
			convertedValue = constraintValue;
			break;
		}
		return convertedValue;
	}

	/**Update the constraints which can produce incompatibilities.
	 * @param typeConsMap Map of package types and their constraints
	 * @param pack Package name
	 * @param constraints New constraints
	 */
	public static void updateCompatibleConstraints(
			HashMap<String, HashMap<String, String>> typeConsMap, String pack,
			Map<String, String> constraints) {
		Map<String, String> packConstraints = typeConsMap.get(pack);
		for (String consName:INCOMPATIBLE_CONS){	
 			String current_Cons = constraints.get(consName);
 			if (current_Cons != null){
 				packConstraints.put(consName, current_Cons);
 			}
		}
	}

	public static int checkMaxResourceProperties(
			Map<String, String> constraints, Map<String, String> maxValues, int minNumber) {
		for (String consName:SCALABLE_CONS){
			String consValue = constraints.get(consName);
			String maxValue = maxValues.get(consName);
			if (consValue != null && consValue.length()>0 && maxValue != null && maxValue.length()>0 ){
				switch (getConstraintType(consName)) {
					case ConstraintDef.INT:
						float iMaxValue = Float.parseFloat(maxValue);
						for (int i=1; i<=minNumber; i++){
							int newMinNumber = minNumber/i;
							float fValue = Integer.parseInt(consValue) * newMinNumber;
							if (iMaxValue >= fValue){
								minNumber = newMinNumber;
								break;
							}
						}
						break;
					case ConstraintDef.FLOAT:
						float fMaxValue = Float.parseFloat(maxValue);
						for (int i=1; i<=minNumber; i++){
							int newMinNumber = minNumber/i;
							float fValue = Float.parseFloat(consValue) * newMinNumber;
							if (fMaxValue >= fValue){
								minNumber = newMinNumber;
								break;
							}
						}
						break;
					default:
						break;
				}
			}
		}
		return minNumber;
	}

}
