package com.compassplus.gui;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
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
    private JMenuItem closeProposal;
    private JMenuItem exit;

    private JMenuItem addProduct;
    private JMenuItem delProduct;
    private JMenuItem export2XLS;

    private JTabbedPane proposalsTabs;
    private ProposalForm currentProposalForm;
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
        proposalFileChooser.setAcceptAllFileFilterUsed(false);
        proposalFileChooser.setMultiSelectionEnabled(false);
        proposalFileChooser.setFileFilter(new FileNameExtensionFilter("*.xml (PCT config)", "xml"));

        xlsFileChooser = new JFileChooser();
        xlsFileChooser.setAcceptAllFileFilterUsed(false);
        xlsFileChooser.setMultiSelectionEnabled(false);
        xlsFileChooser.setFileFilter(new FileNameExtensionFilter("Excel documents", "xls", "xlsx"));
    }

    private void initFileMenu() {
        createProposal = new JMenuItem("Create proposal");
        createProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JTextField proposalNameField = new JTextField();

                final JOptionPane optionPane = new JOptionPane(
                        new JComponent[]{new JLabel("Proposal name"), proposalNameField},
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);

                final JDialog dialog = new JDialog(getFrame(), "Create proposal", true);
                dialog.setResizable(false);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        dialog.dispose();
                    }
                });
                dialog.setContentPane(optionPane);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                optionPane.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent e) {
                                if (optionPane.getValue() != null) {
                                    String prop = e.getPropertyName();
                                    if (dialog.isVisible()
                                            && (e.getSource() == optionPane)
                                            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                        if (optionPane.getValue() instanceof Integer) {
                                            int value = (Integer) optionPane.getValue();
                                            if (value == JOptionPane.OK_OPTION) {
                                                String name = proposalNameField.getText().trim();
                                                if (name.equals("")) {
                                                    JOptionPane.showMessageDialog(getRoot(),
                                                            "Proposal name shouldn't be empty",
                                                            "Error",
                                                            JOptionPane.ERROR_MESSAGE);
                                                    proposalNameField.requestFocus();
                                                } else {
                                                    dialog.dispose();
                                                    Proposal proposal = new Proposal(config);
                                                    proposal.setClientName(name);
                                                    addProposalForm(new ProposalForm(proposal));
                                                }
                                            } else if (value == JOptionPane.CANCEL_OPTION) {
                                                dialog.dispose();
                                            }
                                        }
                                        optionPane.setValue(null);
                                    }
                                }
                            }
                        }
                );
                dialog.pack();
                dialog.setLocationRelativeTo(getRoot());
                dialog.setVisible(true);
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
                        if (exception instanceof PCTDataFormatException) {
                            Logger.getInstance().error(exception);
                        }
                        JOptionPane.showMessageDialog(getRoot(), "Can't read proposal from specified file", "Error", JOptionPane.ERROR_MESSAGE);
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
                        out.write(getCurrentProposalForm().getProposal().toString().getBytes());
                        out.close();
                        JOptionPane.showMessageDialog(getRoot(), "Proposal successfully exported", "Result", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(getRoot(), "Can't save proposal to specified file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        closeProposal = new JMenuItem("Close proposal");
        closeProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                proposalsTabs.remove(getCurrentProposalForm().getRoot());
            }
        });

        exit = new JMenuItem("Exit");

        fileMenu = new JMenu("File");

        fileMenu.add(createProposal);
        //fileMenu.add(new JSeparator());
        fileMenu.add(openProposal);
        fileMenu.add(saveProposal);
        //fileMenu.add(new JSeparator());
        fileMenu.add(closeProposal);
        //fileMenu.add(new JSeparator());
        fileMenu.add(exit);
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
    }

    private void initProposalMenu() {
        addProduct = new JMenuItem("Add product");
        addProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Object> allowedProjects = new ArrayList<Object>();

                for (String key : getCurrentProposalForm().getProposal().getConfig().getProducts().keySet()) {
                    if (!getCurrentProposalForm().getProposal().getProducts().containsKey(key)) {
                        allowedProjects.add(getCurrentProposalForm().getProposal().getConfig().getProducts().get(key));
                    }
                }

                Object product = JOptionPane.showInputDialog(
                        getRoot(),
                        "Select product",
                        "Add product",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        allowedProjects.toArray(),
                        null);
                if (product != null) {
                    getCurrentProposalForm().addProductForm(new ProductForm(new com.compassplus.proposalModel.Product((com.compassplus.configurationModel.Product) product)));
                }
            }
        });
        delProduct = new JMenuItem("Remove product");
        delProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().delProductForm(getCurrentProposalForm().getCurrentProductForm());
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
                        final Workbook wb = WorkbookFactory.create(inp);
                        inp.close();
                        {
                            if (wb.getNumberOfSheets() == 0) {
                                JOptionPane.showMessageDialog(getRoot(), "Selected excel workbook is empty", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            final JTextField sheetIndexField = wb.getNumberOfSheets() > 1 ? new JTextField() : null;
                            final JTextField rowIndexField = new JTextField();
                            final JTextField cellIndexField = new JTextField();
                            if (wb.getNumberOfSheets() > 1) {
                                sheetIndexField.setText("1");
                            }
                            rowIndexField.setText("1");
                            cellIndexField.setText("1");
                            final JOptionPane optionPane = new JOptionPane(
                                    new JComponent[]{
                                            wb.getNumberOfSheets() > 1 ? new JLabel("Sheet index") : null, sheetIndexField,
                                            new JLabel("Row index"), rowIndexField,
                                            new JLabel("Cell index"), cellIndexField
                                    },
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.OK_CANCEL_OPTION);

                            final JDialog dialog = new JDialog(getFrame(), "Export position", true);
                            dialog.setResizable(false);
                            dialog.addWindowListener(new WindowAdapter() {
                                public void windowClosing(WindowEvent we) {
                                    dialog.dispose();
                                }
                            });
                            dialog.setContentPane(optionPane);
                            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                            optionPane.addPropertyChangeListener(
                                    new PropertyChangeListener() {
                                        public void propertyChange(PropertyChangeEvent e) {
                                            if (optionPane.getValue() != null) {
                                                String prop = e.getPropertyName();
                                                if (dialog.isVisible()
                                                        && (e.getSource() == optionPane)
                                                        && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                                    try {
                                                        if (optionPane.getValue() instanceof Integer) {
                                                            int value = (Integer) optionPane.getValue();
                                                            if (value == JOptionPane.OK_OPTION) {
                                                                Sheet s = null;
                                                                if (sheetIndexField != null) {
                                                                    try {
                                                                        int sheetIndex = Integer.parseInt(sheetIndexField.getText());
                                                                        if (sheetIndex > 0 && sheetIndex <= wb.getNumberOfSheets()) {
                                                                            s = wb.getSheetAt(--sheetIndex);
                                                                        }
                                                                    } catch (Exception exception) {
                                                                    }
                                                                    if (s == null) {
                                                                        JOptionPane.showMessageDialog(getRoot(), "Sheet index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                                        sheetIndexField.requestFocus();
                                                                        sheetIndexField.selectAll();
                                                                        throw new Exception();
                                                                    }
                                                                } else {
                                                                    s = wb.getSheetAt(0);
                                                                }

                                                                Integer rowIndex = null;
                                                                try {
                                                                    rowIndex = Integer.parseInt(rowIndexField.getText());
                                                                    rowIndex--;
                                                                } catch (Exception exception) {
                                                                }
                                                                if (rowIndex == null || rowIndex < 0) {
                                                                    JOptionPane.showMessageDialog(getRoot(), "Row index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                                    rowIndexField.requestFocus();
                                                                    rowIndexField.selectAll();
                                                                    throw new Exception();
                                                                }

                                                                Integer cellIndex = null;
                                                                try {
                                                                    cellIndex = Integer.parseInt(cellIndexField.getText());
                                                                    cellIndex--;
                                                                } catch (Exception exception) {
                                                                }
                                                                if (cellIndex == null || cellIndex < 0) {
                                                                    JOptionPane.showMessageDialog(getRoot(), "Cell index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                                    cellIndexField.requestFocus();
                                                                    cellIndexField.selectAll();
                                                                    throw new Exception();
                                                                }
                                                                dialog.dispose();
                                                                try {
                                                                    int i = 0;
                                                                    for (Product p : getCurrentProposalForm().getProposal().getProducts().values()) {

                                                                        if (s.getLastRowNum() >= rowIndex + i) {
                                                                            s.shiftRows(rowIndex + i, s.getLastRowNum(), getCurrentProposalForm().getProposal().getProducts().size());
                                                                        }
                                                                        Row r = s.createRow(rowIndex + i);

                                                                        Cell c1 = r.createCell(0 + cellIndex);
                                                                        CellStyle cs1 = wb.createCellStyle();
                                                                        cs1.setWrapText(true);
                                                                        c1.setCellValue(p.getDescription());
                                                                        c1.setCellStyle(cs1);

                                                                        Cell c2 = r.createCell(1 + cellIndex);
                                                                        CellStyle cs2 = wb.createCellStyle();
                                                                        cs2.setDataFormat(s.getWorkbook().createDataFormat().getFormat("$#,##0"));
                                                                        c2.setCellStyle(cs2);
                                                                        c2.setCellValue(p.getPrice());
                                                                        i++;
                                                                    }
                                                                    OutputStream out = new FileOutputStream(xlsFileChooser.getSelectedFile());
                                                                    wb.write(out);
                                                                    out.close();
                                                                    JOptionPane.showMessageDialog(getRoot(), "Proposal successfully exported", "Result", JOptionPane.INFORMATION_MESSAGE);
                                                                } catch (Exception exception) {
                                                                    JOptionPane.showMessageDialog(getRoot(), "Proposal can't be exported", "Error", JOptionPane.ERROR_MESSAGE);
                                                                }
                                                            } else if (value == JOptionPane.CANCEL_OPTION) {
                                                                dialog.dispose();
                                                            }
                                                        }
                                                    } catch (Exception exception) {
                                                        optionPane.setValue(null);
                                                    }
                                                }
                                            }
                                        }
                                    }
                            );
                            dialog.pack();
                            dialog.setLocationRelativeTo(getRoot());
                            dialog.setVisible(true);
                        }


                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(getRoot(), "Selected excel workbook can't be read", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        proposalMenu = new JMenu("Proposal");
        proposalMenu.setEnabled(false);
        proposalMenu.add(addProduct);
        proposalMenu.add(delProduct);
        proposalMenu.add(export2XLS);
        proposalMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                if (getCurrentProposalForm() != null && getCurrentProposalForm().getProposal().getProducts().size() == 0) {
                    getExport2XLS().setEnabled(false);
                } else {
                    getExport2XLS().setEnabled(true);
                }
                if (getCurrentProposalForm() != null && getCurrentProposalForm().getProposal().getProducts().size() < getCurrentProposalForm().getProposal().getConfig().getProducts().size()) {
                    getAddProduct().setEnabled(true);
                } else {
                    getAddProduct().setEnabled(false);
                }

                if (getCurrentProposalForm() != null && getCurrentProposalForm().getCurrentProductForm() != null) {
                    getDelProduct().setEnabled(true);
                } else {
                    getDelProduct().setEnabled(false);
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
                ProposalJPanel p = (ProposalJPanel) proposalsTabs.getSelectedComponent();
                if (p != null) {
                    setCurrentProposalForm(p.getParentForm());
                } else {
                    setCurrentProposalForm(null);
                }
            }
        });
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(proposalsTabs, BorderLayout.CENTER);
    }

    private ProposalForm getCurrentProposalForm() {
        return this.currentProposalForm;
    }

    private void setCurrentProposalForm(ProposalForm currentProposalForm) {
        if (currentProposalForm != null) {
            proposalMenu.setEnabled(true);
        } else {
            proposalMenu.setEnabled(false);
        }
        this.currentProposalForm = currentProposalForm;
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
        proposalsTabs.setSelectedComponent(proposalForm.getRoot());
    }

    private JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private JMenuItem getDelProduct() {
        return delProduct;
    }
}
