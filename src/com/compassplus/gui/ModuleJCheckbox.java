package com.compassplus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/14/11
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModuleJCheckbox extends JCheckBox implements ModuleJButton {
    private String key;

    public ModuleJCheckbox(String text, boolean selected, String key, ProductForm form) {
        super(text);
        this.setModel(new ModuleToggleButtonModel(this, form));
        this.key = key;
        this.setSelected(selected, true);
    }

    public void setSelected(boolean b, boolean ignoreEvent) {
        ((ModuleToggleButtonModel) this.getModel()).setSelected(b, ignoreEvent);
    }

    public String getKey() {
        return key;
    }

    public ModuleButtonGroup getGroup() {
        return (ModuleButtonGroup)((ModuleToggleButtonModel) this.getModel()).getGroup();
    }

    public void dropOldSelected() {
        if (this.getModel() instanceof ModuleToggleButtonModel) {
            ((ModuleToggleButtonModel) this.getModel()).dropOldSelected();
        }
    }
}
