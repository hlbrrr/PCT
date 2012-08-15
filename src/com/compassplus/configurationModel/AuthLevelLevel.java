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
public class AuthLevelLevel {
    private String key;
    private String name;
    private Double priority;
    private String description;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public AuthLevelLevel(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing authority level level");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setPriority(xut.getNode("Priority", initialData));
            this.setDescription(xut.getNode("Description", initialData));

            log.info("Authority level level successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nPriority: " + this.getPriority() +
                    "\nDescription: " + this.getDescription());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level level is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public Double getPriority() {
        return priority;
    }

    private void setPriority(Node priority) throws PCTDataFormatException {
        try {
            this.priority = xut.getDouble(priority);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level level priority is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level level name is not defined correctly", e.getDetails());
        }
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(Node description) throws PCTDataFormatException {
        try {
            this.description = xut.getString(description, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level level description is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level level key is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
