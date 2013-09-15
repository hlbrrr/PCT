package com.compassplus.gui;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.PSQuote;
import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;
import com.compassplus.proposalModel.TrainingCourse;

import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import net.iharder.dnd.FileDrop;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.10.11
 * Time: 22:35
 */
public class MainForm {
    //private JFileChooser proposalFileChooser;
    private Logger log = Logger.getInstance();

    private static short __REMOVE = 1;
    private static short __INSERT = 2;
    private JFileChooser xlsFileChooser;
    private JPanel mainPanel;
    private JMenuBar mainMenu;
    private JMenu fileMenu;
    private JMenu proposalMenu;
    private JMenu psMenu;
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

    private JMenuItem addPSQuote;
    private JMenuItem delPSQuote;
    private JMenuItem addPSService;
    private JMenuItem addPSTrainingCourse;

    private JMenuItem addProduct;
    private JMenuItem delProduct;

    private JTabbedPane proposalsTabs;
    private ProposalForm currentProposalForm;
    private JFrame frame;

    private Configuration config;

    private String TEMPLATES_DIR = "templates";
    private ArrayList<XLSTemplate> templatesList;

    private String CURRENT_VERSION = "1.1";

    public MainForm(Configuration config) {
        this.config = config;
        initFileChoosers();
        initFileMenu();
        initHelpMenu();
        initProposalMenu();
        initPSMenu();
        // initViewMenu();
        initMainPanel();

        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        //mainMenu.add(viewMenu);
        mainMenu.add(proposalMenu);

        if (config.getServices().size() > 0) {
            mainMenu.add(psMenu);
        }
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
                JLabel version = new JLabel("<html><b>PCT version:</b> " + CURRENT_VERSION + "</html>");
                version.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel build = new JLabel("<html><b>Build number:</b> " + config.getBuild() + "</html>");
                build.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel minBuild = new JLabel("<html><b>Minimal build number required by current configuration:</b> " + config.getMinBuild() + "</html>");
                minBuild.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel expiration = new JLabel("<html><b>Configuration expires:</b> " + config.getExpirationDateString() + "</html>");
                expiration.setAlignmentX(Component.LEFT_ALIGNMENT);

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
                msg.add(build);
                if (config.getMinBuild() != null) {
                    msg.add(minBuild);
                }
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


            Integer psRowsCountInt = null;
            Integer psCellIndexInt = null;
            Integer psRowIndexInt = null;
            String psSheetIndexStr = null;
            Integer psSheetIndexInt = null;

            Sheet settingsSheet = wb.getSheet("PCTSettings");
            final List<RowStyle> rowStyles = new ArrayList<RowStyle>();
            final List<RowStyle> psRowStyles = new ArrayList<RowStyle>();
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
                                    rowStyle.init(wb, wb.getSheet(sheetIndexStr).getRow(rowIndexInt - 1));
                                    rowStyles.add(rowStyle);
                                    removeRow(wb.getSheet(sheetIndexStr), rowIndexInt - 1, wb);
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                } else {
                }
            }

            if (settingsSheet != null) {
                Row currentSettingsRow = settingsSheet.getRow(0);
                if (currentSettingsRow != null) {
                    Cell oldProposalCell = currentSettingsRow.getCell(0);
                    Cell rowsCountCell = currentSettingsRow.getCell(5);
                    Cell cellIndexCell = currentSettingsRow.getCell(6);
                    Cell rowIndexCell = currentSettingsRow.getCell(7);
                    Cell sheetIndexCell = currentSettingsRow.getCell(8);
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
                            psRowsCountInt = Integer.parseInt(rowsCountCell.getStringCellValue());
                            psCellIndexInt = Integer.parseInt(cellIndexCell.getStringCellValue());
                            psRowIndexInt = Integer.parseInt(rowIndexCell.getStringCellValue());
                            psRowIndexInt++;
                            psCellIndexInt++;
                            System.out.println("psRowIndexInt = " + psRowIndexInt);
                            System.out.println("psRowsCountInt = " + psRowsCountInt);
                            //sheetIndexInt = Integer.parseInt(sheetIndexCell.getStringCellValue());
                            psSheetIndexStr = sheetIndexCell.getStringCellValue();
                            if (wb.getSheet(psSheetIndexStr) != null) {
                                //sheetIndexStr = wb.getSheetAt(sheetIndexInt).getSheetName();
                                for (int j = 0; j < psRowsCountInt; j++) {
                                    System.out.println("removing " + j);
                                    RowStyle rowStyle = new RowStyle();
                                    rowStyle.init(wb, wb.getSheet(psSheetIndexStr).getRow(psRowIndexInt - 1));
                                    psRowStyles.add(rowStyle);
                                    removeRow(wb.getSheet(psSheetIndexStr), psRowIndexInt - 1, wb, true);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
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
            String selectedSheet1 = (String) sheetIndexField.getSelectedItem();

            final JComboBox psSheetIndexField = sheets.size() > 1 ? new JComboBox(sheets.toArray()) : null;
            if (psSheetIndexField != null && psSheetIndexStr != null) {
                for (String key : sheets) {
                    if (key.equals(psSheetIndexStr)) {
                        psSheetIndexField.setSelectedItem(key);
                        break;
                    }
                }
            }else{
                for (String key : sheets) {
                    if (!key.equals(selectedSheet1)) {
                        psSheetIndexField.setSelectedItem(key);
                        break;
                    }
                }
            }

            final JTextField rowIndexField = new JTextField(rowIndexInt != null ? rowIndexInt.toString() : "1");
            final JTextField cellIndexField = new JTextField(cellIndexInt != null ? cellIndexInt.toString() : "1");

            final JTextField psRowIndexField = new JTextField(psRowIndexInt != null ? psRowIndexInt.toString() : "1");
            final JTextField psCellIndexField = new JTextField(psCellIndexInt != null ? psCellIndexInt.toString() : "1");
            final boolean isPSQ = getCurrentProposalForm().getProposal().getPSQuote().enabled();
            final JOptionPane optionPane = new JOptionPane(
                    new JComponent[]{
                            sheets.size() > 1 ? new JLabel("Products sheet") : null, sheetIndexField,
                            new JLabel("Products row index"), rowIndexField,
                            new JLabel("Products cell index"), cellIndexField,
                            (sheets.size() > 1 && isPSQ) ? new JLabel("Prof. services sheet") : null, isPSQ ? psSheetIndexField : null,
                            isPSQ ? new JLabel("Prof. services row index") : null, isPSQ ? psRowIndexField : null,
                            isPSQ ? new JLabel("Prof. services cell index") : null, isPSQ ? psCellIndexField : null
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

            final boolean fSameCurrency = false;//sameCurrency;
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
                                                Sheet psS = null;
                                                if (sheetIndexField != null) {
                                                    try {
                                                        s = wb.getSheet((String) sheetIndexField.getSelectedItem());
                                                    } catch (Exception exception) {
                                                    }
                                                } else {
                                                    s = wb.getSheet(sheets.get(0));
                                                }

                                                //PS
                                                if (psSheetIndexField != null) {
                                                    try {
                                                        psS = wb.getSheet((String) psSheetIndexField.getSelectedItem());
                                                    } catch (Exception exception) {
                                                    }
                                                } else {
                                                    psS = wb.getSheet(sheets.get(0));
                                                }


                                                if(getCurrentProposalForm().getProposal().getPSQuote().enabled()){
                                                    if(s.equals(psS)){
                                                        JOptionPane.showMessageDialog(getRoot(), "Products and prof. services can't be exported on the same sheet", "Error", JOptionPane.ERROR_MESSAGE);
                                                        throw new Exception();
                                                    }
                                                }

                                                Integer rowIndex = null;
                                                try {
                                                    rowIndex = Integer.parseInt(rowIndexField.getText());
                                                    rowIndex--;
                                                } catch (Exception exception) {
                                                }
                                                if (rowIndex == null || rowIndex < 0) {
                                                    JOptionPane.showMessageDialog(getRoot(), "Products row index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                    rowIndexField.requestFocus();
                                                    rowIndexField.selectAll();
                                                    throw new Exception();
                                                }

                                                //PS
                                                Integer psRowIndex = null;
                                                try {
                                                    psRowIndex = Integer.parseInt(psRowIndexField.getText());
                                                    psRowIndex--;
                                                } catch (Exception exception) {
                                                }
                                                if ((psRowIndex == null || psRowIndex < 0) && isPSQ) {
                                                    JOptionPane.showMessageDialog(getRoot(), "Prof. services row index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                    psRowIndexField.requestFocus();
                                                    psRowIndexField.selectAll();
                                                    throw new Exception();
                                                }

                                                Integer cellIndex = null;
                                                try {
                                                    cellIndex = Integer.parseInt(cellIndexField.getText());
                                                    cellIndex--;
                                                } catch (Exception exception) {
                                                }
                                                if (cellIndex == null || cellIndex < 0) {
                                                    JOptionPane.showMessageDialog(getRoot(), "Products cell index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                    cellIndexField.requestFocus();
                                                    cellIndexField.selectAll();
                                                    throw new Exception();
                                                }


                                                Integer psCellIndex = null;
                                                try {
                                                    psCellIndex = Integer.parseInt(psCellIndexField.getText());
                                                    psCellIndex--;
                                                } catch (Exception exception) {
                                                }
                                                if ((psCellIndex == null || psCellIndex < 0) && isPSQ) {
                                                    JOptionPane.showMessageDialog(getRoot(), "Prof. services cell index is not valid", "Error", JOptionPane.ERROR_MESSAGE);
                                                    psCellIndexField.requestFocus();
                                                    psCellIndexField.selectAll();
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
                                                        rowStyle.init(wb, s.getRow(rowIndex));
                                                        rowStyles.add(rowStyle);
                                                    }

                                                    //PS
                                                    if (psRowStyles.size() == 0) {
                                                        RowStyle rowStyle = new RowStyle();
                                                        rowStyle.init(wb, psS.getRow(psRowIndex));
                                                        psRowStyles.add(rowStyle);
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
                                                        c3.setCellStyle(cs3);
                                                        c3.setCellValue(p.getProposal().getConfig().isSalesSupport() ? 0 : p.getDiscount());

                                                        Cell c4 = r.createCell(3 + cellIndex);
                                                        CellStyle cs4 = rowStyle.getCellStyle(3 + cellIndex, wb.createCellStyle());
                                                        if (!fSameCurrency)
                                                            cs4.setDataFormat(s.getWorkbook().createDataFormat().getFormat(format));
                                                        c4.setCellStyle(cs4);
                                                        int rowIndexTotal = rowIndex + i + 1;
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

                                                    PSQuote psq = getCurrentProposalForm().getProposal().getPSQuote();
                                                    //PS
                                                    i = 0;
                                                    currentRowIndex = 0;
                                                    if (isPSQ) {
                                                        int sTotal = -1;
                                                        int tTotal = -1;

                                                        int currentRowIndexFrom = -1;
                                                        int currentRowIndexTo = -1;
                                                        String rCol = CellReference.convertNumToColString(1 + psCellIndex);
                                                        String chargeCol = CellReference.convertNumToColString(2 + psCellIndex);
                                                        String totalCol = CellReference.convertNumToColString(3 + psCellIndex);
                                                        String format = (getCurrentProposalForm().getProposal().getCurrency().getSymbol() != null ?
                                                                "\"" + getCurrentProposalForm().getProposal().getCurrency().getSymbol() + "\" " : "") + "#,##0" +
                                                                (getCurrentProposalForm().getProposal().getCurrency().getSymbol() == null ?
                                                                        " \"" + getCurrentProposalForm().getProposal().getCurrency().getName() + "\"" : "");
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Man-day rate:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            cs2.setWrapText(true);
                                                            c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?getCurrentProposalForm().getProposal().getRegion().getMDRate() * getCurrentProposalForm().getProposal().getCurrency().getRate():0);
                                                            if (!fSameCurrency)
                                                                cs2.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                            c2.setCellStyle(cs2);

                                                            for (int y = 2 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }

                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Man-day discount:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            cs2.setDataFormat(psS.getWorkbook().createDataFormat().getFormat("0%;-0%"));
                                                            c2.setCellStyle(cs2);
                                                            c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?psq.getMDDiscount():0);

                                                            for (int y = 2 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }

                                                        /*{
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Onsite daily rate:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            cs2.setWrapText(true);
                                                            c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?getCurrentProposalForm().getProposal().getRegion().getOnsiteDailyCost() * getCurrentProposalForm().getProposal().getCurrency().getRate():0);
                                                            if (!fSameCurrency)
                                                                cs2.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                            c2.setCellStyle(cs2);

                                                            for (int y = 2 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Onsite trip rate:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            cs2.setWrapText(true);
                                                            c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?getCurrentProposalForm().getProposal().getRegion().getTripPrice() * getCurrentProposalForm().getProposal().getCurrency().getRate():0);
                                                            if (!fSameCurrency)
                                                                cs2.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                            c2.setCellStyle(cs2);

                                                            for (int y = 2 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }*/
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }

                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Professional Services Description:");
                                                            c1.setCellStyle(cs1);

                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            cs2.setWrapText(true);
                                                            c2.setCellValue("M/D:");
                                                            c2.setCellStyle(cs2);

                                                            Cell c3 = r.createCell(2 + psCellIndex);
                                                            CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                            cs3.setWrapText(true);
                                                            c3.setCellValue("Chargeable:");
                                                            c3.setCellStyle(cs3);

                                                            Cell c4 = r.createCell(3 + psCellIndex);
                                                            CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                            cs4.setWrapText(true);
                                                            c4.setCellValue("$$:");
                                                            c4.setCellStyle(cs4);

                                                            /*Cell c5 = r.createCell(4 + psCellIndex);
                                                            CellStyle cs5 = rowStyle.getCellStyle(4 + psCellIndex, wb.createCellStyle());
                                                            cs5.setWrapText(true);
                                                            c5.setCellValue("Onsite days:");
                                                            c5.setCellStyle(cs5);

                                                            Cell c6 = r.createCell(5 + psCellIndex);
                                                            CellStyle cs6 = rowStyle.getCellStyle(5 + psCellIndex, wb.createCellStyle());
                                                            cs6.setWrapText(true);
                                                            c6.setCellValue("Onsite trips:");
                                                            c6.setCellStyle(cs6);*/

                                                            for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }

                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }


                                                        for (ServicesGroup sg : getCurrentProposalForm().getProposal().getConfig().getServicesRoot().getGroups()) {
                                                            if (sg.notEmpty(psq)) {
                                                                //vigrujaem gruppu
                                                                {
                                                                    if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                        psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                    }
                                                                    RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                    currentRowIndex++;
                                                                    if(currentRowIndexFrom < 0){
                                                                        currentRowIndexFrom = currentRowIndex;
                                                                    }
                                                                    Row r = psS.createRow(psRowIndex + i);
                                                                    for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                        CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                        if (tcs != null) {
                                                                            Cell tc = r.createCell(y);
                                                                            tc.setCellStyle(tcs);
                                                                        }
                                                                    }
                                                                    Cell c1 = r.createCell(0 + psCellIndex);
                                                                    CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                    cs1.setWrapText(true);
                                                                    c1.setCellValue(sg.getName());
                                                                    c1.setCellStyle(cs1);
                                                                    if (psq.isExportable(sg.getKey())) {
                                                                        Cell c2 = r.createCell(1 + psCellIndex);
                                                                        CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                        c2.setCellValue(sg.getTotalMD(psq));
                                                                        c2.setCellStyle(cs2);

                                                                        Cell c3 = r.createCell(2 + psCellIndex);
                                                                        CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                        c3.setCellValue(sg.getChargeableMD(psq));
                                                                        c3.setCellStyle(cs3);

                                                                        Cell c4 = r.createCell(3 + psCellIndex);
                                                                        CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                        c4.setCellFormula(rCol + (psRowIndex + 1) + "*" + chargeCol + (psRowIndex + currentRowIndex) + "*(1-" + rCol + (psRowIndex + 2) + ")");
                                                                        if (!fSameCurrency)
                                                                            cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                        c4.setCellStyle(cs4);

                                                                        /*Cell c5 = r.createCell(4 + psCellIndex);
                                                                        CellStyle cs5 = rowStyle.getCellStyle(4 + psCellIndex, wb.createCellStyle());
                                                                        c5.setCellValue(sg.getTotalOnsiteMD(psq));
                                                                        c5.setCellStyle(cs5);

                                                                        Cell c6 = r.createCell(5 + psCellIndex);
                                                                        CellStyle cs6 = rowStyle.getCellStyle(5 + psCellIndex, wb.createCellStyle());
                                                                        c6.setCellValue(sg.getTotalOnsiteTrips(psq));
                                                                        c6.setCellStyle(cs6);*/
                                                                    }

                                                                    for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                        CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                        if (tcs != null) {
                                                                            Cell tc = r.createCell(y);
                                                                            tc.setCellStyle(tcs);
                                                                        }
                                                                    }
                                                                    i++;
                                                                }
                                                                if (!psq.isExportable(sg.getKey())) {
                                                                    for (Service srv : sg.getServices().values()) {
                                                                        if (srv.notEmpty(psq)) {
                                                                            //System.out.println("\n\nsg.getKey()="+sg.getKey());
                                                                            //System.out.println("psq.isHidden(sg.getKey())="+psq.isHidden(sg.getKey()));
                                                                            if (psq.isExportable(srv.getKey())) {
                                                                                //vigrujaem service
                                                                                {
                                                                                    if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                                        psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                                    }
                                                                                    RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                                    currentRowIndex++;
                                                                                    Row r = psS.createRow(psRowIndex + i);
                                                                                    for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                                        CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                                        if (tcs != null) {
                                                                                            Cell tc = r.createCell(y);
                                                                                            tc.setCellStyle(tcs);
                                                                                        }
                                                                                    }
                                                                                    Cell c1 = r.createCell(0 + psCellIndex);
                                                                                    CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                                    cs1.setWrapText(true);
                                                                                    c1.setCellValue(srv.getName());
                                                                                    c1.setCellStyle(cs1);
                                                                                    Cell c2 = r.createCell(1 + psCellIndex);
                                                                                    CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                                    c2.setCellValue(srv.getTotalMD(psq));
                                                                                    c2.setCellStyle(cs2);

                                                                                    Cell c3 = r.createCell(2 + psCellIndex);
                                                                                    CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                                    c3.setCellValue(srv.getChargeableMD(psq));
                                                                                    c3.setCellStyle(cs3);

                                                                                    Cell c4 = r.createCell(3 + psCellIndex);
                                                                                    CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                                    c4.setCellFormula(rCol + (psRowIndex + 1) + "*" + chargeCol + (psRowIndex + currentRowIndex) + "*(1-" + rCol + (psRowIndex + 2) + ")");
                                                                                    if (!fSameCurrency)
                                                                                        cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                                    c4.setCellStyle(cs4);

                                                                                    /*Cell c5 = r.createCell(4 + psCellIndex);
                                                                                    CellStyle cs5 = rowStyle.getCellStyle(4 + psCellIndex, wb.createCellStyle());
                                                                                    c5.setCellValue(srv.getTotalOnsiteMD(psq));
                                                                                    c5.setCellStyle(cs5);

                                                                                    Cell c6 = r.createCell(5 + psCellIndex);
                                                                                    CellStyle cs6 = rowStyle.getCellStyle(5 + psCellIndex, wb.createCellStyle());
                                                                                    c6.setCellValue(srv.getTotalOnsiteTrips(psq));
                                                                                    c6.setCellStyle(cs6);*/

                                                                                    for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                                        CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                                        if (tcs != null) {
                                                                                            Cell tc = r.createCell(y);
                                                                                            tc.setCellStyle(tcs);
                                                                                        }
                                                                                    }
                                                                                    i++;
                                                                                }
                                                                            } else {
                                                                                for (com.compassplus.proposalModel.Service inst : psq.getServices().values()) {
                                                                                    if (inst.getService().getGroupKey().equals(sg.getKey()) && inst.getService().getKey().equals(srv.getKey())) {
                                                                                        {
                                                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                                            }
                                                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                                            currentRowIndex++;
                                                                                            Row r = psS.createRow(psRowIndex + i);
                                                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                                                if (tcs != null) {
                                                                                                    Cell tc = r.createCell(y);
                                                                                                    tc.setCellStyle(tcs);
                                                                                                }
                                                                                            }
                                                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                                            cs1.setWrapText(true);
                                                                                            c1.setCellValue(inst.getName());
                                                                                            c1.setCellStyle(cs1);
                                                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                                            c2.setCellValue(inst.getTotalValue());
                                                                                            c2.setCellStyle(cs2);

                                                                                            Cell c3 = r.createCell(2 + psCellIndex);
                                                                                            CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                                            c3.setCellValue(inst.getCharge() ? inst.getTotalValue() : 0);
                                                                                            c3.setCellStyle(cs3);

                                                                                            Cell c4 = r.createCell(3 + psCellIndex);
                                                                                            CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                                            c4.setCellFormula(rCol + (psRowIndex + 1) + "*" + chargeCol + (psRowIndex + currentRowIndex) + "*(1-" + rCol + (psRowIndex + 2) + ")");
                                                                                            if (!fSameCurrency)
                                                                                                cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                                            c4.setCellStyle(cs4);

                                                                                            /*Cell c5 = r.createCell(4 + psCellIndex);
                                                                                            CellStyle cs5 = rowStyle.getCellStyle(4 + psCellIndex, wb.createCellStyle());
                                                                                            c5.setCellValue(inst.getOnsiteTotalValue());
                                                                                            c5.setCellStyle(cs5);

                                                                                            Cell c6 = r.createCell(5 + psCellIndex);
                                                                                            CellStyle cs6 = rowStyle.getCellStyle(5 + psCellIndex, wb.createCellStyle());
                                                                                            c6.setCellValue(inst.getTripTotalValue());
                                                                                            c6.setCellStyle(cs6);*/

                                                                                            for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                                                if (tcs != null) {
                                                                                                    Cell tc = r.createCell(y);
                                                                                                    tc.setCellStyle(tcs);
                                                                                                }
                                                                                            }
                                                                                            i++;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        currentRowIndexTo = currentRowIndex;
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }
                                                        {
                                                            sTotal = currentRowIndex;
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Services total:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            c2.setCellFormula("SUM(" + rCol + (psRowIndex + currentRowIndexFrom) + ":" + rCol + (psRowIndex + currentRowIndexTo) + ")");
                                                            c2.setCellStyle(cs2);


                                                            Cell c3 = r.createCell(2 + psCellIndex);
                                                            CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                            cs3.setWrapText(true);
                                                            c3.setCellValue("");
                                                            c3.setCellStyle(cs3);

                                                            Cell c4 = r.createCell(3 + psCellIndex);
                                                            CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                            c4.setCellFormula("SUM(" + totalCol + (psRowIndex + currentRowIndexFrom) + ":" + totalCol + (psRowIndex + currentRowIndexTo) + ")");
                                                            if (!fSameCurrency)
                                                                cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                            c4.setCellStyle(cs4);

                                                            for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }

                                                        int currentRowIndexFromTC = -1;
                                                        int currentRowIndexToTC = -1;
                                                        if(psq.getTrainingCoursesCount()>0){
                                                            {
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("");
                                                                c1.setCellStyle(cs1);

                                                                for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                i++;
                                                            }
                                                            {
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("");
                                                                c1.setCellStyle(cs1);

                                                                for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                i++;
                                                            }

                                                            {
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("Training course:");
                                                                c1.setCellStyle(cs1);


                                                                Cell c2 = r.createCell(1 + psCellIndex);
                                                                CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                cs2.setWrapText(true);
                                                                c2.setCellValue("Cost per person:");
                                                                c2.setCellStyle(cs2);


                                                                Cell c3 = r.createCell(2 + psCellIndex);
                                                                CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                cs3.setWrapText(true);
                                                                c3.setCellValue("Number of participants:");
                                                                c3.setCellStyle(cs3);

                                                                Cell c4 = r.createCell(3 + psCellIndex);
                                                                CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                cs4.setWrapText(true);
                                                                c4.setCellValue("$$:");
                                                                c4.setCellStyle(cs4);

                                                                for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }

                                                                i++;
                                                            }
                                                            {
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("");
                                                                c1.setCellStyle(cs1);

                                                                for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                i++;
                                                            }
                                                            for(TrainingCourse ttc : psq.getTrainingCourses().values()){
                                                                if(!ttc.getInclude()){
                                                                    continue;
                                                                }
                                                                if(currentRowIndexFromTC < 0){
                                                                    currentRowIndexFromTC = currentRowIndex + 1;
                                                                }
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue(ttc.getTrainingCourse().getName());
                                                                c1.setCellStyle(cs1);


                                                                Cell c2 = r.createCell(1 + psCellIndex);
                                                                CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?ttc.getPricePerAttendee():0);
                                                                if (!fSameCurrency)
                                                                    cs2.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                c2.setCellStyle(cs2);


                                                                Cell c3 = r.createCell(2 + psCellIndex);
                                                                CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                cs3.setWrapText(true);
                                                                c3.setCellValue(ttc.getAttendees());
                                                                c3.setCellStyle(cs3);

                                                                Cell c4 = r.createCell(3 + psCellIndex);
                                                                CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                c4.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?ttc.getRegionalPrice():0);
                                                                if (!fSameCurrency)
                                                                    cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                c4.setCellStyle(cs4);

                                                                for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }

                                                                i++;
                                                            }
                                                            currentRowIndexToTC = currentRowIndex;
                                                            {
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("");
                                                                c1.setCellStyle(cs1);

                                                                for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                i++;
                                                            }
                                                            {
                                                                tTotal = currentRowIndex;
                                                                if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                    psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                                }
                                                                RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                                currentRowIndex++;
                                                                Row r = psS.createRow(psRowIndex + i);
                                                                for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }
                                                                Cell c1 = r.createCell(0 + psCellIndex);
                                                                CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                                cs1.setWrapText(true);
                                                                c1.setCellValue("Training courses total:");
                                                                c1.setCellStyle(cs1);


                                                                Cell c2 = r.createCell(1 + psCellIndex);
                                                                CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                                cs2.setWrapText(true);
                                                                c2.setCellValue("");
                                                                c2.setCellStyle(cs2);


                                                                Cell c3 = r.createCell(2 + psCellIndex);
                                                                CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                                cs3.setWrapText(true);
                                                                c3.setCellValue("");
                                                                c3.setCellStyle(cs3);

                                                                Cell c4 = r.createCell(3 + psCellIndex);
                                                                CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                                c4.setCellFormula("SUM(" + totalCol + (psRowIndex + currentRowIndexFromTC) + ":" + totalCol + (psRowIndex + currentRowIndexToTC) + ")");
                                                                if (!fSameCurrency)
                                                                    cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                                c4.setCellStyle(cs4);

                                                                for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                    CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                    if (tcs != null) {
                                                                        Cell tc = r.createCell(y);
                                                                        tc.setCellStyle(tcs);
                                                                    }
                                                                }

                                                                i++;
                                                            }
                                                        }


                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("");
                                                            c1.setCellStyle(cs1);

                                                            for (int y = 1 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            i++;
                                                        }
                                                        {
                                                            if (psS.getLastRowNum() >= psRowIndex + i) {
                                                                psS.shiftRows(psRowIndex + i, psS.getLastRowNum(), 1);
                                                            }
                                                            RowStyle rowStyle = psRowStyles.get((int) (currentRowIndex - psRowStyles.size() * Math.floor(currentRowIndex / psRowStyles.size())));
                                                            currentRowIndex++;
                                                            Row r = psS.createRow(psRowIndex + i);
                                                            for (int y = rowStyle.getFirst(); y < 0 + psCellIndex; y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }
                                                            Cell c1 = r.createCell(0 + psCellIndex);
                                                            CellStyle cs1 = rowStyle.getCellStyle(0 + psCellIndex, wb.createCellStyle());
                                                            cs1.setWrapText(true);
                                                            c1.setCellValue("Total cost discount:");
                                                            c1.setCellStyle(cs1);


                                                            Cell c2 = r.createCell(1 + psCellIndex);
                                                            CellStyle cs2 = rowStyle.getCellStyle(1 + psCellIndex, wb.createCellStyle());
                                                            c2.setCellValue(!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()?psq.getPSDiscount():0);
                                                            cs2.setDataFormat(s.getWorkbook().createDataFormat().getFormat("0%;-0%"));
                                                            c2.setCellStyle(cs2);


                                                            Cell c3 = r.createCell(2 + psCellIndex);
                                                            CellStyle cs3 = rowStyle.getCellStyle(2 + psCellIndex, wb.createCellStyle());
                                                            cs3.setWrapText(true);
                                                            c3.setCellValue("");
                                                            c3.setCellStyle(cs3);

                                                            Cell c4 = r.createCell(3 + psCellIndex);
                                                            CellStyle cs4 = rowStyle.getCellStyle(3 + psCellIndex, wb.createCellStyle());
                                                            c4.setCellFormula("(1-" + rCol+(psRowIndex + currentRowIndex) + ")*(" + totalCol + (psRowIndex + sTotal +1) + "+" + totalCol +(psRowIndex + tTotal+1) + ")");
                                                            if (!fSameCurrency)
                                                                cs4.setDataFormat(psS.getWorkbook().createDataFormat().getFormat(format));
                                                            c4.setCellStyle(cs4);

                                                            for (int y = 4 + psCellIndex; y <= rowStyle.getLast(); y++) {
                                                                CellStyle tcs = rowStyle.getCellStyle(y, null);
                                                                if (tcs != null) {
                                                                    Cell tc = r.createCell(y);
                                                                    tc.setCellStyle(tcs);
                                                                }
                                                            }

                                                            i++;
                                                        }
                                                    }

                                                    ArrayList<Row> rowsToRemove = new ArrayList();
                                                    ScriptEngineManager factory = new ScriptEngineManager();
                                                    ScriptEngine engine = factory.getEngineByName("JavaScript");
                                                    Bindings bindings = getBindings(getCurrentProposalForm().getProposal(), engine);
                                                    int dec = 0;
                                                    int psDec = 0;
                                                    for (int si = 0; si < wb.getNumberOfSheets(); si++) {
                                                        Sheet sis = wb.getSheetAt(si);
                                                        Iterator<Row> riter = sis.rowIterator();
                                                        while (riter.hasNext()) {
                                                            Row row = riter.next();
                                                            Iterator<Cell> citer = row.cellIterator();
                                                            while (citer.hasNext()) {
                                                                Cell cell = citer.next();
                                                                if (analyzeCell(wb, sis, row, cell, engine, bindings)) {
                                                                    if (sis.getSheetName() == s.getSheetName() && row.getRowNum() < rowIndex) {
                                                                        dec++;
                                                                    }
                                                                    if (sis.getSheetName() == psS.getSheetName() && row.getRowNum() < psRowIndex) {
                                                                        psDec++;
                                                                    }
                                                                    if (!rowsToRemove.contains(row)) {
                                                                        rowsToRemove.add(row);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        for (Row ri : rowsToRemove) {
                                                            removeRow(sis, ri.getRowNum(), wb);
                                                        }

                                                        rowsToRemove.clear();
                                                    }
                                                    rowIndex = rowIndex - dec;
                                                    psRowIndex = psRowIndex - psDec;

                                                    Row settingsRow = settingsSheet.getRow(0);
                                                    if (settingsRow == null) {
                                                        settingsRow = settingsSheet.createRow(0);
                                                    }
                                                    CellUtil.createCell(settingsRow, 0, getCurrentProposalForm().getProposal().toString());
                                                    CellUtil.createCell(settingsRow, 1, new Integer(getCurrentProposalForm().getProposal().getProducts().size()).toString());
                                                    CellUtil.createCell(settingsRow, 2, cellIndex.toString());
                                                    CellUtil.createCell(settingsRow, 3, rowIndex.toString());
                                                    CellUtil.createCell(settingsRow, 4, s.getSheetName());

                                                    //PS
                                                    if (isPSQ) {
                                                        CellUtil.createCell(settingsRow, 5, new Integer(i).toString());
                                                        CellUtil.createCell(settingsRow, 6, psCellIndex.toString());
                                                        CellUtil.createCell(settingsRow, 7, psRowIndex.toString());
                                                        CellUtil.createCell(settingsRow, 8, psS.getSheetName());
                                                    }

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
                        if (checkForConsistence() && (getCurrentProposalForm().getProposal().getConfig().getAuthLevels().size() == 0 || getCurrentProposalForm().getProposal().getConfig().isSalesSupport() || JOptionPane.showOptionDialog(getRoot(), "Have you checked authority levels for this proposal?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION)) {

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

        if (!getCurrentProposalForm().getProposal().getConfig().isSalesSupport()) {
            for (String key : getCurrentProposalForm().getProposal().getConfig().getAuthLevels().keySet()) {
                if (!getCurrentProposalForm().getProposal().getSelectedAls().containsKey(key) ||
                        !getCurrentProposalForm().getProposal().getConfig().getAuthLevels().get(key).getLevels().containsKey(getCurrentProposalForm().getProposal().getSelectedAls().get(key))) {
                    sb.append("\nAuthority level \"");
                    sb.append(getCurrentProposalForm().getProposal().getConfig().getAuthLevels().get(key).getName());
                    sb.append("\" value is not set");
                }
            }
        }
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
        /*addPSQuote = new JMenuItem("Add PS quote");
        delPSQuote = new JMenuItem("Remove PS quote");
        addPSQuote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().getProposal().createPSQuote();
                getCurrentProposalForm().addPSForm();
            }
        });
        delPSQuote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().delPSForm();
            }
        });*/


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
                    try {
                        getCurrentProposalForm().addProductForm(new Product((com.compassplus.configurationModel.Product) product, getCurrentProposalForm().getProposal()));
                    } catch (PCTDataFormatException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
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
        /*proposalMenu.add(addPSQuote);
        proposalMenu.add(delPSQuote);*/
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

/*                if (getCurrentProposalForm() != null && !getCurrentProposalForm().getProposal().getPSQuote().enabled()) {
                    addPSQuote.setEnabled(true);
                    delPSQuote.setEnabled(false);
                } else {
                    addPSQuote.setEnabled(false);
                    delPSQuote.setEnabled(true);
                }*/

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

    private void initPSMenu() {
        addPSQuote = new JMenuItem("Add PS quote");
        delPSQuote = new JMenuItem("Remove PS quote");
        addPSService = new JMenuItem("Add service");
        addPSTrainingCourse = new JMenuItem("Add training course");

        addPSQuote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().getProposal().createPSQuote();
                getCurrentProposalForm().addPSForm();
            }
        });
        delPSQuote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getCurrentProposalForm().delPSForm();
            }
        });

        addPSService.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<ListItem> sgs = new ArrayList<ListItem>();
                List<ListItem> services = new ArrayList<ListItem>();
                boolean first = true;
                for (ServicesGroup sg : getCurrentProposalForm().getProposal().getConfig().getServicesRoot().getGroups()) {
                    sgs.add(new ListItem(sg.getName(), sg.getKey()));
                    if (first) {
                        for (Service s : sg.getServices().values()) {
                            services.add(new ListItem(s.getName(), s.getKey()));
                        }
                        first = false;
                    }
                }


                final JComboBox serviceGroupField = sgs.size() > 1 ? new JComboBox(sgs.toArray()) : null;
                final JComboBox serviceField = new JComboBox(services.toArray());

                serviceGroupField.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        ListItem li = (ListItem) serviceGroupField.getSelectedItem();
                        serviceField.removeAllItems();
                        for (ServicesGroup sg : getCurrentProposalForm().getProposal().getConfig().getServicesRoot().getGroups()) {
                            if (sg.getKey().equals(li.getValue())) {
                                for (Service s : sg.getServices().values()) {
                                    serviceField.addItem(new ListItem(s.getName(), s.getKey()));
                                }
                                break;
                            }
                        }
                    }
                });

                serviceGroupField.setSelectedIndex(0);


                final JTextField serviceName = new JTextField();

                final JTextArea textArea = new JTextArea("");
                textArea.setColumns(35);
                textArea.setRows(10);
                textArea.setLineWrap(true);
                textArea.setEditable(true);
                textArea.setWrapStyleWord(true);
                JScrollPane spane = new JScrollPane(textArea);
                textArea.setFont(spane.getFont());
                /*JOptionPane.showMessageDialog(
                        null, spane, "Description", JOptionPane.INFORMATION_MESSAGE);*/


                final JOptionPane optionPane = new JOptionPane(
                        new JComponent[]{
                                sgs.size() > 1 ? new JLabel("Service group") : null, serviceGroupField,
                                new JLabel("Service"), serviceField,
                                new JLabel("Name"), serviceName,
                                new JLabel("Description"), spane
                        },
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);

                final JDialog dialog = new JDialog(getFrame(), "Add service", true);
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

                                                    getCurrentProposalForm().getProposal().getPSQuote().addService(new com.compassplus.proposalModel.Service(getCurrentProposalForm().getProposal(), serviceName.getText(), textArea.getText(), ((ListItem) serviceField.getSelectedItem()).getValue()));
                                                    try {
                                                        getCurrentProposalForm().getPSForm().update();
                                                    } catch (Exception ee) {
                                                        ee.printStackTrace();
                                                    }

                                                    dialog.dispose();
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
        });

        addPSTrainingCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<ListItem> sgs = new ArrayList<ListItem>();
                boolean first = true;
                for (com.compassplus.configurationModel.TrainingCourse sg : getCurrentProposalForm().getProposal().getConfig().getTrainingCourses().values()) {
                    if(!getCurrentProposalForm().getProposal().getPSQuote().getTrainingCourses().containsKey(sg.getKey())){
                        sgs.add(new ListItem(sg.getName(), sg.getKey()));
                    }
                }

                final JComboBox serviceGroupField = new JComboBox(sgs.toArray());

                serviceGroupField.setSelectedIndex(0);


                final JOptionPane optionPane = new JOptionPane(
                        new JComponent[]{
                                new JLabel("Training course"), serviceGroupField
                        },
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);

                final JDialog dialog = new JDialog(getFrame(), "Add training course", true);
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
                                                    ListItem li = (ListItem) serviceGroupField.getSelectedItem();

                                                    TrainingCourse tCourse = new TrainingCourse(getCurrentProposalForm().getProposal().getConfig().getTrainingCourses().get(li.getValue()), getCurrentProposalForm().getProposal());

                                                    tCourse.setUserDefined(true);
                                                    tCourse.setInclude(false);

                                                    getCurrentProposalForm().getProposal().getPSQuote().addTrainingCourse(tCourse);
                                                    //getCurrentProposalForm().getProposal().getPSQuote().addService(new com.compassplus.proposalModel.Service(getCurrentProposalForm().getProposal(), serviceName.getText(), textArea.getText(), ((ListItem) serviceField.getSelectedItem()).getValue()));
                                                    try {
                                                        getCurrentProposalForm().getPSForm().update();
                                                    } catch (Exception ee) {
                                                        ee.printStackTrace();
                                                    }

                                                    dialog.dispose();
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
        });

        psMenu = new JMenu("Professional services");
        psMenu.add(addPSQuote);
        psMenu.add(delPSQuote);
        psMenu.add(addPSService);
        psMenu.add(addPSTrainingCourse);
        psMenu.setEnabled(false);

        psMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                if (getCurrentProposalForm() != null && !getCurrentProposalForm().getProposal().getPSQuote().enabled()) {
                    addPSQuote.setEnabled(true);
                    delPSQuote.setEnabled(false);
                    addPSService.setEnabled(false);
                } else {
                    addPSQuote.setEnabled(false);
                    delPSQuote.setEnabled(true);
                    addPSService.setEnabled(true);
                }

                if (getCurrentProposalForm() != null && !getCurrentProposalForm().getProposal().getPSQuote().enabled()
                        || getCurrentProposalForm().getProposal().getPSQuote().getTrainingCourses().size() == getCurrentProposalForm().getProposal().getConfig().getTrainingCourses().size()) {
                    addPSTrainingCourse.setEnabled(false);
                }else{
                    addPSTrainingCourse.setEnabled(true);
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

    private JMenuItem getAddPSQuote() {
        return addPSQuote;
    }

    private JMenuItem getDelPSQuote() {
        return delPSQuote;
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
            psMenu.setEnabled(true);
        } else {
            proposalMenu.setEnabled(false);
            psMenu.setEnabled(false);
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

    private boolean isNormalDiscount(Number value, Product product, Comparable maximumDiscount, boolean isSupport) {
        Double maximumDiscountedSum = ((1 - ((Number) maximumDiscount).doubleValue())) * (isSupport ? product.getSupportPriceUndiscounted(true) : product.getRegionPrice(true));

        final Double newMax = (Math.round(10000d * (1 - maximumDiscountedSum / (isSupport ? product.getSupportPriceUndiscounted() : product.getRegionPrice()))) / 100d);

        //if (!value.equals(this.value)) {
        if (newMax.compareTo(((Number) value).doubleValue()) < 0) {
            return false;
        } else {
            return true;
        }

    }

    public void addProposalForm(Proposal proposal, JFrame frame, boolean dropChanged) {
        final Proposal _proposal = proposal;
        ProposalForm proposalForm = new ProposalForm(proposal, getFrame(), new PCTChangedListener() {
            Map<String, Object> data = new HashMap<String, Object>();

            public void act(Object src) {
                try {
                    com.compassplus.proposalModel.Proposal pp = ((com.compassplus.proposalModel.Proposal) src);
                    int status = 0;
                    boolean exceedDL = false;

                    if (_proposal.getPSQuote().enabled() && (_proposal.getPSQuote().getMDDiscount() > _proposal.getConfig().getMDRDiscount() ||
                            _proposal.getPSQuote().getPSDiscount() > _proposal.getConfig().getPSDiscount())) {
                        exceedDL = true;
                    }

                    for (Product p : pp.getProducts().values()) {
                        if (!isNormalDiscount(p.getSupportDiscount() * 100d, p, p.getProposal().getConfig().getMaxSupportDiscount(), true) ||
                                !isNormalDiscount(p.getDiscount() * 100d, p, p.getProposal().getConfig().getMaxDiscount(), false)) {
                            exceedDL = true;
                            break;
                        }
                    }
                    if (pp.getConfig().getAuthLevels().size() == 0 || !pp.isAllAlsDefined() || pp.getConfig().isSalesSupport()) {

                    } else if (pp.isApproved()) {
                        status = 1;
                    } else {
                        status = 2;
                    }
                    if (!pp.getConfig().isSalesSupport() && exceedDL) {
                        status = 2;
                    }

                    if (getData("productTab") != null) {
                        int ind = proposalsTabs.indexOfComponent((Component) getData("productTab"));
                        if (status == 0) {
                            proposalsTabs.setForegroundAt(ind, Color.BLACK);
                        } else if (status == 1) {
                            proposalsTabs.setForegroundAt(ind, new Color(0, 158, 5));
                        } else {
                            proposalsTabs.setForegroundAt(ind, Color.RED);
                        }
                        proposalsTabs.setTitleAt(ind, pp.getProjectName() + " [" + pp.getClientName() + "]");
                    }
                    if (getData("productsTabs") != null && getData("summaryForm") != null) {
                        SummaryForm summaryForm = (SummaryForm) getData("summaryForm");
                        JTabbedPane productsTabs = (JTabbedPane) getData("productsTabs");
                        int ind = productsTabs.indexOfComponent((Component) (summaryForm.getRoot()));
                        String append = "";
                        if (status == 0) {
                            productsTabs.setForegroundAt(ind, Color.BLACK);
                        } else if (status == 1) {
                            append = " [APPROVED]";
                            productsTabs.setForegroundAt(ind, new Color(0, 158, 5));
                        } else {
                            append = " [REQUIRES APPROVAL]";
                            productsTabs.setForegroundAt(ind, Color.RED);
                        }
                        productsTabs.setTitleAt(ind, "Summary" + append);
                    }
                } catch (Exception e) {
                }
            }

            public void setData(String key, Object data) {
                this.data.put(key, data);
            }

            public Object getData(String key) {
                return this.data.get(key);
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

    private void removeRow(Sheet sheet, int rowIndex, Workbook wb) {
        removeRow(sheet, rowIndex, wb, false);
    }

    private void removeRow(Sheet sheet, int rowIndex, Workbook wb, boolean debug) {
        ArrayList<CellRangeAddress> cras = new ArrayList<CellRangeAddress>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            cras.add(sheet.getMergedRegion(i));
        }
        while (sheet.getNumMergedRegions() > 0) {
            sheet.removeMergedRegion(0);
        }
        int lastRowNum = sheet.getLastRowNum();

        if(debug){
            System.out.println("lastRowNum = " + lastRowNum);
            System.out.println("rowIndex = " + rowIndex);
        }

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
        for (CellRangeAddress cra : cras) {
            if (rowIndex >= cra.getFirstRow() && rowIndex <= cra.getLastRow() && cra.getFirstRow() != cra.getLastRow()) {
                cra.setLastRow(cra.getLastRow() - 1);
                sheet.addMergedRegion(cra);
            } else if (rowIndex < cra.getFirstRow()) {
                cra.setFirstRow(cra.getFirstRow() - 1);
                cra.setLastRow(cra.getLastRow() - 1);
                sheet.addMergedRegion(cra);
            } else if (rowIndex > cra.getLastRow()) {
                sheet.addMergedRegion(cra);
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
        b.put("VAR$MAN-DAY-RATE", 0);

        for (com.compassplus.configurationModel.Product prod : proposal.getConfig().getProducts().values()) {
            Product p = proposal.getProducts().get(prod.getName());
            for (Capacity c : prod.getCapacities().values()) {
                String key = "CAP$" + c.getKey().replace("-", "_");
                b.put(key, (p != null && p.getCapacities().containsKey(c.getKey())) ? true : false);
                b.put(key + "$PRICE", (p != null && p.getCapacities().containsKey(c.getKey())) ?
                        (!proposal.getConfig().isSalesSupport() ? p.getCapacities().get(c.getKey()).getPrice(p) : 0) : "");
                b.put(key + "$VALUE", (p != null && p.getCapacities().containsKey(c.getKey())) ?
                        p.getCapacities().get(c.getKey()).getVal() : "");
                b.put(key + "$NAME", (p != null && p.getCapacities().containsKey(c.getKey())) ?
                        (c.getShortName() != null ? c.getShortName() : c.getName()) : "");
            }
            for (Module m : prod.getModules().values()) {
                String key = "MOD$" + m.getKey().replace("-", "_");
                b.put(key, (p != null && p.getModules().containsKey(m.getKey())) ? true : false);
                b.put(key + "$PRICE", (p != null && p.getModules().containsKey(m.getKey())) ?
                        (!proposal.getConfig().isSalesSupport() ? p.getModules().get(m.getKey()).getPrice(p) : 0) : "");
                b.put(key + "$NAME", (p != null && p.getModules().containsKey(m.getKey())) ?
                        (m.getShortName() != null ? m.getShortName() : m.getName()) : "");
            }
            b.put("VAR$" + prod.getName().replaceAll("\\s", "_") + "$PRIMARY_MODE", p != null ? !p.getSecondarySale() : "");
        }
        for (com.compassplus.configurationModel.AuthLevel al : proposal.getConfig().getAuthLevels().values()) {
            String key = "AL$" + al.getKey().replace("-", "_");
            String alValue = proposal.getSelectedAls().get(al.getKey());
            String alText = proposal.getAlsTxt().get(al.getKey());
            b.put(key + "$VALUE", alValue != null ? alValue : "");
            b.put(key + "$TEXT", alText != null ? alText : "");
        }
        return b;
    }

    private boolean analyzeCell(Workbook wb, Sheet sheet, Row row, Cell cell, ScriptEngine engine, Bindings bindings) {
        try {
            String formula = cell.getCellFormula();
            cell.setCellFormula(formula);
        } catch (Exception e) {
        }
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
                        if (val instanceof Boolean) {
                            return (Boolean) val;
                        } else {
                            throw new Exception("result is not boolean");
                        }
                    } else if (type == __INSERT) {
                        cell.setCellValue("");
                        Object val = null;
                        val = engine.eval(expr, bindings);

                        if (!(val instanceof String && ((String) val).equals(""))) {
                            CellStyle cs = wb.createCellStyle();
                            CellStyle csT = cell.getCellStyle();
                            cs.cloneStyleFrom(csT);
                            String format = (getCurrentProposalForm().getProposal().getCurrency().getSymbol() != null ?
                                    "\"" + getCurrentProposalForm().getProposal().getCurrency().getSymbol() + "\" " : "") + "#,##0" +
                                    (getCurrentProposalForm().getProposal().getCurrency().getSymbol() == null ?
                                            " \"" + getCurrentProposalForm().getProposal().getCurrency().getName() + "\"" : "");
                            if (expr.contains("$PRICE") || expr.contains("$MAN-DAY-RATE")) {
                                cs.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat(format));
                            } else if (expr.contains("$SUPPORT_RATE")) {
                                cs.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0%;-0%"));
                            } else if (expr.contains("$VALUE")) {
                                cs.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("#,##0"));
                            }
                            cell.setCellStyle(cs);
                        }
                        if (val instanceof Boolean) {
                            cell.setCellValue((Boolean) val);
                        } else if (val instanceof Number) {
                            cell.setCellValue(((Number) val).doubleValue());
                        } else if (val instanceof String) {
                            cell.setCellValue((String) val);
                        } else {
                            throw new Exception("result type is unknown");
                        }
                    }

                } catch (Exception e) {
                    log.error("Bad" + (type == __REMOVE ? " __REMOVE" : (type == __INSERT ? " __INSERT" : "")) + " expression: " + expr);
                    cell.setCellValue("");
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
