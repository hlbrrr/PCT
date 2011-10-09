package com.compassplus.proposalModel;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:39
 */
public class Proposal {

    private Configuration config;
    private String name = "";
    private Map<String, Product> products = new LinkedHashMap<String, Product>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Proposal(Configuration config) {
        this.setConfig(config);
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.setName(xut.getNode("/root/Name", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad proposal", e.getDetails());
        }
    }

    public String getName() {
        return this.name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal client name is not defined correctly", e.getDetails());
        }
    }

    public Map<String, Product> getProducts() {
        return this.products;
    }

    private void setProducts(NodeList products) throws PCTDataFormatException {
        this.getProducts().clear();
        if (products.getLength() > 0) {
            log.info("Found " + products.getLength() + " product(s)");
            for (int i = 0; i < products.getLength(); i++) {
                try {
                    Product tmpProduct = new Product(products.item(i), this.getConfig().getProducts());
                    this.getProducts().put(tmpProduct.getName(), tmpProduct);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getProducts().size() + " product(s)");
        }
    }

    public void addProduct(com.compassplus.configurationModel.Product product) {
        this.getProducts().put(product.getName(), new Product(product));
    }

    public void setClientName(String clientName) {
        this.name = clientName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        if (this.getProducts() != null && this.getProducts().size() > 0) {
            sb.append("<Products>");
            for (Product p : this.getProducts().values()) {
                sb.append(p.toString());
            }
            sb.append("</Products>");
        }
        sb.append("</root>");
        return sb.toString();
    }

    private Configuration getConfig() {
        return config;
    }

    private void setConfig(Configuration config) {
        this.config = config;
    }

    public Workbook getWorkbook() {
        Workbook wb = new HSSFWorkbook();
        for (Product p : this.getProducts().values()) {
            p.createSheet(wb);
        }
        return wb;
    }
}
