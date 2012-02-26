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
public class License {
    private String key;
    private String name;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public License(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing license");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));

            log.info("License successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("License is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("License key is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("License name is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
