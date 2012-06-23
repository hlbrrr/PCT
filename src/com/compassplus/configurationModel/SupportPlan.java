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
 * User: arudin
 * Date: 11/19/11
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SupportPlan {
    private String key;
    private String name;
    private Double rate;
    private Double minPrice;
    private Boolean idefault;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private Map<String, Double> regionSettings = new HashMap<String, Double>(0);

    public SupportPlan(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing support plan");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setRate(xut.getNode("Rate", initialData));
            this.setMinPrice(xut.getNode("MinPrice", initialData));
            this.setDefault(xut.getNode("Default", initialData));
            this.setRegionSettings(xut.getNodes("Regions/Region", initialData));

            log.info("Region successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nRate: " + this.getRate() +
                    "\nDefault: " + this.isDefault() +
                    "\nMinPrice: " + this.getMinPrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan key is not defined correctly", e.getDetails());
        }
    }

    public Double getRate() {
        return rate;
    }

    private void setRate(Node rate) throws PCTDataFormatException {
        try {
            this.rate = xut.getDouble(rate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan rate is not defined correctly", e.getDetails());
        }
    }

    public Double getMinPrice() {
        return minPrice;
    }

    private void setMinPrice(Node minPrice) throws PCTDataFormatException {
        try {
            this.minPrice = xut.getDouble(minPrice, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan minimum price is not defined correctly", e.getDetails());
        }
    }

    public Boolean isDefault() {
        return this.idefault;
    }

    private void setDefault(Node idefault) throws PCTDataFormatException {
        try {
            this.idefault = xut.getBoolean(idefault);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Support plan \"default\" is not defined correctly", e.getDetails());
        }
    }

    public Map<String, Double> getRegionSettings() {
        return regionSettings;
    }

    private void setRegionSettings(NodeList regionSettings) throws PCTDataFormatException {
        this.getRegionSettings().clear();
        if (regionSettings.getLength() > 0) {
            log.info("Found " + regionSettings.getLength() + " region(s)");
            String regionKey;
            Double regionRate;
            for (int i = 0; i < regionSettings.getLength(); i++) {
                try {
                    regionKey = xut.getString(xut.getNode("Key", regionSettings.item(i)));
                    regionRate = xut.getDouble(xut.getNode("Rate", regionSettings.item(i)));
                    this.getRegionSettings().put(regionKey, regionRate);
                    log.info("Region Key: " + regionKey);
                    log.info("Region Rate: " + regionRate);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRegionSettings().size() + " region(s)");
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
