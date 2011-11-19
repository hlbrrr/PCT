package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

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
    private String defaultCurrencyName;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Region(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing region");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setRate(xut.getNode("Rate", initialData));
            this.setDefaultCurrencyName(xut.getNode("DefaultCurrency", initialData));

            log.info("Region successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nDefaultCurrency: " + this.getDefaultCurrencyName() +
                    "\nRate: " + this.getRate());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region is not defined correctly", e.getDetails());
        }
    }

    public Double getRate() {
        return rate;
    }

    private void setRate(Node rate) throws PCTDataFormatException {
        try {
            this.rate = xut.getDouble(rate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Region rate is not defined correctly", e.getDetails());
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
}
