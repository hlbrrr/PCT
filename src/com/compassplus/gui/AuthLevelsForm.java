package com.compassplus.gui;

import com.compassplus.proposalModel.Proposal;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.08.12
 * Time: 22:07
 */
public class AuthLevelsForm {
    private JPanel mainPanel;
    private Proposal proposal;

    public AuthLevelsForm(Proposal proposal) {
        this.mainPanel = new JPanel();                  
        this.proposal = proposal;
    }

    public JPanel getRoot() {
        return mainPanel;
    }
}
