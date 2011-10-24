package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/4/11
 * Time: 4:32 PM
 */
public class Capacity {
    private String key;
    private String path;
    private String name;
    private String shortName;
    private Integer type;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private ArrayList<Tier> tiers = new ArrayList<Tier>();

    public Capacity(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing Capacity");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setType(xut.getNode("Type", initialData));
            this.setTiers(xut.getNodes("Tiers/Tier", initialData));

            log.info("Capacity successfully parsed: \nKey: " + this.getKey() +
                    "\nName: " + this.getName() +
                    "\nShortName: " + this.getShortName() +
                    "\nType: " + this.getType());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity name is not defined correctly", e.getDetails());
        }
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity key is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return this.key;
    }

    public String getShortName() {
        return shortName;
    }

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity short name is not defined correctly", e.getDetails());
        }
    }

    public Integer getType() {
        return this.type;
    }

    private void setType(Node type) throws PCTDataFormatException {
        try {
            this.type = xut.getInteger(type);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity type is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Tier> getTiers() {
        return tiers;
    }

    private void setTiers(NodeList tiers) throws PCTDataFormatException {
        this.getTiers().clear();
        if (tiers.getLength() > 0) {
            log.info("Found " + tiers.getLength() + "  tier(s)");
            for (int i = 0; i < tiers.getLength(); i++) {
                try {
                    this.getTiers().add(new Tier(tiers.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getTiers().size() + " tier(s)");
        } else {
            throw new PCTDataFormatException("No capacity tiers defined");
        }
        if (this.getTiers().size() == 0) {
            throw new PCTDataFormatException("Capacity tiers are not defined correctly");
        }
    }

}
