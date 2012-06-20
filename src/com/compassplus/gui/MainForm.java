package com.compassplus.gui;

import com.compassplus.configurationModel.Capacity;
import com.compassplus.configurationModel.Configuration;
import com.compassplus.configurationModel.Module;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import net.iharder.dnd.FileDrop;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 22:35
 */
public class MainForm {
    //private JFileChooser proposalFileChooser;

    private static short __REMOVE = 1;
    private static short __INSERT = 2;
    private JFileChooser xlsFileChooser;
    private JPanel mainPanel;
    private JMenuBar mainMenu;
    private JMenu fileMenu;
    private JMenu proposalMenu;
    private JMenu helpMenu;
    //private JMenu viewMenu;
    private JMenuItem createProposal;
    private JMenuItem openProposal;
    private JMenuItem saveProposal;
    private JMenuItem applyTemplate;
    private JMenuItem closeProposal;
    private JMenuItem exit;
    private JMenuItem about;
    private JMenuItem rollUp;

    private JMenuItem addProduct;
    private JMenuItem delProduct;

    private JTabbedPane proposalsTabs;
    private ProposalForm currentProposalForm;
    private JFrame frame;

    private Configuration config;

    private String TEMPLATES_DIR = "templates";
    private ArrayList<XLSTemplate> templatesList;

    private String CURRENT_VERSION = "1.0";

    public MainForm(Configuration config) {
        this.config = config;
        initFileChoosers();
        initFileMenu();
        initHelpMenu();
        initProposalMenu();
        // initViewMenu();
        initMainPanel();

        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        //mainMenu.add(viewMenu);
        mainMenu.add(proposalMenu);
        mainMenu.add(helpMenu);
    }

