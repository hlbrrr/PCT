package com.compassplus.proposalModel;

import com.compassplus.configurationModel.ModulesGroup;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
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

    public Product(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        init(initialData, allowedProducts);
    }

    public Product(com.compassplus.configurationModel.Product product) {
        this.setProduct(product);
    }

    private void init(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
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

    private void setName(Node name, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);
            for (com.compassplus.configurationModel.Product p : allowedProducts) {
                if (p.getName().equals(nameString)) {
                    this.setProduct(p);
                    break;
                }
            }
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
                    this.getModules().put(tmpModule.getName(), tmpModule);
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
                    this.getCapacities().put(tmpCapacity.getName(), tmpCapacity);
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
        Sheet s = wb.createSheet(this.getName());
        Row primarySaleRow = s.createRow(0);
        primarySaleRow.createCell(0).setCellValue(dict.getString("primarySale"));
        primarySaleRow.createCell(1).setCellValue(this.getSecondarySale() ? "" : this.bullet);

        s.createRow(s.getLastRowNum() + 1); // empty row

        s.createRow(s.getLastRowNum() + 1).createCell(0).setCellValue(dict.getString("modules")); // modules title row

        Row tableHeaderRow = s.createRow(s.getLastRowNum() + 1);
        tableHeaderRow.createCell(0).setCellValue(dict.getString("item"));
        tableHeaderRow.createCell(1).setCellValue(dict.getString("amount"));
        tableHeaderRow.createCell(2).setCellValue(dict.getString("price"));

        this.createRows(s);

        s.autoSizeColumn(0);
        s.autoSizeColumn(1);
        s.autoSizeColumn(2);
    }

    private void createRows(Sheet s) {
        this.createRows(s, null);
    }

    private void createRows(Sheet s, ModulesGroup modulesGroup) {
        if (modulesGroup == null) {
            modulesGroup = this.product.getModulesRoot();
        } else {
            s.createRow(s.getLastRowNum() + 1).createCell(0).setCellValue(modulesGroup.getName());
        }
        for (com.compassplus.configurationModel.Module m : modulesGroup.getModules()) {
            Row moduleRow = s.createRow(s.getLastRowNum() + 1);
            moduleRow.createCell(0).setCellValue(m.getName());

            if (this.getModules().containsKey(m.getName())) {
                Double price = this.getProduct().getMaximumFunctionalityPrice() * m.getWeight() / this.getProduct().getTotalWeight(); // primary sales price
                if (this.getSecondarySale()) {
                    if (m.getSecondarySalesPrice() != null) {
                        price = m.getSecondarySalesPrice();
                    } else {
                        price *= m.getSecondarySalesRate();
                    }
                }
                moduleRow.createCell(1).setCellValue(this.bullet);
                moduleRow.createCell(2).setCellValue(price);
            } else {
                moduleRow.createCell(1).setCellValue("");
                moduleRow.createCell(2).setCellValue("");
            }
        }
        for (ModulesGroup g : modulesGroup.getGroups()) {
            this.createRows(s, g);
        }
    }
}
