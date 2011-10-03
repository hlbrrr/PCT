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
    private boolean secondarySale = false;
    private ArrayList<Product> products = new ArrayList<Product>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Proposal(Configuration config) {
        this.config = config;
    }

    public void init(Document initialData) throws PCTDataFormatException {
        this.products.clear();
        try {
            this.setClientName(xut.getNode("/root/ClientName", initialData));
            NodeList products = xut.getNodes("/root/Products/Product", initialData);
            if (products.getLength() > 0) {
                log.info("Found " + products.getLength() + " product(s)");
                for (int i = 0; i < products.getLength(); i++) {
                    try {
                        this.products.add(new Product(products.item(i), this.config.getProducts()));
                    } catch (PCTDataFormatException e) {
                        log.error(e);
                    }
                }
                log.info("Successfully parsed " + this.products.size() + " product(s)");
            } else {
                throw new PCTDataFormatException("No products defined");
            }
            if (this.products.size() == 0) {
                throw new PCTDataFormatException("Products are not defined correctly");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad proposal", e.getDetails());
        }
    }

    public String getClientName() {
        return clientName;
    }

    private void setClientName(Node clientName) throws PCTDataFormatException {
        try {
            this.clientName = xut.getString(clientName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal client name is not defined correctly", e.getDetails());
        }
    }

    private void setSecondarySale(Node secondarySale) throws PCTDataFormatException {
        try {
            this.secondarySale = xut.getBoolean(secondarySale, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal secondary sale is not defined correctly", e.getDetails());
        }
    }
}
