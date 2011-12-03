package com.compassplus.gui;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;

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
    //private JFileChooser proposalFileChooser;
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
        /*proposalFileChooser = new JFileChooser();
        proposalFileChooser.setAcceptAllFileFilterUsed(false);
        proposalFileChooser.setMultiSelectionEnabled(false);
        proposalFileChooser.setFileFilter(new FileNameExtensionFilter("*.xml (PCT config)", "xml"));*/

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
                                                    proposal.setName(name);
                                                    addProposalForm(new ProposalForm(proposal, getFrame()));
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        xlsFileChooser.setDialogTitle("Import");
                        int retVal = xlsFileChooser.showDialog(getRoot(), "Import");
                        if (retVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                Proposal proposal = new Proposal(config);

                                FileInputStream inp = new FileInputStream(xlsFileChooser.getSelectedFile());
                                final Workbook wb = WorkbookFactory.create(inp);
                                inp.close();
                                Sheet s = wb.getSheet("PCTSettings");
                                String proposalString = null;
                                if (s != null) {
                                    Row r = s.getRow(0);
                                    if (r != null) {
                                        Cell c = r.getCell(0);
                                        if (c != null) {
                                            proposalString = c.getStringCellValue();
                                        }
                                    }
                                }
                                if (proposalString == null) {
                                    throw new PCTDataFormatException("Proposal not found");
                                }
                                proposal.init(CommonUtils.getInstance().getDocumentFromString(proposalString));
                                addProposalForm(new ProposalForm(proposal, getFrame()));
                                if (proposal.containsDeprecated()) {
                                    JOptionPane.showMessageDialog(getRoot(), "Selected proposal contains deprecated module(s) or capacity(ies).", "Warning", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                if (exception instanceof PCTDataFormatException) {
                                    Logger.getInstance().error(exception);
                                }
                                JOptionPane.showMessageDialog(getRoot(), "Can't read proposal from specified file", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }


                        /*proposalFileChooser.setDialogTitle("Import");
                   int retVal = proposalFileChooser.showDialog(getRoot(), "Import");
                   if (retVal == JFileChooser.APPROVE_OPTION) {
                       try {
                           Proposal proposal = new Proposal(config);
                           proposal.init(CommonUtils.getInstance().getDocumentFromFile(proposalFileChooser.getSelectedFile()));
                           addProposalForm(new ProposalForm(proposal));
                           if (proposal.containsDeprecated()) {
                               JOptionPane.showMessageDialog(getRoot(), "Selected proposal contains deprecated module(s) or capacity(ies).", "Warning", JOptionPane.INFORMATION_MESSAGE);
                           }
                       } catch (Exception exception) {
                           exception.printStackTrace();
                           if (exception instanceof PCTDataFormatException) {
                               Logger.getInstance().error(exception);
                           }
                           JOptionPane.showMessageDialog(getRoot(), "Can't read proposal from specified file", "Error", JOptionPane.ERROR_MESSAGE);
                       }
                   }     */
                    }
                });
            }
        });
        saveProposal = new JMenuItem("Export proposal");

        saveProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (checkForConsistence()) {
                            xlsFileChooser.setDialogTitle("Export");
                            int retVal = xlsFileChooser.showDialog(getRoot(), "Export");

                            if (retVal == JFileChooser.APPROVE_OPTION) {

                                try {
                                    FileInputStream inp = new FileInputStream(xlsFileChooser.getSelectedFile());
                                    final Workbook wb = WorkbookFactory.create(inp);
                                    inp.close();
                                    {
                                        final ArrayList<String> sheets = new ArrayList<String>(0);
                                        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                                            if (!(wb.isSheetHidden(i) || wb.isSheetVeryHidden(i))) {
                                                sheets.add(wb.getSheetName(i));
                                            }
                                        }
                                        if (sheets.size() == 0) {
                                            JOptionPane.showMessageDialog(getRoot(), "Selected excel workbook is empty", "Error", JOptionPane.ERROR_MESSAGE);
                                            return;
                                        }

                                        Integer rowsCountInt = null;
                                        Integer cellIndexInt = null;
                                        Integer rowIndexInt = null;
                                        String sheetIndexStr = null;
                                        Sheet settingsSheet = wb.getSheet("PCTSettings");
                                        if (settingsSheet != null) {
                                            Row currentSettingsRow = settingsSheet.getRow(0);
                                            if (currentSettingsRow != null) {
                                                Cell rowsCountCell = currentSettingsRow.getCell(1);
                                                Cell cellIndexCell = currentSettingsRow.getCell(2);
                                                Cell rowIndexCell = currentSettingsRow.getCell(3);
                                                Cell sheetIndexCell = currentSettingsRow.getCell(4);
                                                if (rowsCountCell != null && rowIndexCell != null && sheetIndexCell != null && cellIndexCell != null) {
                                                    try {
                                                        rowsCountInt = Integer.parseInt(rowsCountCell.getStringCellValue());
                                                        cellIndexInt = Integer.parseInt(cellIndexCell.getStringCellValue());
                                                        rowIndexInt = Integer.parseInt(rowIndexCell.getStringCellValue());
                                                        sheetIndexStr = sheetIndexCell.getStringCellValue();
                                                        for (int j = 0; j < rowsCountInt; j++) {
                                                            removeRow(wb.getSheet(sheetIndexStr), rowIndexInt);
                                                        }
                                                        rowIndexInt++;
                                                        cellIndexInt++;
                                                    } catch (Exception ex) {
                                                    }
                                                }
                                            }
                                        }

                                        final JComboBox sheetIndexField = sheets.size() > 1 ? new JComboBox(sheets.toArray()) : null;
                                        if (sheetIndexField != null && sheetIndexStr != null) {
                                            for (String key : sheets) {
                                                if (key.equals(sheetIndexStr)) {
                                                    sheetIndexField.setSelectedItem(key);
                                                    break;
                                                }
                                            }
                                        }
                                        final JTextField rowIndexField = new JTextField(rowIndexInt != null ? rowIndexInt.toString() : "1");
                                        final JTextField cellIndexField = new JTextField(cellIndexInt != null ? cellIndexInt.toString() : "1");
                                        final JOptionPane optionPane = new JOptionPane(
                                                new JComponent[]{
                                                        sheets.size() > 1 ? new JLabel("Sheet") : null, sheetIndexField,
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
                                                                                    s = wb.getSheet((String) sheetIndexField.getSelectedItem());
                                                                                } catch (Exception exception) {
                                                                                }
                                                                            } else {
                                                                                s = wb.getSheet(sheets.get(0));
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
                                                                                Sheet settingsSheet = wb.getSheet("PCTSettings");
                                                                                if (settingsSheet == null) {
                                                                                    settingsSheet = wb.createSheet("PCTSettings");
                                                                                }
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
                                                                                    String format = (getCurrentProposalForm().getProposal().getCurrency().getSymbol() != null ?
                                                                                            "\"" + getCurrentProposalForm().getProposal().getCurrency().getSymbol() + "\" " : "") + "#,##0" +
                                                                                            (getCurrentProposalForm().getProposal().getCurrency().getSymbol() == null ?
                                                                                                    " \"" + getCurrentProposalForm().getProposal().getCurrency().getName() + "\"" : "");
                                                                                    cs2.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                                                    c2.setCellStyle(cs2);
                                                                                    c2.setCellValue(p.getPrice());
                                                                                    i++;
                                                                                }
                                                                                Row settingsRow = settingsSheet.getRow(0);
                                                                                if (settingsRow == null) {
                                                                                    settingsRow = settingsSheet.createRow(0);
                                                                                }
                                                                                CellUtil.createCell(settingsRow, 0, getCurrentProposalForm().getProposal().toString());
                                                                                CellUtil.createCell(settingsRow, 1, new Integer(getCurrentProposalForm().getProposal().getProducts().size()).toString());
                                                                                CellUtil.createCell(settingsRow, 2, cellIndex.toString());
                                                                                CellUtil.createCell(settingsRow, 3, rowIndex.toString());
                                                                                CellUtil.createCell(settingsRow, 4, s.getSheetName());
                                                                                wb.setSheetHidden(wb.getSheetIndex(settingsSheet), true);
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


                            /*proposalFileChooser.setDialogTitle("Export");
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
                           } */
                        }
                    }
                });
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
                if (getCurrentProposalForm() != null) {
                    getCloseProposal().setEnabled(true);
                } else {
                    getCloseProposal().setEnabled(false);
                }
                if (getCurrentProposalForm() != null && getCurrentProposalForm().getProposal().getProducts().size() > 0) {
                    getSaveProposal().setEnabled(true);
                } else {
                    getSaveProposal().setEnabled(false);
                }
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });
    }

    private boolean checkForConsistence() {
        StringBuilder sb = new StringBuilder();
        for (Product p : getCurrentProposalForm().getProposal().getProducts().values()) {
            StringBuilder sbDeprecated = new StringBuilder();
            StringBuilder sbDependencies = new StringBuilder();
            for (String key : p.getModules().keySet()) {
                for (String rkey : p.getProduct().getModules().get(key).getRequireModules()) {
                    if (!p.getModules().containsKey(rkey)) {
                        sbDependencies.append("\nModule \"").append(p.getProduct().getModules().get(key).getPath()).append("\" requires disabled module \"").append(p.getProduct().getModules().get(rkey).getPath()).append("\"");
                    }
                }
                for (String rkey : p.getProduct().getModules().get(key).getExcludeModules()) {
                    if (p.getModules().containsKey(rkey)) {
                        sbDependencies.append("\nModule \"").append(p.getProduct().getModules().get(key).getPath()).append("\" conflicts with module \"").append(p.getProduct().getModules().get(rkey).getPath()).append("\"");
                    }
                }
                if (p.getProduct().getModules().get(key).isDeprecated()) {
                    sbDeprecated.append("\nDeprecated module \"").append(p.getProduct().getModules().get(key).getPath()).append("\"");
                }
            }
            for (String key : p.getCapacities().keySet()) {
                if (p.getProduct().getCapacities().get(key).isDeprecated()) {
                    sbDeprecated.append("\nDeprecated capacity \"").append(p.getProduct().getCapacities().get(key).getPath()).append("\"");
                }
            }
            if (sbDependencies.length() > 0 || sbDeprecated.length() > 0) {
                sb.append("\nProduct ").append(p.getName()).append(" contains following error(s):").append(sbDependencies).append(sbDeprecated);
            }
        }
        if (sb.length() > 0) {
            sb.append("\n\nYou should fix error(s) first, then try again.");
            JOptionPane.showMessageDialog(getRoot(), "Current proposal is not correct: " + sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
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
                    getCurrentProposalForm().addProductForm(new com.compassplus.proposalModel.Product((com.compassplus.configurationModel.Product) product, getCurrentProposalForm().getProposal()));
                }
            }
        });
        delProduct = new JMenuItem("Remove product");
        delProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().delProductForm(getCurrentProposalForm().getCurrentProductForm());
            }
        });

        proposalMenu = new JMenu("Proposal");
        proposalMenu.setEnabled(false);
        proposalMenu.add(addProduct);
        proposalMenu.add(delProduct);
        proposalMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
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

    private void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    public JMenuItem getCloseProposal() {
        return closeProposal;
    }

}
