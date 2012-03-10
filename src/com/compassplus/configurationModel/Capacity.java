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
    private Boolean deprecated;
    private Boolean hidden;
    private String key;
    private String linkKey;
    private ArrayList<String> licenseKeys = new ArrayList<String>(0);
    private String path;
    private String name;
    private String shortName;
    private Integer minValue;
    private String hint;
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
            this.setHint(xut.getNode("Hint", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setLicenseKey(xut.getNode("LicenseKey", initialData));
            boolean linkKeyPresent = true;
            try {
                this.setLinkKey(xut.getNode("LinkKey", initialData));
            } catch (PCTDataFormatException e) {
                linkKeyPresent = false;
            }
            if (linkKeyPresent) {

            } else {
                this.setDeprecated(xut.getNode("Deprecated", initialData));
                this.setHidden(xut.getNode("Hidden", initialData));
                this.setType(xut.getNode("Type", initialData));
                this.setMinValue(xut.getNode("MinValue", initialData));
                this.setTiers(xut.getNodes("Tiers/Tier", initialData));
            }

            log.info("Capacity successfully parsed: \nKey: " + this.getKey() +
                    "\nName: " + this.getName() +
                    "\nShortName: " + this.getShortName() +
                    "\nLinkKey: " + this.getLinkKey() +
                    "\nDeprecated: " + this.isDeprecated() +
                    "\nHidden: " + this.isHidden() +
                    "\nMinValue: " + this.getMinValue() +
                    "\nType: " + this.getType());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity is not defined correctly", e.getDetails());
        }
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    private void setDeprecated(Node deprecated) throws PCTDataFormatException {
        try {
            this.deprecated = xut.getBoolean(deprecated, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module deprecated-flag is not defined correctly", e.getDetails());
        }
    }

    public Boolean isDeprecated() {
        return this.deprecated != null ? this.deprecated : false;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    private void setHidden(Node hidden) throws PCTDataFormatException {
        try {
            this.hidden = xut.getBoolean(hidden, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module hidden-flag is not defined correctly", e.getDetails());
        }
    }

    public Boolean isHidden() {
        return this.hidden != null ? this.hidden : false;
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

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity hint is not defined correctly", e.getDetails());
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

    private void setLinkKey(Node linkKey) throws PCTDataFormatException {
        try {
            this.linkKey = xut.getString(linkKey);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity link key is not defined correctly", e.getDetails());
        }
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    public String getLinkKey() {
        return this.linkKey;
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

    public boolean checkLicenseKey(String key) {
        return licenseKeys.size() == 0 || key!=null && licenseKeys.contains(key);
    }

    private void setLicenseKey(Node licenseKey) throws PCTDataFormatException {
        try {
            licenseKeys.clear();
            String licenseKeyString = xut.getString(licenseKey, true);
            if (licenseKeyString != null && licenseKeyString.trim().length() > 0) {
                String keys[] = licenseKeyString.split("\\s+");
                for (int i = 0; i < keys.length; i++) {
                    licenseKeys.add(keys[i]);
                }
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity license key is not defined correctly", e.getDetails());
        }
    }

    public Integer getType() {
        return this.type;
    }

    public Integer getMinValue() {
        return this.minValue;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    private void setType(Node type) throws PCTDataFormatException {
        try {
            this.type = xut.getInteger(type);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity type is not defined correctly", e.getDetails());
        }
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    private void setMinValue(Node minValue) throws PCTDataFormatException {
        try {
            this.minValue = xut.getInteger(minValue, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity min value is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(ArrayList<Tier> tiers) {
        this.tiers = tiers;
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
