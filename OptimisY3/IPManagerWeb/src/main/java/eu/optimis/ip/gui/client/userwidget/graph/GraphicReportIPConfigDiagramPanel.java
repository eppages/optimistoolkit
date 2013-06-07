/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ip.gui.client.userwidget.graph;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.optimis.ip.gui.client.IPManagerWebServiceAsync;
import eu.optimis.ip.gui.client.MainEvents;
import eu.optimis.ip.gui.client.resources.Constants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphicReportIPConfigDiagramPanel extends ContentPanel {

    public static final String STR_CONSTRAINT_TRUST_GET = "Trust >=";
    public static final String STR_CONSTRAINT_RISK_LET = "Risk <=";
    public static final String STR_CONSTRAINT_ECOLOGICAL_GET = "Ecological Eff. >=";
    public static final String STR_CONSTRAINT_ENERGY_GET = "Energy Eff. >=";
    public static final String STR_CONSTRAINT_COST_LET = "Cost <=";
    
    public static final String STR_BLO_TRUST = "Max. Trust";
    public static final String STR_BLO_RISK = "Min. Risk";
    public static final String STR_BLO_ENERGY = "Max. Energy Eff.";
    public static final String STR_BLO_ECOLOGICAL = "Max. Ecological Eff.";
    public static final String STR_BLO_COST = "Min. Cost";
    
    
    private IPManagerWebServiceAsync service;
    private LabelToolItem errorLabel;
    private TextArea textConstraintDisplayer;
    private String blo;
    private SimpleComboBox objective;
    private SimpleComboBox constraint;
    private NumberField constraintNum;
    private List<String> constraintsToSubmit = new ArrayList();
    private String constraintsToSubmitString = "";

    public GraphicReportIPConfigDiagramPanel() {

        setHeading(Constants.MENU_IPCONFIG_NAME);
        setLayout(new FitLayout());

        ToolBar toolBar = new ToolBar();
        LabelToolItem labelObjective = new LabelToolItem("Objective:   ");
        toolBar.add(labelObjective);
        labelObjective.setVisible(true);
        setTopComponent(toolBar);

        objective = new SimpleComboBox();
        objective.setLabelSeparator("Objective:");
        objective.setEmptyText("Specify an objective");
        objective.add(STR_BLO_TRUST);
        objective.add(STR_BLO_RISK);
        objective.add(STR_BLO_ENERGY);
        objective.add(STR_BLO_ECOLOGICAL);
        objective.add(STR_BLO_COST);
        toolBar.add(objective);

        LabelToolItem labelConstraint = new LabelToolItem("Constraint:   ");
        toolBar.add(labelConstraint);
        labelConstraint.setVisible(true);

        constraint = new SimpleComboBox();
        constraint.setLabelSeparator("Constraint:");
        constraint.setEmptyText("Specify a constraint");
        constraint.add(STR_CONSTRAINT_TRUST_GET);
        constraint.add(STR_CONSTRAINT_RISK_LET);
        constraint.add(STR_CONSTRAINT_ECOLOGICAL_GET);
        constraint.add(STR_CONSTRAINT_ENERGY_GET);
        constraint.add(STR_CONSTRAINT_COST_LET);
        toolBar.add(constraint);

        constraintNum = new NumberField();
        constraintNum.setEmptyText("Specify a value");
        constraintNum.setLabelSeparator("");
        constraintNum.setAllowBlank(false);
        toolBar.add(constraintNum);

        Button addConst = new Button("Add constraint");
        toolBar.add(addConst);

        Button submit = new Button("Submit");
        toolBar.add(submit);

        Button reset = new Button("Reset");
        toolBar.add(reset);

        addConst.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

                int num = -1;
                if (constraintNum.getValue() != null) {
                    num = constraintNum.getValue().intValue();
                }
                if (constraint.getSimpleValue() == null || constraintNum.getValue() == null || constraint.getSimpleValue().toString().equalsIgnoreCase("")) {
                    errorLabel.setLabel("Please complete all constraint fields");
                    //risk and trust correct values are from 1 to 7
                } else if (((constraint.getSimpleValue()).toString().equalsIgnoreCase(STR_CONSTRAINT_RISK_LET) || (constraint.getSimpleValue()).toString().equalsIgnoreCase(STR_CONSTRAINT_TRUST_GET)) && (num < 1 || num > 7)) {
                    errorLabel.setLabel("Correct values are from 1 to 7");
                } else if (((constraint.getSimpleValue()).toString().equalsIgnoreCase(STR_CONSTRAINT_ECOLOGICAL_GET) || (constraint.getSimpleValue()).toString().equalsIgnoreCase(STR_CONSTRAINT_ENERGY_GET) || (constraint.getSimpleValue()).toString().equalsIgnoreCase(STR_CONSTRAINT_COST_LET)) && (num < 0)) {
                    errorLabel.setLabel("Negative values are not allowed");
                } else {

                    String cons = constraint.getSimpleValue() + " " + constraintNum.getValue();
                    constraintsToSubmit.add(cons);
                    constraint.clear();
                    constraintNum.clear();
                    errorLabel.setLabel("");
                    addConstraintToSubmit(cons);
                }
            }
        });
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (objective.getValue() == null) {
                    errorLabel.setLabel("Please select an objective");
                } else {
                    final List<String> constraints2 = new ArrayList();
                    Iterator<String> iter = constraintsToSubmit.iterator();
                    while (iter.hasNext()) {
                        constraints2.add(iter.next());
                    }
                    blo = objective.getSimpleValue().toString();
                    service = (IPManagerWebServiceAsync) Registry.get("guiservice");
                    service.setBLO(blo, constraints2, new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Dispatcher.forwardEvent(MainEvents.Error,
                                    caught);
                            errorLabel.setLabel("An error ocurred while submitting the BLO and constraints.");
                            objective.clear();
                            constraint.clear();
                            constraintNum.clear();
                            constraintsToSubmitString = "";
                            constraintsToSubmit = new ArrayList();
                            textConstraintDisplayer.setValue("");
                        }

                        @Override
                        public void onSuccess(Object result) {

                            errorLabel.setLabel("");
                            objective.clear();
                            constraint.clear();
                            constraintNum.clear();
                            constraintsToSubmit = new ArrayList();
                            setBLOSubmissionResult(constraints2);
                        }
                    });

                }
            }
        });

        reset.addSelectionListener(
                new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        objective.clear();
                        constraint.clear();
                        constraintNum.clear();
                        blo = "";
                        constraintsToSubmit = new ArrayList();
                        constraintsToSubmitString = "";
                        textConstraintDisplayer.setValue("");
                    }
                });

        ToolBar toolBar3 = new ToolBar();
        toolBar3.setAlignment(Style.HorizontalAlignment.CENTER);
        toolBar3.setAutoHeight(true);
        textConstraintDisplayer = new TextArea();
        textConstraintDisplayer.addStyleName("demo-TextArea");
        textConstraintDisplayer.setWidth("400px");
        textConstraintDisplayer.setHeight("200px");
        textConstraintDisplayer.setReadOnly(true);
        textConstraintDisplayer.setEmptyText("No constraints specified.");
        toolBar3.add(textConstraintDisplayer);
        textConstraintDisplayer.setVisible(true);
        add(toolBar3);

        ToolBar toolBar2 = new ToolBar();
        toolBar2.setAlignment(Style.HorizontalAlignment.CENTER);
        errorLabel = new LabelToolItem("");
        toolBar2.add(errorLabel);
        errorLabel.setVisible(true);
        add(toolBar2);
    }

    public void setBLOSubmissionResult(List cons) {
        String obj = "Submitted BLO: " + blo;

        String constraints = "";
        if (!cons.isEmpty()) {
            Iterator iter = cons.iterator();
            constraints = "Submitted constraints:\n";
            while (iter.hasNext()) {
                constraints = constraints + "\t" + (String) iter.next() + "\n";
            }
        }
        
        textConstraintDisplayer.setValue(obj + "\n" + constraints);
    }

    public void addConstraintToSubmit(String cons) {
        if (constraintsToSubmitString.equalsIgnoreCase("")) {
            constraintsToSubmitString = "\t" + cons;
        } else {
            constraintsToSubmitString = constraintsToSubmitString + "\n\t" + cons;
        }
        textConstraintDisplayer.setValue("Constraints to be submitted:\n" + constraintsToSubmitString);
    }
}
