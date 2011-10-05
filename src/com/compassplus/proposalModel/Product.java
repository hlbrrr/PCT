package com.compassplus.proposalModel;

import com.compassplus.configurationModel.ModulesGroup;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:44
 */
public class Product {
    private static String bullet = "\u2022";
    private static ResourceBundle dict = ResourceBundle.getBundle("dictionary");
    private com.compassplus.configurationModel.Product product;
    private HashMap<String, Module> modules = new HashMap<String, Module>();
    private HashMap<String, Capacity> capacities = new HashMap<String, Capacity>();
    private Boolean secondarySale = false;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData, HashMap<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        init(initialData, allowedProducts);
    }

    public Product(com.compassplus.configurationModel.Product product) {
        this.setProduct(product);
    }

    private void init(Node initialData, HashMap<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData), allowedProducts);
            this.setSecondarySale(xut.getNode("SecondarySale", initialData));
            this.setModules(xut.getNodes("Modules/Module", initialData));
            this.setCapacities(xut.getNodes("Capacities/Capacity", initialData));

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nSecondarySale: " + this.getSecondarySale());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getProduct().getName();
    }

    private void setProduct(com.compassplus.configurationModel.Product product) {
        this.product = product;
    }

    private com.compassplus.configurationModel.Product getProduct() {
        return this.product;
    }

    private void setName(Node name, HashMap<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);
            this.setProduct(allowedProducts.get(nameString));
            if (this.getProduct() == null) {
                throw new PCTDataFormatException("No such product \"" + nameString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public HashMap<String, Module> getModules() {
        return this.modules;
    }

    private void setModules(NodeList modules) {
        this.getModules().clear();
        if (modules.getLength() > 0) {
            log.info("Found " + modules.getLength() + " modules(s)");
            for (int i = 0; i < modules.getLength(); i++) {
                try {
                    Module tmpModule = new Module(modules.item(i), this.getProduct().getModules());
                    this.getModules().put(tmpModule.getKey(), tmpModule);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getModules().size() + " modules(s)");
        }
    }

    public void addModule(com.compassplus.configurationModel.Module module) {
        this.getModules().put(module.getName(), new Module(module));
    }

    public HashMap<String, Capacity> getCapacities() {
        return this.capacities;
    }

    private void setCapacities(NodeList capacities) {
        this.getCapacities().clear();
        if (capacities.getLength() > 0) {
            log.info("Found " + capacities.getLength() + " capacity(s)");
            for (int i = 0; i < capacities.getLength(); i++) {
                try {
                    Capacity tmpCapacity = new Capacity(capacities.item(i), this.getProduct().getCapacities());
                    this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(s)");
        }
    }

    public void addCapacity(com.compassplus.configurationModel.Capacity capacity) {
        this.getCapacities().put(capacity.getName(), new Capacity(capacity));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Product>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<SecondarySale>").append(this.getSecondarySale()).append("</SecondarySale>");
        if (this.getModules() != null && this.getModules().size() > 0) {
            sb.append("<Modules>");
            for (Module m : this.getModules().values()) {
                sb.append(m.toString());
            }
            sb.append("</Modules>");
        }
        if (this.getCapacities() != null && this.getCapacities().size() > 0) {
            sb.append("<Capacities>");
            for (Capacity c : this.getCapacities().values()) {
                sb.append(c.toString());
            }
            sb.append("</Capacities>");
        }
        sb.append("</Product>");
        return sb.toString();
    }

    public Boolean getSecondarySale() {
        return this.secondarySale;
    }

    private void setSecondarySale(Node secondarySale) throws PCTDataFormatException {
        try {
            this.secondarySale = xut.getBoolean(secondarySale, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product secondary sale is not defined correctly", e.getDetails());
        }
    }

    public void setSecondarySale(Boolean secondarySale) {
        this.secondarySale = secondarySale;
    }

    public void createSheet(Workbook wb) {
        /* fonts */
        Font calibri = wb.createFont();
        calibri.setFontName("Calibri");
        calibri.setFontHeightInPoints((short) 11);

        Font calibriBoldItalic = wb.createFont();
        calibriBoldItalic.setFontName("Calibri");
        calibriBoldItalic.setFontHeightInPoints((short) 11);
        calibriBoldItalic.setBoldweight(Font.BOLDWEIGHT_BOLD);
        calibriBoldItalic.setItalic(true);

        Font arialBlack = wb.createFont();
        arialBlack.setFontName("Calibri");
        arialBlack.setBoldweight(Font.BOLDWEIGHT_BOLD);
        arialBlack.setFontHeightInPoints((short) 11);

        /* sheet */
        Sheet s = wb.createSheet(this.getName());
        {
            s.setDisplayGridlines(false);

            s.setColumnWidth(0, 55 * 256);
            s.setColumnWidth(1, 9 * 256);
            s.setColumnWidth(2, 10 * 256);
        }
        {   /* primary sale */
            Row row = s.createRow(0);
            //row.setHeightInPoints(16.5f);

            CellStyle cs1 = wb.createCellStyle();
            cs1.setAlignment(CellStyle.ALIGN_RIGHT);
            cs1.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs1.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
            cs1.setFont(arialBlack);
            CellUtil.createCell(row, 0, dict.getString("primarySale"), cs1);

            CellStyle cs2 = wb.createCellStyle();
            cs2.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs2.setAlignment(CellStyle.ALIGN_CENTER);
            cs2.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            cs2.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs2.setBorderRight(CellStyle.BORDER_MEDIUM);
            cs2.setFont(arialBlack);
            CellUtil.createCell(row, 1, this.getSecondarySale() ? "" : this.bullet, cs2);
        }
        {   /* empty row */
            s.createRow(s.getLastRowNum() + 1);//.setHeightInPoints(15.75f);
        }
        {   /* modules header */
            Row row = s.createRow(s.getLastRowNum() + 1);
            //row.setHeightInPoints(16.5f);

            CellStyle cs1 = wb.createCellStyle();
            cs1.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs1.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
            cs1.setFont(arialBlack);
            CellUtil.createCell(row, 0, dict.getString("modules"), cs1);

            CellStyle cs2 = wb.createCellStyle();
            cs2.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs2.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs2.setFont(arialBlack);
            CellUtil.createCell(row, 1, "", cs2);

            CellStyle cs3 = wb.createCellStyle();
            cs3.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs3.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
            cs3.setFont(arialBlack);
            CellUtil.createCell(row, 2, "", cs3);
        }
        {   /* modules table header */
            Row row = s.createRow(s.getLastRowNum() + 1);
            //row.setHeightInPoints(15.75f);

            CellStyle cs1 = wb.createCellStyle();
            cs1.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs1.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
            cs1.setFont(calibriBoldItalic);
            CellUtil.createCell(row, 0, dict.getString("item"), cs1);

            CellStyle cs2 = wb.createCellStyle();
            cs2.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs2.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs2.setFont(calibriBoldItalic);
            CellUtil.createCell(row, 1, dict.getString("amount"), cs2);

            CellStyle cs3 = wb.createCellStyle();
            cs3.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs3.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
            cs3.setFont(calibriBoldItalic);
            CellUtil.createCell(row, 2, dict.getString("price"), cs3);
        }
        int modulesBeg = s.getLastRowNum() + 2;
        {   /* modules list */
            this.createModulesRows(s);
        }
        int modulesEnd = s.getLastRowNum() + 1;
        {   /* empty row */
            Row row = s.createRow(s.getLastRowNum() + 1);//.setHeightInPoints(15.75f);
            CellStyle cs = wb.createCellStyle();
            cs.setBorderTop(CellStyle.BORDER_MEDIUM);
            CellUtil.createCell(row, 0, "", cs);
            CellUtil.createCell(row, 1, "", cs);
            CellUtil.createCell(row, 2, "", cs);
        }
        {   /* modules total */
            Row row = s.createRow(s.getLastRowNum() + 1);
            //row.setHeightInPoints(15.75f);

            CellStyle cs1 = wb.createCellStyle();
            cs1.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs1.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
            cs1.setFont(arialBlack);
            CellUtil.createCell(row, 0, dict.getString("modulesTotal").replace("%product%", this.getName()), cs1);

            CellStyle cs2 = wb.createCellStyle();
            cs2.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs2.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs2.setFont(arialBlack);
            CellUtil.createCell(row, 1, "", cs2);

            CellStyle cs3 = wb.createCellStyle();
            cs3.setBorderTop(CellStyle.BORDER_MEDIUM);
            cs3.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
            cs3.setDataFormat(s.getWorkbook().createDataFormat().getFormat("$#,##0"));
            cs3.setFont(arialBlack);
            Cell c = row.createCell(2);
            c.setCellStyle(cs3);
            c.setCellFormula("SUM(C" + modulesBeg + ":C" + modulesEnd + ")");
        }

    }

    private void createModulesRows(Sheet s) {
        this.createModulesRows(s, null, 0);
    }

    private void createModulesRows(Sheet s, ModulesGroup modulesGroup, int level) {
        /* fonts */
        Font calibri = s.getWorkbook().createFont();
        calibri.setFontName("Calibri");
        calibri.setFontHeightInPoints((short) 11);

        Font arialBlack = s.getWorkbook().createFont();
        arialBlack.setFontName("Calibri");
        arialBlack.setBoldweight(Font.BOLDWEIGHT_BOLD);
        arialBlack.setFontHeightInPoints((short) 11);

        if (modulesGroup == null) {
            modulesGroup = this.product.getModulesRoot();
        } else {
            Row row = s.createRow(s.getLastRowNum() + 1);
            //row.setHeightInPoints(16.5f);

            CellStyle cs1 = s.getWorkbook().createCellStyle();
            CellStyle cs2 = s.getWorkbook().createCellStyle();
            CellStyle cs3 = s.getWorkbook().createCellStyle();
            if (level == 1) {
                cs1.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                cs1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs1.setBorderTop(CellStyle.BORDER_MEDIUM);
                cs1.setBorderBottom(CellStyle.BORDER_MEDIUM);
                cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
                cs1.setFont(arialBlack);

                cs2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                cs2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs2.setBorderTop(CellStyle.BORDER_MEDIUM);
                cs2.setBorderBottom(CellStyle.BORDER_MEDIUM);
                cs2.setFont(arialBlack);

                cs3.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                cs3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs3.setBorderTop(CellStyle.BORDER_MEDIUM);
                cs3.setBorderBottom(CellStyle.BORDER_MEDIUM);
                cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
                cs3.setFont(arialBlack);
            } else {
                cs1.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                cs1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs1.setBorderTop(CellStyle.BORDER_THIN);
                cs1.setBorderBottom(CellStyle.BORDER_THIN);
                cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
                cs1.setFont(calibri);

                cs2.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                cs2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs2.setBorderTop(CellStyle.BORDER_THIN);
                cs2.setBorderBottom(CellStyle.BORDER_THIN);
                cs2.setFont(calibri);

                cs3.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
                cs3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cs3.setBorderTop(CellStyle.BORDER_THIN);
                cs3.setBorderBottom(CellStyle.BORDER_THIN);
                cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
                cs3.setFont(calibri);
            }
            CellUtil.createCell(row, 0, modulesGroup.getName(), cs1);
            CellUtil.createCell(row, 1, "", cs2);
            CellUtil.createCell(row, 2, "", cs3);
        }
        {
            CellStyle cs1 = s.getWorkbook().createCellStyle();
            cs1.setBorderBottom(CellStyle.BORDER_THIN);
            cs1.setBorderLeft(CellStyle.BORDER_MEDIUM);
            cs1.setWrapText(true);
            cs1.setFont(calibri);

            CellStyle cs2 = s.getWorkbook().createCellStyle();
            cs2.setAlignment(CellStyle.ALIGN_CENTER);
            cs2.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            cs2.setBorderBottom(CellStyle.BORDER_THIN);
            cs2.setFont(calibri);

            CellStyle cs3 = s.getWorkbook().createCellStyle();
            cs3.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            cs3.setBorderBottom(CellStyle.BORDER_THIN);
            cs3.setBorderRight(CellStyle.BORDER_MEDIUM);
            cs3.setDataFormat(s.getWorkbook().createDataFormat().getFormat("$#,##0"));
            cs3.setFont(calibri);
            for (String key : modulesGroup.getModules().keySet()) {
                com.compassplus.configurationModel.Module m = modulesGroup.getModules().get(key);

                Row row = s.createRow(s.getLastRowNum() + 1);
                CellUtil.createCell(row, 0, m.getName(), cs1);

                if (this.getModules().containsKey(key)) {
                    Double price = this.getProduct().getMaximumFunctionalityPrice() * m.getWeight() / this.getProduct().getTotalWeight(); // primary sales price
                    if (this.getSecondarySale()) {
                        if (m.getSecondarySalesPrice() != null) {
                            price = m.getSecondarySalesPrice();
                        } else {
                            price *= m.getSecondarySalesRate();
                        }
                    }
                    CellUtil.createCell(row, 1, this.bullet, cs2);

                    Cell c = row.createCell(2);
                    c.setCellStyle(cs3);
                    c.setCellValue(CommonUtils.getInstance().toNextThousand(price));
                } else {
                    CellUtil.createCell(row, 1, "", cs2);
                    CellUtil.createCell(row, 2, "", cs3);
                }
            }
        }
        for (ModulesGroup g : modulesGroup.getGroups()) {
            this.createModulesRows(s, g, level + 1);
        }
    }
}
