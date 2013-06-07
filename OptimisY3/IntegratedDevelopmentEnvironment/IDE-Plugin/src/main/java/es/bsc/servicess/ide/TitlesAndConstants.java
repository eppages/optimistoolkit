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

public class TitlesAndConstants {

	public static final String JAVA_LANG = "java.lang";

	public static final String STRING = "String";
	public static final String RETURNTYPE = "return_type";
	private static int SERVICESS = 0;
	private static int OPTIMIS = 1;
	private static int TYPE = OPTIMIS;
	private static String OPTIMISTitle = "OPTIMIS Service";
	private static String ServiceSsTitle = "ServiceSs Application";
	
	public static final String NORMAL_CLASS = "Standard Class";
	public static final String WS_CLASS = "Web Service Interface Class";
	public static final String EXTERNAL_CLASS = "External Class";
	private static final String[] CLASS_TYPES = new String[]{NORMAL_CLASS, WS_CLASS};
	

	public static String getTypeTitle() {
		if (TYPE == OPTIMIS)
			return OPTIMISTitle;
		else
			return ServiceSsTitle;
	}

	public static String getNewProjectWizardTitle() {
		return "New " + getTypeTitle()+ " Project";
		
	}
	
	public static String getNewProjectWizardDescription() {
		return "This wizard allows users to create a new "+ getTypeTitle() + " project";
	}

	public static String getNewProjectPageWizardTitle() {
		return getTypeTitle() + " Project";
	}

	public static String getNewClassWizardTitle() {
		return "New " + getTypeTitle() + " Class";
	}

	public static String getNewClassPageWizardTitle() {
		return getTypeTitle() + " Class";
	}
	
	public static String getNewClassPageWizardDescription() {
		return " Creates a new "+getTypeTitle() + " class for including Orchestration Elements";
	}

	public static String getDefaultOrchestrationType() {
		if (TYPE != OPTIMIS)
			return NORMAL_CLASS;
		else
			return WS_CLASS;
	}

	public static String[] getOrchestrationClassTypes() {
		if (TYPE == OPTIMIS)
			return new String[]{WS_CLASS, NORMAL_CLASS};
		else
			return new String[]{NORMAL_CLASS, WS_CLASS};
	}

	public static String getImportOrchestrationClassPageWizardTitle() {
		return "Select " + getTypeTitle() + " Class for importing";
	}

	public static String getImportOrchestrationClassPageWizardDescription() {
		return " Creates a new "+getTypeTitle() + " class for including Orchestration Elements from an external package";
	}

	public static String getImportOrchestrationWizardTitle() {
		return "Import" + getTypeTitle() + " Class";
	}
	
	

}
