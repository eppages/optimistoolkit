/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.client.userwidget;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import eu.optimis.sm.gui.client.model.Resource;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class LeftDiagramPanel extends ContentPanel {

	private LabelToolItem labelToolItem = new LabelToolItem();
	private Widget textArea = new TextArea();
	
	public LeftDiagramPanel() {
		setHeading("Welcome to the OPTIMIS Service Provider website!");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(labelToolItem);
		setTopComponent(toolBar);
	}

	public void setErrorLabel() {
		labelToolItem.setStyleName("errorMessage");
		labelToolItem.setVisible(true);
	}

	public void setToolbarMessageLabel() {
		labelToolItem.setStyleName("toolbarMessage");
		labelToolItem.setVisible(true);
	}

	public LeftDiagramPanel(String submission) {
		setHeading("Welcome to the OPTIMIS Service Provider website!");
		setLayout(new FitLayout());
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(submission));
		setTopComponent(toolBar);
	}

	public void setSubmissionText(String text) {
		labelToolItem.setLabel(text);
	}

	private List<Resource> parseXML(String reqtext) {
		Document doc = XMLParser.parse(reqtext);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("monitoring_resource");

		List<Resource> list = new ArrayList<Resource>();
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				list.add(new Resource(getTagValue(
						"physical_resource_id", eElement), getTagValue(
						"resource_type", eElement), getTagValue("metric_name",
						eElement), getTagValue("metric_value", eElement)));
			}
		}

		System.out.println("parsered list size: " + list.size());
		return list;
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		try {
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public Widget getTextArea() {
		return textArea;
	}

	public void setTextArea(Widget textArea) {
		this.textArea = textArea;
	}
}