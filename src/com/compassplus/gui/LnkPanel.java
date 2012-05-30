package com.compassplus.gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 30.05.12
 * Time: 22:38
 */
public class LnkPanel {
    private JPanel panel;
    private JLabel label;
    public LnkPanel(JPanel panel, JLabel label){
        this.label = label;
        this.panel = panel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JLabel getLabel() {
        return label;
    }
}
