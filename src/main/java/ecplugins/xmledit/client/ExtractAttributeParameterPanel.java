
// ExtractAttributeParameterPanel.java --
//
// ExtractAttributeParameterPanel.java is part of ElectricCommander.
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

public class ExtractAttributeParameterPanel
    extends ComponentBase
    implements ParameterPanel,
        ParameterPanelProvider
{

    //~ Static fields/initializers ---------------------------------------------

    // ~ Static fields/initializers----------------------------
    private static UiBinder<Widget, ExtractAttributeParameterPanel> s_binder =
        GWT.create(Binder.class);

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
    static final String XML_OUTPP        = "xml_outpp";

    //~ Instance fields --------------------------------------------------------

    // ~ Instance fields
    // --------------------------------------------------------
    @UiField FormBuilder extractAttributeParameterForm;

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
        extractAttributeParameterForm.addRow(false, "Source:",
            "Select the source for the xml to parse.", SOURCE, FILEPATH,
            source);
        extractAttributeParameterForm.addRow(false, "Filepath:",
            "Path to the XML file to parse. ", FILEPATH, "", new TextBox());
        extractAttributeParameterForm.addRow(false, "Property path:",
            "Path to the property where the xml is located. ", PROPERTY, "",
            new TextBox());
        extractAttributeParameterForm.addRow(false, "Code:",
            "XML code to parse.", XML_CODE, "", new TextArea());
        extractAttributeParameterForm.addRow(true, "Xpath Query:",
            "XPath query to get the element.", QUERY, "", new TextArea());
        extractAttributeParameterForm.addRow(true, "Attribute:",
            "Name of the attribute to extract", ATTRIBUTE, "", new TextBox());
        extractAttributeParameterForm.addRow(true, "Return Type:",
            "Return the entire attribute structure(name=value) or just its value.",
            TYPE, "value", return_type);
        extractAttributeParameterForm.addRow(false, "Attribute Selection:",
            "Select the action to do if the query returns several attributes.",
            SELECTION, "first", selection);
        extractAttributeParameterForm.addRow(false,
            "Result Attributes(output property sheet path):",
            "Path to property sheet to hold the XML attributes extracted.",
            ATTRIBUTE_OUTPSP, "", new TextBox());
        extractAttributeParameterForm.addRow(false,
            "Result Attribute(output property path):",
            "Path to property to hold the XML attribute extracted.",
            ATTRIBUTE_OUTPP, "", new TextBox());
        extractAttributeParameterForm.addRow(false,
            "Result XML(output property path):",
            "Path to property to hold the XML.", XML_OUTPP, "", new TextBox());
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
        boolean validationStatus = extractAttributeParameterForm.validate();
        String  source           = extractAttributeParameterForm.getValue(
                SOURCE);

        // QUERY is required.
        if (StringUtil.isEmpty(
                    extractAttributeParameterForm.getValue(QUERY)
                                             .trim())) {
            extractAttributeParameterForm.setErrorMessage(QUERY,
                "This Field is required.");
            validationStatus = false;
        }

        // ATTRIBUTE is required.
        if (StringUtil.isEmpty(
                    extractAttributeParameterForm.getValue(ATTRIBUTE)
                                             .trim())) {
            extractAttributeParameterForm.setErrorMessage(ATTRIBUTE,
                "This Field is required.");
            validationStatus = false;
        }

        if (FILEPATH.equals(source)) {

            // FILEPATH is required.
            if (StringUtil.isEmpty(
                        extractAttributeParameterForm.getValue(FILEPATH)
                                                 .trim())) {
                extractAttributeParameterForm.setErrorMessage(FILEPATH,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (PROPERTY.equals(source)) {

            // PROPERTY is required.
            if (StringUtil.isEmpty(
                        extractAttributeParameterForm.getValue(PROPERTY)
                                                 .trim())) {
                extractAttributeParameterForm.setErrorMessage(PROPERTY,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (XML_CODE.equals(source)) {

            // XML_CODE is required.
            if (StringUtil.isEmpty(
                        extractAttributeParameterForm.getValue(XML_CODE)
                                                 .trim())) {
                extractAttributeParameterForm.setErrorMessage(XML_CODE,
                    "This Field is required.");
                validationStatus = false;
            }
        }

        return validationStatus;
    }

    protected void updateRowVisibility()
    {
        String source    = extractAttributeParameterForm.getValue(SOURCE);
        String selection = extractAttributeParameterForm.getValue(SELECTION);

        extractAttributeParameterForm.setRowVisible(FILEPATH,
            FILEPATH.equals(source));
        extractAttributeParameterForm.setRowVisible(PROPERTY,
            PROPERTY.equals(source));
        extractAttributeParameterForm.setRowVisible(XML_CODE,
            XML_CODE.equals(source));
        extractAttributeParameterForm.setRowVisible(XML_OUTPP,
            XML_CODE.equals(source));
        extractAttributeParameterForm.setRowVisible(ATTRIBUTE_OUTPSP,
            "all".equals(selection));
        extractAttributeParameterForm.setRowVisible(ATTRIBUTE_OUTPP,
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
        Map<String, String> actualParams      = new HashMap<String, String>();
        Map<String, String> extractFormValues =
            extractAttributeParameterForm.getValues();

        actualParams.put(SOURCE, extractFormValues.get(SOURCE));
        actualParams.put(QUERY, extractFormValues.get(QUERY));
        actualParams.put(ATTRIBUTE, extractFormValues.get(ATTRIBUTE));
        actualParams.put(FILEPATH, extractFormValues.get(FILEPATH));
        actualParams.put(PROPERTY, extractFormValues.get(PROPERTY));
        actualParams.put(XML_OUTPP, extractFormValues.get(XML_OUTPP));
        actualParams.put(XML_CODE, extractFormValues.get(XML_CODE));
        actualParams.put(TYPE, extractFormValues.get(TYPE));
        actualParams.put(ATTRIBUTE_OUTPP,
            extractFormValues.get(ATTRIBUTE_OUTPP));
        actualParams.put(ATTRIBUTE_OUTPSP,
            extractFormValues.get(ATTRIBUTE_OUTPSP));
        actualParams.put(SELECTION, extractFormValues.get(SELECTION));

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
                    SELECTION,
                    XML_OUTPP
                }) {
            extractAttributeParameterForm.setValue(key,
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
        extends UiBinder<Widget, ExtractAttributeParameterPanel> { }
}
