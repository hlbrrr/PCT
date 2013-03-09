package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 11/19/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Region {
    private String key;
    private String name;
    private Double rate;
    private Double mdRate;
    private Double onsiteDailyCost;
    private String defaultCurrencyName;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private Map<String, Double> rateProducts = new HashMap<String, Double>(0);

    public Region(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing region");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setRate(xut.getNode("Rate", initialData));
            this.setMDRate(xut.getNode("MDRate", initialData));
            this.setOnsiteDailyCost(xut.getNode("OnsiteDailyCost", initialData));
            this.setDefaultCurrencyName(xut.getNode("DefaultCurrency", initialData));
            this.setRateProducts(xut.getNodes("Products/Product", initialData));

            log.info("Region successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nDefaultCurrency: " + this.getDefaultCurrencyName() +
                    "\nRate: " + this.getRate(null) +
                    "\nMDRate: " + this.getMDRate(null) +
                    "\nOnsiteDailyCost: " + this.getOnsiteDailyCost(null));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public Double getRate(String product) {
        if (product == null || rateProducts.get(product) == null) {
            return rate;
        } else {
            return rateProducts.get(product);
        }
    }
/*    public Double getRate() {
    return rate;
}*/

    public Double getMDRate(String product) {
        /*if (product == null || mdRateProducts.get(product) == null) {*/
            return mdRate;
       /* } else {
            return mdRateProducts.get(product);
        }*/
    }

    public Double getOnsiteDailyCost(String product) {
        /*if (product == null || onsiteDailyCostProducts.get(product) == null) {*/
        return onsiteDailyCost;
        /* } else {
            return onsiteDailyCostProducts.get(product);
        }*/
    }

    private void setRate(Node rate) throws PCTDataFormatException {
        try {
            this.rate = xut.getDouble(rate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region rate is not defined correctly", e.getDetails());
        }
    }

    private void setMDRate(Node mdRate) throws PCTDataFormatException {
        try {
            this.mdRate = xut.getDouble(mdRate, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region MDRate is not defined correctly", e.getDetails());
        }
    }

    private void setOnsiteDailyCost(Node onsiteDailyCost) throws PCTDataFormatException {
        try {
            this.onsiteDailyCost = xut.getDouble(onsiteDailyCost, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region onsite daily cost is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region key is not defined correctly", e.getDetails());
        }
    }


    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region name is not defined correctly", e.getDetails());
        }
    }

    public String getDefaultCurrencyName() {
        return defaultCurrencyName;
    }

    private void setDefaultCurrencyName(Node defaultCurrencyName) throws PCTDataFormatException {
        try {
            this.defaultCurrencyName = xut.getString(defaultCurrencyName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region default currency is not defined correctly", e.getDetails());
        }
    }

    public Map<String, Double> getRateProducts() {
        return rateProducts;
    }

    private void setRateProducts(NodeList rateProducts) throws PCTDataFormatException {
        this.getRateProducts().clear();
        if (rateProducts.getLength() > 0) {
            log.info("Found " + rateProducts.getLength() + " region product(s)");
            for (int i = 0; i < rateProducts.getLength(); i++) {
                Node initialData = rateProducts.item(i);
                try {
                    try {
                        log.info("Parsing region product");

                        String productName = null;
                        try {
                            productName = xut.getString(xut.getNode("Key", initialData));
                        } catch (PCTDataFormatException e) {
                            throw new PCTDataFormatException("Region product name is not defined correctly", e.getDetails());
                        }
                        Double productRate = null;
                        try {
                            productRate = xut.getDouble(xut.getNode("Rate", initialData));
                        } catch (PCTDataFormatException e) {
                            throw new PCTDataFormatException("Region product rate is not defined correctly", e.getDetails());
                        }
                        this.getRateProducts().put(productName, productRate);

                        log.info("Region product successfully parsed: \nName: " + productName +
                                "\nRate: " + productRate);
                    } catch (PCTDataFormatException e) {
                        throw new PCTDataFormatException("Region product is not defined correctly", e.getDetails());
                    }
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRateProducts().size() + " region product(s)");
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
