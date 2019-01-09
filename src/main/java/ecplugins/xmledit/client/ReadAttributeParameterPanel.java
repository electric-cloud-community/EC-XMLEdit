
// ReadAttributeParameterPanel.java --
//
// ReadAttributeParameterPanel.java is part of ElectricCommander.
//
// Copyright (c) 2005-2012 Electric Cloud, Inc.
// All rights reserved.
//

package ecplugins.xmledit.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.electriccloud.commander.client.domain.ActualParameter;
import com.electriccloud.commander.client.domain.FormalParameter;
import com.electriccloud.commander.client.util.StringUtil;
import com.electriccloud.commander.gwt.client.ComponentBase;
import com.electriccloud.commander.gwt.client.ui.FormBuilder;
import com.electriccloud.commander.gwt.client.ui.ParameterPanel;
import com.electriccloud.commander.gwt.client.ui.ParameterPanelProvider;
import com.electriccloud.commander.gwt.client.ui.RadioButtonGroup;
import com.electriccloud.commander.gwt.client.ui.ValuedListBox;

public class ReadAttributeParameterPanel
    extends ComponentBase
    implements ParameterPanel,
        ParameterPanelProvider
{

    //~ Static fields/initializers ---------------------------------------------

    // ~ Static fields/initializers----------------------------
    private static UiBinder<Widget, ReadAttributeParameterPanel> s_binder = GWT
            .create(Binder.class);

    // These are all the formalParameters on the Procedure
    static final String ATTRIBUTE        = "attribute";
    static final String ATTRIBUTE_OUTPP  = "attribute_outpp";
    static final String ATTRIBUTE_OUTPSP = "attribute_outpsp";
    static final String FILEPATH         = "filepath";
    static final String PROPERTY         = "property_path";
    static final String QUERY            = "read_query";
    static final String TYPE             = "return_type";
    static final String SOURCE           = "source";
    static final String XML_CODE         = "xml_code";
    static final String SELECTION        = "selection";

    //~ Instance fields --------------------------------------------------------

    // ~ Instance fields
    // --------------------------------------------------------
    @UiField FormBuilder readAttributeParameterForm;

    //~ Methods ----------------------------------------------------------------

    // ~ Methods
    // ----------------------------------------------------------------
    /**
     * This function is called by SDK infrastructure to initialize the UI parts
     * of this component.
     *
     * @return  A widget that the infrastructure should place in the UI; usually
     *          a panel.
     */
    @Override public Widget doInit()
    {
        Widget              base   = s_binder.createAndBindUi(this);
        final ValuedListBox source = getUIFactory().createValuedListBox();

        source.addItem("Filepath", FILEPATH);
        source.addItem("Property", PROPERTY);
        source.addItem("Code", XML_CODE);

        final RadioButtonGroup return_type = getUIFactory()
                .createRadioButtonGroup(TYPE, "value", new VerticalPanel());

        return_type.addRadio("Complete structure", "structure");
        return_type.addRadio("Value", "value");

        final ValuedListBox selection = getUIFactory().createValuedListBox();

        selection.addItem("First Element", "first");
        selection.addItem("Last Element", "last");
        selection.addItem("All", "all");
        readAttributeParameterForm.addRow(false, "Source:",
            "Select the source for the xml to parse.", SOURCE, FILEPATH,
            source);
        readAttributeParameterForm.addRow(false, "Filepath:",
            "Path to the XML file to parse. ", FILEPATH, "", new TextBox());
        readAttributeParameterForm.addRow(false, "Property path:",
            "Path to the property where the xml is located. ", PROPERTY, "",
            new TextBox());
        readAttributeParameterForm.addRow(false, "Code:", "XML code to parse.",
            XML_CODE, "", new TextArea());
        readAttributeParameterForm.addRow(true, "Xpath Query:",
            "XPath query to get the element.", QUERY, "", new TextArea());
        readAttributeParameterForm.addRow(true, "Attribute:",
            "Name of the attribute to read", ATTRIBUTE, "", new TextBox());
        readAttributeParameterForm.addRow(true, "Return Type:",
            "Return the entire attribute structure(name=value) or just its value.",
            TYPE, "value", return_type);
        readAttributeParameterForm.addRow(false, "Element Selection:",
            "Select the action to do if the query returns several elements.",
            SELECTION, "first", selection);
        readAttributeParameterForm.addRow(false,
            "Result Attributes(output property sheet path):",
            "Path to property sheet to hold the XML attributes read.",
            ATTRIBUTE_OUTPSP, "", new TextBox());
        readAttributeParameterForm.addRow(false,
            "Result Attribute(output property path):",
            "Path to property to hold the XML attribute read.", ATTRIBUTE_OUTPP,
            "", new TextBox());
        source.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override public void onValueChange(
                        ValueChangeEvent<String> event)
                {
                    updateRowVisibility();
                }
            });
        selection.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override public void onValueChange(
                        ValueChangeEvent<String> event)
                {
                    updateRowVisibility();
                }
            });
        updateRowVisibility();

        return base;
    }

    /**
     * Performs validation of user supplied data before submitting the form.
     *
     * <p>This function is called after the user hits submit.</p>
     *
     * @return  true if checks succeed, false otherwise
     */
    @Override public boolean validate()
    {
        boolean validationStatus = readAttributeParameterForm.validate();
        String  source           = readAttributeParameterForm.getValue(SOURCE);

        // QUERY is required.
        if (StringUtil.isEmpty(
                    readAttributeParameterForm.getValue(QUERY)
                                          .trim())) {
            readAttributeParameterForm.setErrorMessage(QUERY,
                "This Field is required.");
            validationStatus = false;
        }

        // ATTRIBUTE is required.
        if (StringUtil.isEmpty(
                    readAttributeParameterForm.getValue(ATTRIBUTE)
                                          .trim())) {
            readAttributeParameterForm.setErrorMessage(ATTRIBUTE,
                "This Field is required.");
            validationStatus = false;
        }

        if (FILEPATH.equals(source)) {

            // FILEPATH is required.
            if (StringUtil.isEmpty(
                        readAttributeParameterForm.getValue(FILEPATH)
                                              .trim())) {
                readAttributeParameterForm.setErrorMessage(FILEPATH,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (PROPERTY.equals(source)) {

            // PROPERTY is required.
            if (StringUtil.isEmpty(
                        readAttributeParameterForm.getValue(PROPERTY)
                                              .trim())) {
                readAttributeParameterForm.setErrorMessage(PROPERTY,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (XML_CODE.equals(source)) {

            // XML_CODE is required.
            if (StringUtil.isEmpty(
                        readAttributeParameterForm.getValue(XML_CODE)
                                              .trim())) {
                readAttributeParameterForm.setErrorMessage(XML_CODE,
                    "This Field is required.");
                validationStatus = false;
            }
        }

        return validationStatus;
    }

    protected void updateRowVisibility()
    {
        String source    = readAttributeParameterForm.getValue(SOURCE);
        String selection = readAttributeParameterForm.getValue(SELECTION);

        readAttributeParameterForm.setRowVisible(FILEPATH,
            FILEPATH.equals(source));
        readAttributeParameterForm.setRowVisible(PROPERTY,
            PROPERTY.equals(source));
        readAttributeParameterForm.setRowVisible(XML_CODE,
            XML_CODE.equals(source));
        readAttributeParameterForm.setRowVisible(ATTRIBUTE_OUTPSP,
            "all".equals(selection));
        readAttributeParameterForm.setRowVisible(ATTRIBUTE_OUTPP,
            ("first".equals(selection) | "last".equals(selection)));
    }

    /**
     * This method is used by UIBinder to embed FormBuilder's in the UI.
     *
     * @return  a new FormBuilder.
     */
    @UiFactory FormBuilder createFormBuilder()
    {
        return getUIFactory().createFormBuilder();
    }

    @Override public ParameterPanel getParameterPanel()
    {
        return this;
    }

    /**
     * Gets the values of the parameters that should map 1-to-1 to the formal
     * parameters on the object being called. Transform user input into a map of
     * parameter names and values.
     *
     * <p>This function is called after the user hits submit and validation has
     * succeeded.</p>
     *
     * @return  The values of the parameters that should map 1-to-1 to the
     *          formal parameters on the object being called.
     */
    @Override public Map<String, String> getValues()
    {
        Map<String, String> actualParams   = new HashMap<String, String>();
        Map<String, String> readFormValues =
            readAttributeParameterForm.getValues();

        actualParams.put(SOURCE, readFormValues.get(SOURCE));
        actualParams.put(QUERY, readFormValues.get(QUERY));
        actualParams.put(ATTRIBUTE, readFormValues.get(ATTRIBUTE));
        actualParams.put(FILEPATH, readFormValues.get(FILEPATH));
        actualParams.put(PROPERTY, readFormValues.get(PROPERTY));
        actualParams.put(XML_CODE, readFormValues.get(XML_CODE));
        actualParams.put(TYPE, readFormValues.get(TYPE));
        actualParams.put(ATTRIBUTE_OUTPP, readFormValues.get(ATTRIBUTE_OUTPP));
        actualParams.put(ATTRIBUTE_OUTPSP,
            readFormValues.get(ATTRIBUTE_OUTPSP));
        actualParams.put(SELECTION, readFormValues.get(SELECTION));

        return actualParams;
    }

    /**
     * Push actual parameters into the panel implementation.
     *
     * <p>This is used when editing an existing object to show existing content.
     * </p>
     *
     * @param  actualParameters  Actual parameters assigned to this list of
     *                           parameters.
     */
    @Override public void setActualParameters(
            Collection<ActualParameter> actualParameters)
    {

        if (actualParameters == null) {
            return;
        }

        // First load the parameters into a map. Makes it easier to
        // update the form by querying for various params randomly.
        Map<String, String> params = new HashMap<String, String>();

        for (ActualParameter p : actualParameters) {
            params.put(p.getName(), p.getValue());
        }

        // Do the easy form elements first.
        for (String key : new String[] {
                    SOURCE,
                    QUERY,
                    ATTRIBUTE,
                    FILEPATH,
                    PROPERTY,
                    XML_CODE,
                    TYPE,
                    ATTRIBUTE_OUTPP,
                    ATTRIBUTE_OUTPSP,
                    SELECTION
                }) {
            readAttributeParameterForm.setValue(key,
                StringUtil.nullToEmpty(params.get(key)));
        }

        updateRowVisibility();
    }

    /**
     * Push form parameters into the panel implementation.
     *
     * <p>This is used when creating a new object and showing default values.
     * </p>
     *
     * @param  formalParameters  Formal parameters on the target object.
     */
    @Override public void setFormalParameters(
            Collection<FormalParameter> formalParameters) { }

    //~ Inner Interfaces -------------------------------------------------------

    // ~ Inner Interfaces
    // -------------------------------------------------------
    interface Binder
        extends UiBinder<Widget, ReadAttributeParameterPanel> { }
}
