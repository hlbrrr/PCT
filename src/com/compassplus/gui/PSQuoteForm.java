package com.compassplus.gui;

import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class PSQuoteForm {
    private JFrame frame;
    private JPanel mainPanel;
    private Proposal proposal;
    private PCTChangedListener titleUpdater;
    private DecimalFormat df;

    public PSQuoteForm(Proposal proposal, PCTChangedListener titleUpdater, DecimalFormat df, JFrame frame) {
        this.titleUpdater = titleUpdater;
        this.proposal = proposal;
        this.frame = frame;
        this.df = df;
        mainPanel = new PSQuoteJPanel(this);
        mainPanel.setLayout(new GridBagLayout());
        initForm();
    }

    private void initForm() {

    }

    public JPanel getRoot() {
        return mainPanel;
    }

    private JFrame getFrame() {
        return frame;
    }
}
