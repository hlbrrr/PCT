package com.compassplus.gui;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 22:35
 */
public class MainForm {
    private JFileChooser proposalFileChooser;
    private JFileChooser xlsFileChooser;
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

    private JTabbedPane proposalsTabs;
    private Proposal currentProposal;

    private Configuration config;

    public MainForm(Configuration config) {
        this.config = config;
        initFileChoosers();
        initFileMenu();
        initProposalMenu();
        initMainPanel();
    }

    private void initFileChoosers() {
        proposalFileChooser = new JFileChooser();
        //proposalFileChooser.setDialogTitle("Choose proposal file");
        proposalFileChooser.setAcceptAllFileFilterUsed(false);
        proposalFileChooser.setMultiSelectionEnabled(false);
        proposalFileChooser.setFileFilter(new FileNameExtensionFilter("*.xml (PCT config)", "xml"));

        xlsFileChooser = new JFileChooser();
        xlsFileChooser.setAcceptAllFileFilterUsed(false);
        xlsFileChooser.setMultiSelectionEnabled(false);
        xlsFileChooser.setFileFilter(new FileNameExtensionFilter("*.xls (Excel documents)", "xls"));
    }

    private void initFileMenu() {
        createProposal = new JMenuItem("Create proposal");
        createProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Proposal proposal = new Proposal(config);
                proposal.setClientName("Test");
                addProposalForm(new ProposalForm(proposal));
            }
        });
        openProposal = new JMenuItem("Import proposal");
        openProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                proposalFileChooser.setDialogTitle("Import");
                int retVal = proposalFileChooser.showDialog(getRoot(), "Import");
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        Proposal proposal = new Proposal(config);
                        proposal.init(CommonUtils.getInstance().getDocumentFromFile(proposalFileChooser.getSelectedFile()));
                        addProposalForm(new ProposalForm(proposal));
                    } catch (Exception exception) {
                    }
                }
            }
        });
        saveProposal = new JMenuItem("Export proposal");
        saveProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                proposalFileChooser.setDialogTitle("Export");
                int retVal = proposalFileChooser.showDialog(getRoot(), "Export");
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    try {

                    } catch (Exception exception) {
                    }
                }
            }
        });
        exit = new JMenuItem("Exit");

        fileMenu = new JMenu("File");
        fileMenu.add(createProposal);
        fileMenu.add(new JSeparator());
        fileMenu.add(openProposal);
        fileMenu.add(saveProposal);
        fileMenu.add(new JSeparator());
        fileMenu.add(exit);
        fileMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                if (getCurrentProposal() == null) {
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
    }

    private void initProposalMenu() {
        addProduct = new JMenuItem("Add product");
        export2XLS = new JMenuItem("Export to XLS");
        export2XLS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xlsFileChooser.setDialogTitle("Export");
                int retVal = xlsFileChooser.showDialog(getRoot(), "Export");

                if (retVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileOutputStream out = new FileOutputStream(xlsFileChooser.getSelectedFile());
                        getCurrentProposal().getWorkbook().write(out);
                        out.close();
                    } catch (Exception exception) {
                    }
                }
            }
        });

        proposalMenu = new JMenu("Project");
        proposalMenu.setEnabled(false);
        proposalMenu.add(addProduct);
        proposalMenu.add(export2XLS);

        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        mainMenu.add(proposalMenu);

    }

    private void initMainPanel() {
        proposalsTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        proposalsTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setCurrentProposal(((ProposalJPanel) getProposalTabs().getSelectedComponent()).getParentForm().getProposal());
            }
        });
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(proposalsTabs, BorderLayout.CENTER);
    }

    private Proposal getCurrentProposal() {
        return this.currentProposal;
    }

    private void setCurrentProposal(Proposal currentProposal) {
        if (currentProposal != null) {
            getProposalMenu().setEnabled(true);
        } else {
            getProposalMenu().setEnabled(false);
        }
        this.currentProposal = currentProposal;
    }

    private JMenuItem getSaveProposal() {
        return saveProposal;
    }

    private JMenu getProposalMenu() {
        return proposalMenu;
    }

    private JTabbedPane getProposalTabs() {
        return this.proposalsTabs;
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

    private JTabbedPane getProposalsTabs() {
        return proposalsTabs;
    }

    public void addProposalForm(ProposalForm proposalForm) {
        this.getProposalsTabs().addTab(proposalForm.getProposal().getClientName(), proposalForm.getRoot());
    }
}
