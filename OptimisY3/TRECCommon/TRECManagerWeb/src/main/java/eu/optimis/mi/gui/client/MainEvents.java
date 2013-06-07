/**
 *  Copyright 2013 University of Leeds
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
package eu.optimis.mi.gui.client;

import com.extjs.gxt.ui.client.event.EventType;

public class MainEvents {

	public static final EventType Init = new EventType();

	public static final EventType Navigation = new EventType();
	public static final EventType ReportIterms = new EventType();

	public static final EventType ReportServiceIerms = new EventType();
	public static final EventType ReportVirtualIerms = new EventType();
	public static final EventType ReportPhysicalIerms = new EventType();
	public static final EventType ReportEnergyIerms = new EventType();

	public static final EventType ReportGraphicDiagram = new EventType();

	public static final EventType ReportGraphicCancel = new EventType();
	
	// Risk events
	public static final EventType ReportGraphicRiskDiagram = new EventType();

	// Eco events
	public static final EventType ReportGraphicEcoDiagram = new EventType();
	public static final EventType ReportGraphicEcoServiceDeploymentInfo = new EventType();
	public static final EventType ReportGraphicEcoServiceDeploymentInfoSP = new EventType();
	public static final EventType HideReportGraphicEcoServiceDeploymentInfo = new EventType();

	// Trust events
	public static final EventType ReportGraphicTrustDiagram = new EventType();
	public static final EventType ReportGraphicTrustSPGrid = new EventType();
	public static final EventType ReportGraphicTrustIPGrid = new EventType();

	// COST events
	public static final EventType ReportGraphicCostDiagram = new EventType();
	public static final EventType ReportGraphicCostSPDiagram = new EventType();

	public static final EventType Error = new EventType();

}
