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
public class Coefficient {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private String name;
    private String key;
    private Double value;

    public Coefficient(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing coefficient");

            this.setName(xut.getNode("Name", initialData));
            this.setKey(xut.getNode("Key", initialData));
            this.setValue(xut.getNode("Value", initialData));

            log.info("Coefficient successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nValue: " + this.getValue());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Coefficient is not defined correctly: \nKey: " + this.getKey(), e.getDetails());
        }
    }

    public Double getValue() {
        return value;
    }

    private void setValue(Node value) throws PCTDataFormatException {
        try {
            this.value = xut.getDouble(value);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Coefficient value is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Coefficient name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Coefficient key is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
