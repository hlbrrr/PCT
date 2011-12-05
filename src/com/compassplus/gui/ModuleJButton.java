package com.compassplus.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 12/5/11
 * Time: 9:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ModuleJButton {
    public abstract void setSelected(boolean b, boolean ignoreEvent);

    public abstract String getKey();

    public abstract void setText(String text);

    public void addItemListener(ItemListener l);

    public void setBorder(Border border);

    public void setMaximumSize(Dimension maximumSize);

    public void setActionCommand(String actionCommand);

    public void dropOldSelected();

    public void repaint();

    public ModuleButtonGroup getGroup();
}
