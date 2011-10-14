package com.compassplus.gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/14/11
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModuleJCheckbox extends JCheckBox {
    private String key;

    public ModuleJCheckbox(String text, String key) {
        super(text);
        this.key = key;
    }

    public ModuleJCheckbox(String text, boolean selected, String key) {
        super(text, selected);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
