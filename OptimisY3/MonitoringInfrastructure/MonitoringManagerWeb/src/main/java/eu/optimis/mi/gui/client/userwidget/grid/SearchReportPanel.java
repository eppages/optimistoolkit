
/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.gui.client.userwidget.grid;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class SearchReportPanel extends ContentPanel {

	private FormData formData;

	public SearchReportPanel() {
		setHeading("Search Report");
		setLayout(new FlowLayout());
		FormPanel form = new FormPanel();  
		form.setWidth(300);  
		form.setLayout(new FlowLayout());
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeading("Service Level Report");
		fieldSet.setCheckboxToggle(true);

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(75);
		fieldSet.setLayout(layout);

		TextField<String> firstName = new TextField<String>();
		firstName.setFieldLabel("Service ID");
		firstName.setAllowBlank(false);
		fieldSet.add(firstName, formData);
		form.add(fieldSet);
		form.addButton(new Button("Submit"));
		form.addButton(new Button("Cancel"));
		
		add(form);
	}

}
