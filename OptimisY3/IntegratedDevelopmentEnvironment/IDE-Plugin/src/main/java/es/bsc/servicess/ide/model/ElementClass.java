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

import javax.xml.namespace.QName;

import com.sun.codemodel.JDefinedClass;

public class ElementClass {

	private QName elementType;
	private JDefinedClass jDefClass;

	public ElementClass() {

	}

	public ElementClass(QName elementType, JDefinedClass jDefClass) {
		super();
		this.elementType = elementType;
		this.jDefClass = jDefClass;
	}

	public QName getElementType() {
		return elementType;
	}

	public void setElementType(QName elementType) {
		this.elementType = elementType;
	}

	public JDefinedClass getjDefClass() {
		return jDefClass;
	}

	public void setjDefClass(JDefinedClass jDefClass) {
		this.jDefClass = jDefClass;
	}

}
