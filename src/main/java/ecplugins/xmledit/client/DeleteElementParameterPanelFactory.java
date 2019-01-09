// DeleteElementParameterPanelFactory.java --
//
// DeleteElementParameterPanelFactory.java is part of ElectricCommander.
//
// Copyright (c) 2005-2011 Electric Cloud, Inc.
// All rights reserved.
//

package ecplugins.xmledit.client;

import com.electriccloud.commander.gwt.client.Component;
import com.electriccloud.commander.gwt.client.ComponentBaseFactory;
import com.electriccloud.commander.gwt.client.ComponentContext;
import org.jetbrains.annotations.NotNull;

public class DeleteElementParameterPanelFactory extends ComponentBaseFactory {

    @NotNull
    @Override
    public Component createComponent(ComponentContext jso) {
        return new DeleteElementParameterPanel();
    }
}
