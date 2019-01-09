
// XPathQueryParameterPanel.java --
//
// XPathQueryParameterPanel.java is part of ElectricCommander.
//
// Copyright (c) 2005-2012 Electric Cloud, Inc.
// All rights reserved.
//

package ecplugins.xmledit.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NonNls;

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

public class XPathQueryParameterPanel
    extends ComponentBase
    implements ParameterPanel,
        ParameterPanelProvider
{

    //~ Static fields/initializers ---------------------------------------------

    private static final UiBinder<Widget, XPathQueryParameterPanel> s_binder =
        GWT.create(Binder.class);

    // These are all the formalParameters on the Procedure
    @NonNls private static final String FILEPATH = "filepath";
    @NonNls private static final String PROPERTY = "property_path";
    @NonNls private static final String QUERY    = "xpath_query";
    @NonNls private static final String SOURCE   = "source";
    @NonNls private static final String XML_CODE = "xml_code";

    //~ Instance fields --------------------------------------------------------

    @UiField protected FormBuilder m_queryParameterForm;

    //~ Methods ----------------------------------------------------------------

    /**
     * This function is called by SDK infrastructure to initialize the UI parts
     * of this component.
     *
     * @return  A widget that the infrastructure should place in the UI; usually
     *          a panel.
     */
    @Override public Widget doInit()
    {
        Widget        base   = s_binder.createAndBindUi(this);
        ValuedListBox source = getUIFactory().createValuedListBox();

        // noinspection HardCodedStringLiteral
        source.addItem("Filepath", FILEPATH);

        // noinspection HardCodedStringLiteral
        source.addItem("Property", PROPERTY);

        // noinspection HardCodedStringLiteral
        source.addItem("Code", XML_CODE);

        // noinspection HardCodedStringLiteral
        m_queryParameterForm.addRow(false, "Source:",
            "Select the source for the xml to parse.", SOURCE, FILEPATH,
            source);

        // noinspection HardCodedStringLiteral
        m_queryParameterForm.addRow(false, "Filepath:",
            "Path to the XML file to parse. ", FILEPATH, "", new TextBox());

        // noinspection HardCodedStringLiteral
        m_queryParameterForm.addRow(false, "Property path:",
            "Path to the property where the xml is located. ", PROPERTY, "",
            new TextBox());

        // noinspection HardCodedStringLiteral
        m_queryParameterForm.addRow(false, "Code:", "XML code to parse.",
            XML_CODE, "", new TextArea());

        // noinspection HardCodedStringLiteral
        m_queryParameterForm.addRow(true, "Xpath Query:",
            "XPath query to execute.", QUERY, "", new TextArea());
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
        boolean validationStatus = m_queryParameterForm.validate();
        String  source           = m_queryParameterForm.getValue(SOURCE);

        // QUERY is required.
        if (StringUtil.isEmpty(m_queryParameterForm.getValue(QUERY)
                                                   .trim())) {

            // noinspection HardCodedStringLiteral
            m_queryParameterForm.setErrorMessage(QUERY,
                "This Field is required.");
            validationStatus = false;
        }

        if (FILEPATH.equals(source)) {

            // FILEPATH is required.
            if (StringUtil.isEmpty(
                        m_queryParameterForm.getValue(FILEPATH)
                                        .trim())) {

                // noinspection HardCodedStringLiteral
                m_queryParameterForm.setErrorMessage(FILEPATH,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (PROPERTY.equals(source)) {

            // PROPERTY is required.
            if (StringUtil.isEmpty(
                        m_queryParameterForm.getValue(PROPERTY)
                                        .trim())) {

                // noinspection HardCodedStringLiteral
                m_queryParameterForm.setErrorMessage(PROPERTY,
                    "This Field is required.");
                validationStatus = false;
            }
        }
        else if (XML_CODE.equals(source)) {

            // XML_CODE is required.
            if (StringUtil.isEmpty(
                        m_queryParameterForm.getValue(XML_CODE)
                                        .trim())) {

                // noinspection HardCodedStringLiteral
                m_queryParameterForm.setErrorMessage(XML_CODE,
                    "This Field is required.");
                validationStatus = false;
            }
        }

        return validationStatus;
    }

    protected void updateRowVisibility()
    {
        String source = m_queryParameterForm.getValue(SOURCE);

        m_queryParameterForm.setRowVisible(FILEPATH, FILEPATH.equals(source));
        m_queryParameterForm.setRowVisible(PROPERTY, PROPERTY.equals(source));
        m_queryParameterForm.setRowVisible(XML_CODE, XML_CODE.equals(source));
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
        Map<String, String> actualParams    = new HashMap<String, String>();
        Map<String, String> queryFormValues = m_queryParameterForm.getValues();

        actualParams.put(SOURCE, queryFormValues.get(SOURCE));
        actualParams.put(QUERY, queryFormValues.get(QUERY));
        actualParams.put(FILEPATH, queryFormValues.get(FILEPATH));
        actualParams.put(PROPERTY, queryFormValues.get(PROPERTY));
        actualParams.put(XML_CODE, queryFormValues.get(XML_CODE));

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
                    XML_CODE
                }) {
            m_queryParameterForm.setValue(key,
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
        extends UiBinder<Widget, XPathQueryParameterPanel> { }
}
