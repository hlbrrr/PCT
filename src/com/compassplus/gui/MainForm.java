package com.compassplus.gui;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
    private JFrame frame;

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
        xlsFileChooser.setFileFilter(new FileNameExtensionFilter("*.xls (Excel documents)", "xls", "xlsx"));
    }

    private void initFileMenu() {
        createProposal = new JMenuItem("Create proposal");
        createProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JOptionPane optionPane = new JOptionPane(
                        "The only way to close this dialog is by\n"
                                + "pressing one of the following buttons.\n"
                                + "Do you understand?",
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.YES_NO_OPTION);

                final JDialog dialog = new JDialog(getFrame(),
                        "Click a button",
                        true);
                dialog.setContentPane(optionPane);
                dialog.setDefaultCloseOperation(
                        JDialog.DO_NOTHING_ON_CLOSE);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        //setLabel("Thwarted user attempt to close window.");
                    }
                });
                optionPane.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent e) {
                                String prop = e.getPropertyName();

                                if (dialog.isVisible()
                                        && (e.getSource() == optionPane)
                                        && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                    //If you were going to check something
                                    //before closing the window, you'd do
                                    //it here.
                                    dialog.setVisible(false);
                                }
                            }
                        });
                dialog.pack();
                dialog.setVisible(true);

                int value = ((Integer) optionPane.getValue()).intValue();
                if (value == JOptionPane.YES_OPTION) {
                    //setLabel("Good.");
                } else if (value == JOptionPane.NO_OPTION) {
//    setLabel("Try using the window decorations "
//             + "to close the non-auto-closing dialog. "
//             + "You can't!");
                }

/*
String s = (String) JOptionPane.showInputDialog(
        getRoot(),
        "Proposal name",
        "Creating new proposal",
        JOptionPane.PLAIN_MESSAGE);
if (s != null) {
    String name = s.trim();
    if (name.equals("")) {
        JOptionPane.showMessageDialog(getRoot(),
                "Proposal name shouldn't be empty",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        this.actionPerformed(e);
        return;
    }
    Proposal proposal = new Proposal(config);
    proposal.setClientName(name);
    addProposalForm(new ProposalForm(proposal));
}*/
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
                        File f = proposalFileChooser.getSelectedFile();
                        OutputStream out = new FileOutputStream(f);
                        out.write(getCurrentProposal().toString().getBytes());
                        out.close();
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
        addProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Object> allowedProjects = new ArrayList<Object>();

                for (String key : getCurrentProposal().getConfig().getProducts().keySet()) {
                    if (!getCurrentProposal().getProducts().containsKey(key)) {
                        allowedProjects.add(((com.compassplus.configurationModel.Product) getCurrentProposal().getConfig().getProducts().get(key)).getName());
                    }
                }

                String s = (String) JOptionPane.showInputDialog(
                        getRoot(),
                        "Complete the sentence:\n"
                                + "\"Green eggs and...\"",
                        "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        allowedProjects.toArray(),
                        "ham");
            }
        });
        export2XLS = new JMenuItem("Export to XLS");
        export2XLS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xlsFileChooser.setDialogTitle("Export");
                int retVal = xlsFileChooser.showDialog(getRoot(), "Export");

                if (retVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileInputStream inp = new FileInputStream(xlsFileChooser.getSelectedFile());
                        Workbook wb = WorkbookFactory.create(inp);

                        Sheet s = wb.getSheetAt(0);
                        int offset = 10;
                        int i = 0;
                        for (Product p : getCurrentProposal().getProducts().values()) {
                            s.shiftRows(offset + i, s.getLastRowNum(), getCurrentProposal().getProducts().size());
                            Row r = s.createRow(offset + i);


                            Cell c1 = r.createCell(0);
                            CellStyle cs1 = wb.createCellStyle();
                            cs1.setWrapText(true);
                            c1.setCellValue(p.getDescription());
                            c1.setCellStyle(cs1);


                            Cell c2 = r.createCell(1);
                            CellStyle cs2 = wb.createCellStyle();
                            cs2.setDataFormat(s.getWorkbook().createDataFormat().getFormat("$#,##0"));
                            c2.setCellStyle(cs2);
                            c2.setCellValue(p.getPrice());
                            i++;
                        }

                        OutputStream out = new FileOutputStream(xlsFileChooser.getSelectedFile());
                        wb.write(out);
                        out.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        proposalMenu = new JMenu("Proposal");
        proposalMenu.setEnabled(false);
        proposalMenu.add(addProduct);
        proposalMenu.add(export2XLS);
        proposalMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                if (getCurrentProposal() != null && getCurrentProposal().getProducts().size() == 0) {
                    getExport2XLS().setEnabled(false);
                } else {
                    getExport2XLS().setEnabled(true);
                }
                if (getCurrentProposal().getProducts().size() < getCurrentProposal().getConfig().getProducts().size()) {
                    getAddProduct().setEnabled(true);
                } else {
                    getAddProduct().setEnabled(false);
                }
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });

        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        mainMenu.add(proposalMenu);

    }

    private JMenuItem getAddProduct() {
        return addProduct;
    }

    private void initMainPanel() {
        proposalsTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        proposalsTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setCurrentProposal(((ProposalJPanel) proposalsTabs.getSelectedComponent()).getParentForm().getProposal());
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
            proposalMenu.setEnabled(true);
        } else {
            proposalMenu.setEnabled(false);
        }
        this.currentProposal = currentProposal;
    }

    private JMenuItem getSaveProposal() {
        return saveProposal;
    }

    private JMenuItem getExport2XLS() {
        return export2XLS;
    }

    public Container getRoot() {
        return mainPanel;
    }

    public JMenuBar getMenu() {
        return mainMenu;
    }

    public void setExitAction(ActionListener actionListener) {
        exit.addActionListener(actionListener);
    }

    public void addProposalForm(ProposalForm proposalForm) {
        proposalsTabs.addTab(proposalForm.getProposal().getName(), proposalForm.getRoot());
    }

    private JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}
