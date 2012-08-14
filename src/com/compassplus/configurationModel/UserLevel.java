package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.08.12
 * Time: 22:19
 */
public class UserLevel {
    private String key;
    private String subKey;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public UserLevel(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing user level");

            this.setKey(xut.getNode("Key", initialData));
            this.setSubKey(xut.getNode("SubKey", initialData));

            log.info("User level successfully parsed: \nSubKey: " + this.getSubKey() +
                    "\nKey: " + this.getKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User level is not defined correctly: \nKey: " + this.getKey(), e.getDetails());
        }
    }

    public String getSubKey() {
        return subKey;
    }

    private void setSubKey(Node subKey) throws PCTDataFormatException {
        try {
            this.subKey = xut.getString(subKey);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User level sub key is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User level key is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString() {
        return this.getSubKey();
    }
}
