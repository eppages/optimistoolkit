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

/**Class for modeling a Service Element constraint in the plug-in.
 * 
 * @author Jorge Ejarque (Barcelona Supercomputing center)
 *
 */
public class ConstraintDef {
	public static final int STRING = 0;
	public static final int INT = 1;
	public static final int FLOAT = 2;

	private String name;
	private int type;

	/** Class Constructor.
	 * @param name Name of the constraint
	 * @param type Datatype of the constraint value 
	 */

	public ConstraintDef(String name, int type) {
		this.name = name;
		this.type = type;
	}

	/**Get the datatype of the Constraint.
	 * @return datatype.
	 */
	public int getType() {
		return type;
	}

	/**Get the name of the constraint.
	 * @return constraint name
	 */
	public String getName() {
		return name;
	}
}
