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
public class ModuleJCheckbox extends JCheckBox {
    private String key;

    private static class ModuleToggleButtonModel extends ToggleButtonModel {
        public ModuleToggleButtonModel() {
        }


        public void setSelected(boolean b) {
            setSelected(b, false);

        }

        public void setSelected(boolean b, boolean ignoreEvent) {
            ButtonGroup group = getGroup();
            if (group != null) {
                // use the group model instead
                group.setSelected(this, b);
                b = group.isSelected(this);
            }

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
            }
        }
    }

    public ModuleJCheckbox(String text, String key) {
        super(text);
        this.setModel(new ModuleToggleButtonModel());
        this.key = key;
    }

    public ModuleJCheckbox(String text, boolean selected, String key) {
        super(text);
        this.setModel(new ModuleToggleButtonModel());
        this.key = key;
        this.setSelected(selected, true);
    }

    public void setSelected(boolean b, boolean ignoreEvent) {
        ((ModuleToggleButtonModel) this.getModel()).setSelected(b, ignoreEvent);
    }

    public String getKey() {
        return key;
    }
}
