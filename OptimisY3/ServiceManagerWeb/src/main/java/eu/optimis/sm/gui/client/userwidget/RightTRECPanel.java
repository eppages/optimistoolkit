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

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import eu.optimis.sm.gui.client.MainEvents;
import eu.optimis.sm.gui.client.ServiceManagerWebServiceAsync;

public class RightTRECPanel extends ContentPanel {

	private ServiceManagerWebServiceAsync service;
	String TRECurl = null;
	private String textScreen;
	private Widget textArea = new TextArea();
	

	public RightTRECPanel() {
		setHeading("TREC GUI");
		setLayout(new FitLayout());

		service = (ServiceManagerWebServiceAsync) Registry.get("guiservice");

		service.getTRECurl(LeftPanelLogin.session_id, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				System.out.println("Error: getTRECurl");
				Dispatcher.forwardEvent(MainEvents.error, caught);
			}
			public void onSuccess(String result) {
				System.out.println("Successfully executed: getTRECurl");
				removeAll();
				
				if(result.substring(0,0).equals("-"))
				{
					textScreen = result;
					((TextArea)textArea).setValue(textScreen);
					add(textArea);
					layout(true);
				}
				else {
					TRECurl = result;
					Frame frame = new Frame(TRECurl);
					frame.setWidth("100%");
					add(frame);
					System.out.println("SDO url = " + TRECurl);
					setLayoutOnChange(true);
					layout(true);
					TRECurl = TRECurl.concat("?noheader=y");
					TRECurl = TRECurl.concat("&side=sp");
	                Frame newFrame = new Frame(TRECurl);
	                newFrame.setWidth("100%");
	                remove(frame);
	                add(newFrame);
	                frame = newFrame;
	                setLayout(new FitLayout());
	                layout(true);
				}
			}
		});
	}
}
