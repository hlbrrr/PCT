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
    private ProductForm form;

    public ModuleToggleButtonModel(ModuleJButton src, ProductForm form) {
        this.form = form;
        this.src = src;
    }

    public void setSelected(boolean b) {
        setSelected(b, false);

    }

   /* public ModuleButtonGroup getGroup() {
        return (ModuleButtonGroup) getGroup();
    }*/

    public void dropOldSelected() {
        ButtonGroup group = getGroup();
        if (group != null && group instanceof ModuleButtonGroup) {
            ((ModuleButtonGroup) group).dropOldSelected();
        }

    }

    public void setSelected(boolean b, boolean ignoreEvent) {
        /*ModuleButtonGroup group = (ModuleButtonGroup) getGroup();
        if (group != null) {
            // use the group model instead
            group.setSelected(this, b, ignoreEvent);
            b = group.isSelected(this);
        }*/

        if (isSelected() == b) {
            return;
        }

        if (b) {
            stateMask |= SELECTED;
        } else {
            stateMask &= ~SELECTED;
        }
        if (!ignoreEvent) {
            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
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
