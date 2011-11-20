package com.compassplus.gui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 21.11.11
 * Time: 2:25
 */
public class CustomJLabel extends JLabel {
    private PCTChangedListener ls;

    public CustomJLabel(PCTChangedListener ls) {
        this.ls = ls;
    }

    public void call() {
        ls.act(this);
    }
}
