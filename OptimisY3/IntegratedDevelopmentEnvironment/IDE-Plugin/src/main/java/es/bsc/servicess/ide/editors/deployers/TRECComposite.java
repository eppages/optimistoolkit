/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
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

package es.bsc.servicess.ide.editors.deployers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class TRECComposite {
	private Shell shell;
	private Text trustValue;
	private Text riskValue;
	private Text ecoValue;
	private Text costValue;
	
	public TRECComposite(Shell shell) {
		this.shell = shell;
	}

	public void createComposite(Composite combInst) {
		Composite comp = new Composite(combInst, SWT.BORDER);
		comp.setLayout(new GridLayout(8, false)); 
		GridData rd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rd.grabExcessHorizontalSpace = true;
		comp.setLayoutData(rd);
		Label trustLabel = new Label(comp, SWT.NONE | SWT.BOLD);
		trustLabel.setText(" Trust: ");
		trustValue = new Text(comp,SWT.NONE);
		Label riskLabel = new Label(comp, SWT.NONE | SWT.BOLD);
		riskLabel.setText(" Risk: ");
		riskValue = new Text(comp,SWT.NONE);
		Label ecoLabel = new Label(comp, SWT.NONE | SWT.BOLD);
		ecoLabel.setText(" Eco: ");
		ecoValue = new Text(comp,SWT.NONE);
		Label costLabel = new Label(comp, SWT.NONE | SWT.BOLD);
		costLabel.setText(" Cost: ");
		costValue = new Text(comp,SWT.NONE);
		
	}

	public void setValues(Object o) {
		trustValue.setText(new Float(((TRECValues)o).getTrustValue()).toString());
		riskValue.setText(new Float(((TRECValues)o).getRiskValue()).toString());
		ecoValue.setText(new Float(((TRECValues)o).getEcoValue()).toString());
		costValue.setText(new Float(((TRECValues)o).getCostValue()).toString());
		
	}

}