    public boolean checkChanges() {
        String proposalName = "";
        for (Component c : proposalsTabs.getComponents()) {
            if (c instanceof ProposalJPanel) {
                ProposalJPanel p = (ProposalJPanel) c;
                if (p.getParentForm().isChanged() && p.getParentForm().getProposal().getProducts().size() > 0) {
                    proposalName += "\"" + p.getParentForm().getProposal().getProjectName() + " [" + p.getParentForm().getProposal().getClientName() + "]\", ";
                }
            }
        }
        if (proposalName.length() > 0) {
            int choice = JOptionPane.showOptionDialog(getRoot(), "There are unsaved changes in following proposal(s): " + proposalName.substring(0, proposalName.length() - 2) + ". Do you really want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);//Dialog(getRoot(), "Selected excel workbook can't be read", "Error", JOptionPane.ERROR_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    private void initHelpMenu() {
        about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JLabel version = new JLabel("<html><b>PCT Version:</b> " + CURRENT_VERSION + "</html>");
                version.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel expiration = new JLabel("<html><b>Configuration expires:</b> " + config.getExpirationDateString() + "</html>");
                expiration.setAlignmentX(Component.LEFT_ALIGNMENT);
                expiration.setBackground(Color.green);

                JLabel text = new JLabel("Online user service can be found at ");
                JLabel link = new JLabel("<html><a href=\"#\">pct.compassplus.ru</a></html>");
                link.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                link.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        JLabel l = (JLabel) evt.getSource();
                        try {
                            Desktop desktop = java.awt.Desktop.getDesktop();
                            URI uri = new java.net.URI("http://pct.compassplus.ru");
                            desktop.browse(uri);
                        } catch (URISyntaxException use) {
                            throw new AssertionError(use);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Sorry, a problem occurred while trying to open this link in your system's standard browser.", "A problem occured", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                });


                JPanel msgBottom = new JPanel();
                msgBottom.setLayout(new FlowLayout(0, 0, 0));
                msgBottom.add(text);
                msgBottom.add(link);
                msgBottom.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel msg = new JPanel();
                BoxLayout lt = new BoxLayout(msg, BoxLayout.Y_AXIS);
                msg.setLayout(lt);

                msg.add(version);
                msg.add(expiration);
                msg.add(Box.createRigidArea(new Dimension(0, 10)));
                msg.add(msgBottom);
                JOptionPane.showMessageDialog(
                        null, msg, "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        helpMenu = new JMenu("Help");
        helpMenu.add(about);
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void copyAndSave(XLSTemplate template, String destination) {
        try {
            destination = template.formatDestination(destination);

            copyFile(new File(template.getFullName()), new File(destination));
            try {
                save(destination);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(getRoot(), "Template is broken", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getRoot(), "Can't copy template to given destination", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save(String file) throws IOException, InvalidFormatException {
        final String sfile = file;
        FileInputStream inp = new FileInputStream(sfile);
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
            Integer sheetIndexInt = null;
            Sheet settingsSheet = wb.getSheet("PCTSettings");
            final List<RowStyle> rowStyles = new ArrayList<RowStyle>();
            boolean sameCurrency = false;
            if (settingsSheet != null) {
                Row currentSettingsRow = settingsSheet.getRow(0);
                if (currentSettingsRow != null) {
                    Cell oldProposalCell = currentSettingsRow.getCell(0);
                    Cell rowsCountCell = currentSettingsRow.getCell(1);
                    Cell cellIndexCell = currentSettingsRow.getCell(2);
                    Cell rowIndexCell = currentSettingsRow.getCell(3);
                    Cell sheetIndexCell = currentSettingsRow.getCell(4);
                    try {
                        Proposal oldProposal = new Proposal(config);
                        String proposalString = oldProposalCell.getStringCellValue();
                        if (proposalString != null) {
                            oldProposal.init(CommonUtils.getInstance().getDocumentFromString(proposalString));
                            sameCurrency = getCurrentProposalForm().getProposal().getCurrency().getName().equals(oldProposal.getCurrency().getName());
                        }
                    } catch (Exception e) {

                    }

                    if (rowsCountCell != null && rowIndexCell != null && sheetIndexCell != null && cellIndexCell != null) {
                        try {
                            rowsCountInt = Integer.parseInt(rowsCountCell.getStringCellValue());
                            cellIndexInt = Integer.parseInt(cellIndexCell.getStringCellValue());
                            rowIndexInt = Integer.parseInt(rowIndexCell.getStringCellValue());
                            rowIndexInt++;
                            cellIndexInt++;
                            //sheetIndexInt = Integer.parseInt(sheetIndexCell.getStringCellValue());
                            sheetIndexStr = sheetIndexCell.getStringCellValue();
                            if (wb.getSheet(sheetIndexStr) != null) {
                                //sheetIndexStr = wb.getSheetAt(sheetIndexInt).getSheetName();
                                for (int j = 0; j < rowsCountInt; j++) {
                                    RowStyle rowStyle = new RowStyle();
                                    rowStyle.init(wb.getSheet(sheetIndexStr).getRow(rowIndexInt - 1));
                                    rowStyles.add(rowStyle);
                                    removeRow(wb.getSheet(sheetIndexStr), rowIndexInt - 1);
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                } else {
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

            final boolean fSameCurrency = sameCurrency;
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
                                                    } else {

                                                    }
                                                    if (rowStyles.size() == 0) {
                                                        RowStyle rowStyle = new RowStyle();
                                                        rowStyle.init(s.getRow(rowIndex));
                                                        rowStyles.add(rowStyle);
                                                    }
                                                    String regPriceCol = CellReference.convertNumToColString(1 + cellIndex);
                                                    String regPriceDiscount = CellReference.convertNumToColString(2 + cellIndex);
                                                    String supPriceCol = CellReference.convertNumToColString(4 + cellIndex);
                                                    String supPriceDiscount = CellReference.convertNumToColString(5 + cellIndex);
                                                    int currentRowIndex = 0;
                                                    for (Product p : getCurrentProposalForm().getProposal().getProducts().values()) {
                                                        if (s.getLastRowNum() >= rowIndex + i) {
                                                            s.shiftRows(rowIndex + i, s.getLastRowNum(), 1);
                                                        }
                                                        RowStyle rowStyle = rowStyles.get((int) (currentRowIndex - rowStyles.size() * Math.floor(currentRowIndex / rowStyles.size())));
                                                        currentRowIndex++;
                                                        Row r = s.createRow(rowIndex + i);
                                                        for (int y = rowStyle.getFirst(); y < 0 + cellIndex; y++) {
                                                            CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                            if (tcs != null) {
                                                                Cell tc = r.createCell(y);
                                                                tc.setCellStyle(tcs);
                                                            }
                                                        }

                                                        Cell c1 = r.createCell(0 + cellIndex);
                                                        CellStyle cs1 = rowStyle.getCellStyle(0 + cellIndex, wb.createCellStyle());
                                                        cs1.setWrapText(true);
                                                        c1.setCellValue(p.getDescription());
                                                        c1.setCellStyle(cs1);

                                                        Cell c2 = r.createCell(1 + cellIndex);
                                                        CellStyle cs2 = rowStyle.getCellStyle(1 + cellIndex, wb.createCellStyle());
                                                        String format = (getCurrentProposalForm().getProposal().getCurrency().getSymbol() != null ?
                                                                "\"" + getCurrentProposalForm().getProposal().getCurrency().getSymbol() + "\" " : "") + "#,##0" +
                                                                (getCurrentProposalForm().getProposal().getCurrency().getSymbol() == null ?
                                                                        " \"" + getCurrentProposalForm().getProposal().getCurrency().getName() + "\"" : "");
                                                        if (!fSameCurrency)
                                                            cs2.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                        c2.setCellStyle(cs2);
                                                        c2.setCellValue(p.getProposal().getConfig().isSalesSupport() ? 0 : p.getRegionPrice());

                                                        Cell c3 = r.createCell(2 + cellIndex);
                                                        CellStyle cs3 = rowStyle.getCellStyle(2 + cellIndex, wb.createCellStyle());
                                                        cs3.setDataFormat(s.getWorkbook().createDataFormat().getFormat("0%;-0%"));
                                                        //cs3.setDataFormat(s.getWorkbook().createDataFormat().getFormat("0%;-0%;;@"));
                                                        c3.setCellStyle(cs3);
                                                        c3.setCellValue(p.getProposal().getConfig().isSalesSupport() ? 0 : p.getDiscount());

                                                        Cell c4 = r.createCell(3 + cellIndex);
                                                        CellStyle cs4 = rowStyle.getCellStyle(3 + cellIndex, wb.createCellStyle());
                                                        if (!fSameCurrency)
                                                            cs4.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                        c4.setCellStyle(cs4);
                                                        int rowIndexTotal = rowIndex + i + 1;
                                                        //c4.setCellValue(p.getEndUserPrice());
                                                        c4.setCellFormula("CEILING(" + regPriceCol + rowIndexTotal + "*(1-" + regPriceDiscount + rowIndexTotal + "),1)");

                                                        Cell c5 = r.createCell(4 + cellIndex);
                                                        CellStyle cs5 = rowStyle.getCellStyle(4 + cellIndex, wb.createCellStyle());
                                                        if (!fSameCurrency)
                                                            cs5.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                        c5.setCellStyle(cs5);
                                                        c5.setCellValue(p.getProposal().getConfig().isSalesSupport() ? 0 : p.getSupportPriceUndiscounted());

                                                        Cell c6 = r.createCell(5 + cellIndex);
                                                        CellStyle cs6 = rowStyle.getCellStyle(5 + cellIndex, wb.createCellStyle());
                                                        cs6.setDataFormat(s.getWorkbook().createDataFormat().getFormat("0%;-0%"));
                                                        c6.setCellStyle(cs6);
                                                        c6.setCellValue(p.getProposal().getConfig().isSalesSupport() ? 0 : p.getSupportDiscount());

                                                        Cell c7 = r.createCell(6 + cellIndex);
                                                        CellStyle cs7 = rowStyle.getCellStyle(6 + cellIndex, wb.createCellStyle());
                                                        if (!fSameCurrency)
                                                            cs7.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                        c7.setCellStyle(cs7);
                                                        //c7.setCellValue(p.getSupportPrice());
                                                        c7.setCellFormula("CEILING(" + supPriceCol + rowIndexTotal + "*(1-" + supPriceDiscount + rowIndexTotal + "),1)");

                                                        for (int y = 7 + cellIndex; y <= rowStyle.getLast(); y++) {
                                                            CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                            if (tcs != null) {
                                                                Cell tc = r.createCell(y);
                                                                tc.setCellStyle(tcs);
                                                            }
                                                        }
                                                        i++;
                                                    }
                                                    ArrayList<Integer> rowsToRemove = new ArrayList();
                                                    ScriptEngineManager factory = new ScriptEngineManager();
                                                    ScriptEngine engine = factory.getEngineByName("JavaScript");
                                                    Bindings bindings = getBindings(getCurrentProposalForm().getProposal(), engine);
                                                    for (int si = 0; si < wb.getNumberOfSheets(); si++) {
                                                        Sheet sis = wb.getSheetAt(si);
                                                        Iterator<Row> riter = sis.rowIterator();
                                                        while (riter.hasNext()) {
                                                            Row row = riter.next();
                                                            Iterator<Cell> citer = row.cellIterator();
                                                            while (citer.hasNext()) {
                                                                Cell cell = citer.next();
                                                                if (analyzeCell(row, cell, engine, bindings)) {
                                                                    rowsToRemove.add(row.getRowNum());
                                                                }
                                                            }
                                                        }
                                                        for (int ri : rowsToRemove) {
                                                            removeRow(sis, ri);
                                                        }
                                                        rowsToRemove.clear();
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
                                                    OutputStream out = new FileOutputStream(sfile);
                                                    wb.write(out);
                                                    out.close();
                                                    getCurrentProposalForm().setChanged(false);
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
    }

    private void initFileChoosers() {
        /*proposalFileChooser = new JFileChooser();
        proposalFileChooser.setAcceptAllFileFilterUsed(false);
        proposalFileChooser.setMultiSelectionEnabled(false);
        proposalFileChooser.setFileFilter(new FileNameExtensionFilter("*.xml (PCT config)", "xml"));*/
        templatesList = new ArrayList<XLSTemplate>();
        try {
            File folder = new File(TEMPLATES_DIR);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                try {
                    if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xls") || listOfFiles[i].getName().endsWith(".xlsx"))) {
                        templatesList.add(new XLSTemplate(listOfFiles[i].getAbsolutePath(), listOfFiles[i].getName()));
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }

        xlsFileChooser = new JFileChooser();
        xlsFileChooser.setAcceptAllFileFilterUsed(false);
        xlsFileChooser.setMultiSelectionEnabled(false);
        xlsFileChooser.setFileFilter(new FileNameExtensionFilter("Excel documents", "xls", "xlsx"));
    }

    private void openFile(File file) {                //xlsFileChooser.getSelectedFile()
        try {
            Proposal proposal = new Proposal(config);
            FileInputStream inp = new FileInputStream(file);
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
            //ProposalForm tmpForm = new ProposalForm(proposal, getFrame());
            addProposalForm(proposal, getFrame(), true);
            //tmpForm.setChanged(false);
            if (proposal.containsDeprecated()) {
                JOptionPane.showMessageDialog(getRoot(), "Selected proposal contains deprecated module(s) or capacity(ies).", "Warning", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception exception) {
            //exception.printStackTrace();
            if (exception instanceof PCTDataFormatException) {
                Logger.getInstance().error(exception);
            }
            JOptionPane.showMessageDialog(getRoot(), "Can't read proposal from specified file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initFileMenu() {
        createProposal = new JMenuItem("Create proposal");
        createProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JTextField proposalNameField = new JTextField();

                final JOptionPane optionPane = new JOptionPane(
                        new JComponent[]{new JLabel("Project name"), proposalNameField},
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
                                                /*if (name.equals("")) {
                                                    JOptionPane.showMessageDialog(getRoot(),
                                                            "Project name shouldn't be empty",
                                                            "Error",
                                                            JOptionPane.ERROR_MESSAGE);
                                                    proposalNameField.requestFocus();
                                                } else {*/
                                                if (name.equals("")) name = "Untitled";
                                                dialog.dispose();
                                                Proposal proposal = new Proposal(config);
                                                proposal.setName(name);
                                                proposal.setProjectName(name);
                                                addProposalForm(proposal, getFrame(), false);
                                                /*}*/
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
                            openFile(xlsFileChooser.getSelectedFile());
                        }
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
                                    final String filename = xlsFileChooser.getSelectedFile().getAbsolutePath();
                                    if (!xlsFileChooser.getSelectedFile().exists() && templatesList != null && templatesList.size() > 0) {
                                        if (templatesList.size() > 1) {
                                            final JComboBox template = new JComboBox(templatesList.toArray());
                                            final JOptionPane optionPane = new JOptionPane(
                                                    new JComponent[]{new JLabel("Template"), template},
                                                    JOptionPane.QUESTION_MESSAGE,
                                                    JOptionPane.OK_CANCEL_OPTION);
                                            final JDialog dialog = new JDialog(getFrame(), "Template", true);
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
                                                                                dialog.dispose();
                                                                                copyAndSave((XLSTemplate) template.getSelectedItem(), filename);
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
                                        } else {
                                            copyAndSave(templatesList.get(0), filename);
                                        }
                                    } else {
                                        save(filename);
                                    }
                                } catch (Exception exception) {
                                    JOptionPane.showMessageDialog(getRoot(), "Selected excel workbook can't be read", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                });
            }
        });
        closeProposal = new JMenuItem("Close proposal");
        closeProposal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCurrentProposalForm().getProposal().getProducts().size() > 0 && getCurrentProposalForm().isChanged()) {
                    final String proposalName = getCurrentProposalForm().getProposal().getProjectName() + " [" + getCurrentProposalForm().getProposal().getClientName() + "]";

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            int choice = JOptionPane.showOptionDialog(getRoot(), "Proposal \"" + proposalName + "\" isn't saved, do you really want to close it?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);//Dialog(getRoot(), "Selected excel workbook can't be read", "Error", JOptionPane.ERROR_MESSAGE);
                            if (choice == JOptionPane.YES_OPTION) {
                                proposalsTabs.remove(getCurrentProposalForm().getRoot());
                            }
                        }
                    });

                } else {
                    proposalsTabs.remove(getCurrentProposalForm().getRoot());
                }
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
            if (!p.getSecondarySale()) {
                sb.append(p.checkForConsistence());
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
        rollUp = new JMenuItem("Roll-up empty sections");
        rollUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCurrentProposalForm() != null) {
                    for (Component c : getCurrentProposalForm().getProductsTabs().getComponents()) {
                        if (c instanceof ProductJPanel) {
                            ProductJPanel pjpc = (ProductJPanel) c;
                            pjpc.getParentForm().rollUp();
                        }
                    }
                }
            }
        });
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
        proposalMenu.add(rollUp);
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


                if (getCurrentProposalForm() != null && getCurrentProposalForm().getProposal().getProducts().size() > 0) {
                    rollUp.setEnabled(true);
                } else {
                    rollUp.setEnabled(false);
                }
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });
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

        new FileDrop(mainPanel, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                for (int i = 0; i < files.length; i++) {
                    openFile(files[i]);
                }
            }
        });
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

    public void addProposalForm(Proposal proposal, JFrame frame, boolean dropChanged) {
        ProposalForm proposalForm = new ProposalForm(proposal, getFrame(), new PCTChangedListener() {
            Object data;

            public void act(Object src) {
                if (getData() != null) {
                    try {
                        com.compassplus.proposalModel.Proposal pp = ((com.compassplus.proposalModel.Proposal) src);
                        proposalsTabs.setTitleAt(proposalsTabs.indexOfComponent((Component) getData()), pp.getProjectName() + " [" + pp.getClientName() + "]");
                    } catch (Exception e) {
                    }
                }
            }

            public void setData(Object data) {
                this.data = data;
            }

            public Object getData() {
                return data;
            }
        });
        proposalsTabs.addTab(proposalForm.getProposal().getName(), proposalForm.getRoot());
        proposalsTabs.setSelectedComponent(proposalForm.getRoot());
        if (dropChanged) {
            proposalForm.setChanged(false);
        }
        proposalForm.updateMainTitle();
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
            Row removingRow = sheet.getRow(rowIndex);
            sheet.removeRow(removingRow);
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

    private Bindings getBindings(Proposal proposal, ScriptEngine engine) {
        Bindings b = engine.createBindings();
        b.put("VAR$CUSTOMER_NAME", proposal.getClientName());
        b.put("VAR$PROJECT_NAME", proposal.getProjectName());
        b.put("VAR$SALES_MANAGER", proposal.getUserName());
        b.put("VAR$SUPPORT_RATE", proposal.getSupportRate());
        b.put("VAR$SUPPORT_PLAN", proposal.getSupportPlan().getName());

        for (com.compassplus.configurationModel.Product prod : proposal.getConfig().getProducts().values()) {
            Product p = proposal.getProducts().get(prod.getName());
            for (Capacity c : prod.getCapacities().values()) {
                String key = "CAP$" + c.getKey().replace("-", "_");
                b.put(key, (p!=null && p.getCapacities().containsKey(c.getKey())) ? true : false);
                b.put(key + "$PRICE", (p!=null && p.getCapacities().containsKey(c.getKey())) ?
                        p.getCapacities().get(c.getKey()).getPrice(p) : 0);
                b.put(key + "$VALUE", (p!=null && p.getCapacities().containsKey(c.getKey())) ?
                        p.getCapacities().get(c.getKey()).getVal() : 0);
                b.put(key + "$NAME", c.getShortName() != null ? c.getShortName() : c.getName());
            }
            for (Module m : prod.getModules().values()) {
                String key = "MOD$" + m.getKey().replace("-", "_");
                b.put(key, (p!=null && p.getModules().containsKey(m.getKey())) ? true : false);
                b.put(key + "$PRICE", (p!=null && p.getModules().containsKey(m.getKey())) ?
                        p.getModules().get(m.getKey()).getPrice(p) : 0);
                b.put(key + "$NAME", m.getShortName() != null ? m.getShortName() : m.getName());
            }
            b.put("VAR$" + prod.getName().replaceAll("\\s", "_") + "$PRIMARY_MODE", (p!=null && !p.getSecondarySale()));
        }
        return b;
    }

    private boolean analyzeCell(Row row, Cell cell, ScriptEngine engine, Bindings bindings) {
        try {
            String expr = cell.getStringCellValue();
            short type = 0;
            if (expr.contains("__REMOVE")) {
                type = __REMOVE;
            } else if (expr.contains("__INSERT")) {
                type = __INSERT;
            }
            if (type > 0) {
                try {
                    expr = expr.substring(expr.indexOf("(") + 1);
                    expr = expr.substring(0, expr.lastIndexOf(")"));
                    expr = expr.replaceAll("\\s", "");

                    if (type == __REMOVE) {
                        cell.setCellValue("");
                        Object val = null;
                        val = engine.eval(expr, bindings);
                        if(val instanceof Boolean){
                            return (Boolean)val;
                        }else{
                            throw new Exception("result is not boolean");
                        }
                    } else if (type == __INSERT) {

                    }

                } catch (Exception e) {
                    cell.setCellValue("");
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
