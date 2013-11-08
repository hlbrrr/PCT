package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 11/19/11
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class OracleOption {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private String key;
    private String name;
    private String shortName;
    private String hint;
    private Double basePrice;
    private Double supportPrice;

    public OracleOption(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing oracle option");

            this.setName(xut.getNode("Name", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setKey(xut.getNode("Key", initialData));
            this.setBasePrice(xut.getNode("BasePrice", initialData));
            this.setHint(xut.getNode("Hint", initialData));
            this.setSupportPrice(xut.getNode("SupportPrice", initialData));

            log.info("Oracle option successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nBasePrice: " + this.getBasePrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option is not defined correctly: \nKey: " + this.getKey(), e.getDetails());
        }
    }

    public Double getSupportPrice() {
        return supportPrice;
    }

    private void setSupportPrice(Node supportPrice) throws PCTDataFormatException {
        try {
            this.supportPrice = xut.getDouble(supportPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license supportPrice is not defined correctly", e.getDetails());
        }
    }

    public Double getBasePrice() {
        return basePrice;
    }

    private void setBasePrice(Node basePrice) throws PCTDataFormatException {
        try {
            this.basePrice = xut.getDouble(basePrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option basePrice is not defined correctly", e.getDetails());
        }
    }

    public String getShortName() {
        return shortName;
    }

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option shortName is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option key is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle option hint is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
