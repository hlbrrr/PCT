package com.compassplus.gui;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 22:35
 */
public class MainForm {
    private JPanel mainPanel;
    private JMenuBar mainMenu;
    private JMenu fileMenu;
    private JMenu proposalMenu;
    private JMenuItem createProposal;
    private JMenuItem openProposal;
    private JMenuItem saveProposal;
    private JMenuItem exit;

    private JMenuItem addProduct;
    private JMenuItem export2XLS;
    private Map<String, ProposalForm> proposalForms;

    private JTabbedPane proposalsTabs;
    private ProposalForm currentProposalForm;

    public MainForm() {
        proposalForms = new HashMap<String, ProposalForm>();
        createProposal = new JMenuItem("Create project");
        openProposal = new JMenuItem("Open project");
        saveProposal = new JMenuItem("Save project");
        exit = new JMenuItem("Exit");

        fileMenu = new JMenu("File");
        fileMenu.add(createProposal);
        fileMenu.add(new JSeparator());
        fileMenu.add(openProposal);
        fileMenu.add(saveProposal);
        fileMenu.add(new JSeparator());
        fileMenu.add(exit);

        addProduct = new JMenuItem("Add product");
        export2XLS = new JMenuItem("Export to XLS");

        proposalMenu = new JMenu("Project");
        proposalMenu.setEnabled(false);
        proposalMenu.add(addProduct);
        proposalMenu.add(export2XLS);

        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        mainMenu.add(proposalMenu);

        fileMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                if (getCurrentProposalForm() == null) {
                    getSaveProposal().setEnabled(false);
                } else {
                    getSaveProposal().setEnabled(true);
                }
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });

        proposalsTabs = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(proposalsTabs, BorderLayout.CENTER);
        //mainPanel.add(toolbar, BorderLayout.NORTH);

    }

    private ProposalForm getCurrentProposalForm() {
        return this.currentProposalForm;
    }

    private void setCurrentProposalForm(ProposalForm currentProposalForm) {
        if (currentProposalForm != null) {
            getProposalMenu().setEnabled(true);
        } else {
            getProposalMenu().setEnabled(false);
        }
        this.currentProposalForm = currentProposalForm;
    }

    private JMenuItem getSaveProposal() {
        return saveProposal;
    }

    private JMenu getProposalMenu() {
        return proposalMenu;
    }

    public Container getRoot() {
        return mainPanel;
    }

    public JMenuBar getMenu() {
        return mainMenu;
    }

    public void setExitAction(ActionListener actionListener) {
        this.exit.addActionListener(actionListener);
    }

    private Map<String, ProposalForm> getProposalForms() {
        return proposalForms;
    }

    private JTabbedPane getProposalsTabs() {
        return proposalsTabs;
    }

    public void addProposalForm(ProposalForm proposalForm) {
        this.getProposalForms().put(proposalForm.getProposal().getClientName(), proposalForm);
        this.getProposalsTabs().addTab(proposalForm.getProposal().getClientName(), proposalForm.getRoot());
        if (this.getCurrentProposalForm() == null) {
            this.setCurrentProposalForm(proposalForm);
        }
    }
}
