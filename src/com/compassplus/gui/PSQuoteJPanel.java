package com.compassplus.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class PSQuoteJPanel extends JPanel {
    private PSQuoteForm parentForm;

    public PSQuoteJPanel(PSQuoteForm parentForm) {
        super();
        this.parentForm = parentForm;
    }
    public PSQuoteJPanel(LayoutManager layout, PSQuoteForm parentForm) {
        super(layout);
        this.parentForm = parentForm;
    }

    public PSQuoteForm getParentForm() {
        return parentForm;
    }
}
