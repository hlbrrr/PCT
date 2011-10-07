package com.compassplus.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/7/11
 * Time: 9:49 AM
 */
public class ProposalJPanel extends JPanel {
    private ProposalForm parentForm;

    public ProposalJPanel(ProposalForm parentForm) {
        super();
        this.parentForm = parentForm;
    }

    public ProposalJPanel(LayoutManager layout, ProposalForm parentForm) {
        super(layout);
        this.parentForm = parentForm;
    }

    public ProposalForm getParentForm() {
        return parentForm;
    }
}
