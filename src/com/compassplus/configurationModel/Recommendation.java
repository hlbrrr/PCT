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
public class Recommendation {
    private String key;
    private String name;
    private String serviceKey;
    private String hint;
    private String recommendationType;
    private String onsiteRecommendationType;
    private String percentageKeys;
    private Double mdValue;
    private Double onsiteValue;
    private Double tripValue;

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Recommendation(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing recommendation");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));

            this.setServiceKey(xut.getNode("ReferenceKey", initialData));
            this.setHint(xut.getNode("Hint", initialData));
            this.setRecommendationType(xut.getNode("RecommendationType", initialData));
            this.setOnsiteRecommendationType(xut.getNode("OnsiteRecommendationType", initialData));
            this.setPercentageKeys(xut.getNode("PercentageKeys", initialData));

            this.setMDValue(xut.getNode("MDValue", initialData));
            this.setOnsiteValue(xut.getNode("OnsiteValue", initialData));
            this.setTripValue(xut.getNode("TripValue", initialData));

            log.info("Recommendation successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public Double getMDValue() {
        return mdValue;
    }

    private void setMDValue(Node mdValue) throws PCTDataFormatException {
        try {
            this.mdValue = xut.getDouble(mdValue, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation MD value is not defined correctly", e.getDetails());
        }
    }

    public Double getOnsiteValue() {
        return onsiteValue;
    }

    private void setOnsiteValue(Node onsiteValue) throws PCTDataFormatException {
        try {
            this.onsiteValue = xut.getDouble(onsiteValue, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation onsite value is not defined correctly", e.getDetails());
        }
    }

    public Double getTripValue() {
        return tripValue;
    }

    private void setTripValue(Node tripValue) throws PCTDataFormatException {
        try {
            this.tripValue = xut.getDouble(tripValue, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation trip value is not defined correctly", e.getDetails());
        }
    }

    public String getPercentageKeys() {
        return percentageKeys;
    }

    private void setPercentageKeys(Node percentageKeys) throws PCTDataFormatException {
        try {
            this.percentageKeys = xut.getString(percentageKeys, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation percentage keys are not defined correctly", e.getDetails());
        }
    }

    public String getOnsiteRecommendationType() {
        return onsiteRecommendationType;
    }

    private void setOnsiteRecommendationType(Node onsiteRecommendationType) throws PCTDataFormatException {
        try {
            this.onsiteRecommendationType = xut.getString(onsiteRecommendationType);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation onsite recommendation type is not defined correctly", e.getDetails());
        }
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    private void setRecommendationType(Node recommendationType) throws PCTDataFormatException {
        try {
            this.recommendationType = xut.getString(recommendationType);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation recommendation type is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation hint is not defined correctly", e.getDetails());
        }
    }

    public String getServiceKey() {
        return serviceKey;
    }

    private void setServiceKey(Node serviceKey) throws PCTDataFormatException {
        try {
            this.serviceKey = xut.getString(serviceKey);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation service key is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation key is not defined correctly", e.getDetails());
        }
    }


    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Recommendation name is not defined correctly", e.getDetails());
        }
    }



    @Override
    public String toString(){
        return this.getName();
    }
}
