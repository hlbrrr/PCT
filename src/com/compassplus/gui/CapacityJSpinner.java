package com.compassplus.gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/14/11
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CapacityJSpinner extends JSpinner {
    private String key;

    public CapacityJSpinner(SpinnerModel spinnerModel, String key) {
        super(spinnerModel);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
