package com.compassplus.gui;

import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 23:08
 */
public class ProposalForm {
    private JPanel mainPanel;
    private Proposal proposal;

    public ProposalForm(Proposal proposal) {
        this.proposal = proposal;
        mainPanel = new ProposalJPanel(new BorderLayout(), this);
    }

    public JPanel getRoot() {
        return mainPanel;
    }

    public Proposal getProposal() {
        return proposal;
    }
}
