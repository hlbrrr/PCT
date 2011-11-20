package com.compassplus.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.10.11
 * Time: 1:15
 */
public class SummaryJPanel extends JPanel {
    private SummaryForm parentForm;

    public SummaryJPanel(SummaryForm parentForm) {
        super();
        this.parentForm = parentForm;
    }

    public SummaryJPanel(LayoutManager layout, SummaryForm parentForm) {
        super(layout);
        this.parentForm = parentForm;
    }

    public SummaryForm getParentForm() {
        return parentForm;
    }

}
