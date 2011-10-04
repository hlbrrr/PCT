package com.compassplus.proposalModel;

import com.compassplus.configurationModel.Configuration;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:39
 */
public class Proposal {

    private Configuration config;
    private String clientName = "";
    private ArrayList<Product> products = new ArrayList<Product>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Proposal(Configuration config) {
        this.setConfig(config);
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.setClientName(xut.getNode("/root/ClientName", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad proposal", e.getDetails());
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    private void setClientName(Node clientName) throws PCTDataFormatException {
        try {
            this.clientName = xut.getString(clientName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal client name is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Product> getProducts() {
        return this.products;
    }

    private void setProducts(NodeList products) throws PCTDataFormatException {
        this.getProducts().clear();
        if (products.getLength() > 0) {
            log.info("Found " + products.getLength() + " product(s)");
            for (int i = 0; i < products.getLength(); i++) {
                try {
                    this.getProducts().add(new Product(products.item(i), this.getConfig().getProducts()));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getProducts().size() + " product(s)");
        }
    }

    public void addProduct(com.compassplus.configurationModel.Product product) {
        this.getProducts().add(new Product(product));
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        sb.append("<ClientName>").append(this.getClientName()).append("</ClientName>");
        if (this.getProducts() != null && this.getProducts().size() > 0) {
            sb.append("<Products>");
            for (Product p : this.getProducts()) {
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
}
