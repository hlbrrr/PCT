package com.compassplus.gui;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 12/5/11
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModuleToggleButtonModel extends JToggleButton.ToggleButtonModel {

    private ModuleJButton src;

    public ModuleToggleButtonModel(ModuleJButton src) {
        this.src = src;
    }

    public void setSelected(boolean b) {
        setSelected(b, false);

    }

    public void setSelected(boolean b, boolean ignoreEvent) {
        if (isSelected() == b) {
            return;
        }
        if (b) {
            stateMask |= SELECTED;
        } else {
            stateMask &= ~SELECTED;
        }
        if (!ignoreEvent) {
            fireStateChanged();

            fireItemStateChanged(
                    new ItemEvent(this,
                            ItemEvent.ITEM_STATE_CHANGED,
                            this,
                            this.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
        } else {
            src.repaint();
        }
    }
}
