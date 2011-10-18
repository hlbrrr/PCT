package com.compassplus.gui;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;

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

        JFormattedTextField tf = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
        DefaultFormatterFactory formatterFactory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DecimalFormat df = new DecimalFormat();
        ((NumberFormatter) formatterFactory.getDefaultFormatter()).setFormat(df);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
