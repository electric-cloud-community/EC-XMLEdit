
// UpdateElementParameterPanel.java --
//
// UpdateElementParameterPanel.java is part of ElectricCommander.
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
import com.google.gwt.user.client.ui.Widget;

import com.electriccloud.commander.client.domain.ActualParameter;
import com.electriccloud.commander.client.domain.FormalParameter;
import com.electriccloud.commander.client.util.StringUtil;
import com.electriccloud.commander.gwt.client.ComponentBase;
import com.electriccloud.commander.gwt.client.ui.FormBuilder;
import com.electriccloud.commander.gwt.client.ui.ParameterPanel;
import com.electriccloud.commander.gwt.client.ui.ParameterPanelProvider;
import com.electriccloud.commander.gwt.client.ui.ValuedListBox;

public class UpdateElementParameterPanel
    extends ComponentBase
    implements ParameterPanel,
        ParameterPanelProvider
{

    //~ Static fields/initializers ---------------------------------------------

    // ~ Static fields/initializers----------------------------
    private static UiBinder<Widget, UpdateElementParameterPanel> s_binder = GWT
            .create(Binder.class);

    // These are all the formalParameters on the Procedure
    static final String SOURCE            = "source";
    static final String FILEPATH          = "filepath";
    static final String PROPERTY          = "property_path";
    static final String XML_CODE          = "xml_code";
    static final String QUERY             = "read_query";
    static final String ELEMENT_NEW_VALUE = "element_new_value";
    static final String SELECTION         = "selection";
    static final String XML_OUTPP         = "xml_outpp";

    //~ Instance fields --------------------------------------------------------

    // ~ Instance fields
    // --------------------------------------------------------
    @UiField FormBuilder updateElementParameterForm;

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

        final ValuedListBox selection = getUIFactory().createValuedListBox();

        selection.addItem("First Element", "first");
        selection.addItem("Last Element", "last");
        selection.addItem("All", "all");
        updateElementParameterForm.addRow(false, "Source:",
            "Select the source for the xml to parse.", SOURCE, FILEPATH,
            source);
        updateElementParameterForm.addRow(false, "Filepath:",
            "Path to the XML file to parse. ", FILEPATH, "", new TextBox());
        updateElementParameterForm.addRow(false, "Property path:",
            "Path to the property where the xml is located. ", PROPERTY, "",
            new TextBox());
        updateElementParameterForm.addRow(false, "Code:", "XML code to parse.",
            XML_CODE, "", new TextArea());
        updateElementParameterForm.addRow(true, "Xpath Query:",
            "XPath query to get the element.", QUERY, "", new TextArea());
        updateElementParameterForm.addRow(false, "Element Value:",
            "New value for the element.", ELEMENT_NEW_VALUE, "",
            new TextArea());
        updateElementParameterForm.addRow(false, "Update:",
            "Select the action to do if the query returns several elements.",
            SELECTION, "first", selection);
        updateElementParameterForm.addRow(false,
            "Result XML(output property path):",
            "Path to property to hold the XML.", XML_OUTPP, "", new TextBox());
        source.addValueChangeHandler(new ValueChangeHandler<String>() {
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
        boolean validationStatus = updateElementParameterForm.validate();
        String  source           = updateElementParameterForm.getValue(SOURCE);

        // QUERY is required.
        if (StringUtil.isEmpty(
                    updateElementParameterForm.getValue(QUERY)
                                          .trim())) {
            updateElementParameterForm.setErrorMessage(QUERY,
                "This Field is required.");
            validationStatus = false;
        }

        if (FILEPATH.equals(source)) {

            // FILEPATH is required.
            if (StringUtil.isEmpty(
                        updateElementParameterForm.getValue(FILEPATH)
                                              .trim())) {
                updateElementParameterForm.setErrorMessage(FILEPATH,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (PROPERTY.equals(source)) {

            // PROPERTY is required.
            if (StringUtil.isEmpty(
                        updateElementParameterForm.getValue(PROPERTY)
                                              .trim())) {
                updateElementParameterForm.setErrorMessage(PROPERTY,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (XML_CODE.equals(source)) {

            // XML_CODE is required.
            if (StringUtil.isEmpty(
                        updateElementParameterForm.getValue(XML_CODE)
                                              .trim())) {
                updateElementParameterForm.setErrorMessage(XML_CODE,
                    "This Field is required.");
                validationStatus = false;
            }
        }

        return validationStatus;
    }

    protected void updateRowVisibility()
    {
        String source = updateElementParameterForm.getValue(SOURCE);

        updateElementParameterForm.setRowVisible(FILEPATH,
            FILEPATH.equals(source));
        updateElementParameterForm.setRowVisible(PROPERTY,
            PROPERTY.equals(source));
        updateElementParameterForm.setRowVisible(XML_CODE,
            XML_CODE.equals(source));
        updateElementParameterForm.setRowVisible(XML_OUTPP,
            XML_CODE.equals(source));
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
        Map<String, String> actualParams     = new HashMap<String, String>();
        Map<String, String> insertFormValues =
            updateElementParameterForm.getValues();

        actualParams.put(SOURCE, insertFormValues.get(SOURCE));
        actualParams.put(QUERY, insertFormValues.get(QUERY));
        actualParams.put(FILEPATH, insertFormValues.get(FILEPATH));
        actualParams.put(PROPERTY, insertFormValues.get(PROPERTY));
        actualParams.put(XML_CODE, insertFormValues.get(XML_CODE));
        actualParams.put(XML_OUTPP, insertFormValues.get(XML_OUTPP));
        actualParams.put(SELECTION, insertFormValues.get(SELECTION));
        actualParams.put(ELEMENT_NEW_VALUE,
            insertFormValues.get(ELEMENT_NEW_VALUE));

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
                    FILEPATH,
                    PROPERTY,
                    XML_CODE,
                    XML_OUTPP,
                    SELECTION,
                    ELEMENT_NEW_VALUE
                }) {
            updateElementParameterForm.setValue(key,
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
        extends UiBinder<Widget, UpdateElementParameterPanel> { }
}
