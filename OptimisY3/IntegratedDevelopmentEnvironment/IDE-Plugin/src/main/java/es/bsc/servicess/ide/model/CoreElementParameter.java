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

public class CoreElementParameter extends Parameter {
	private String direction;

	public final static String FILE = "Type.FILE";
	public final static String FILE_T = "FILE_T";
	public final static String BOOLEAN = "Type.BOOLEAN";
	public final static String BOOLEAN_T = "BOOLEAN_T";
	public final static String STRING = "Type.STRING";
	public final static String STRING_T = "STRING_T";
	public final static String CHAR = "Type.CHAR";
	public final static String CHAR_T = "CHAR_T";
	public final static String BYTE = "Type.BYTE";
	public final static String BYTE_T = "BYTE_T";
	public final static String SHORT = "Type.SHORT";
	public final static String SHORT_T = "SHORT_T";
	public final static String INT = "Type.INT";
	public final static String INT_T = "INT_T";
	public final static String LONG = "Type.LONG";
	public final static String LONG_T = "LONG_T";
	public final static String FLOAT = "Type.FLOAT";
	public final static String FLOAT_T = "FLOAT_T";
	public final static String DOUBLE = "Type.DOUBLE";
	public final static String DOUBLE_T = "DOUBLE_T";
	public final static String OBJECT = "Type.OBJECT";
	public final static String OBJECT_T = "OBJECT_T";

	public static final String FILE_ALONE = FILE;

	public CoreElementParameter(String type, String name, String direction) {
		super(type, name);
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}
