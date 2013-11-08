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
public class OracleLicense {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private String key;
    private String name;
    private String hint;
    private Double basePrice;
    private Double FUDiscount;
    private Double ASFUDiscount;
    private Double supportPrice;
    private List<Coefficient> coefficients = new ArrayList<Coefficient>(0);

    /*

                    config += '<Type>License</Type>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<BasePrice>' + $(that._basePrice).val() + '</BasePrice>';
                    config += '<FUDiscount>' + $(that._FUDiscount).val() + '</FUDiscount>';
                    config += '<ASFUDiscount>' + $(that._ASFUDiscount).val() + '</ASFUDiscount>';
                    config += '<SupportRate>' + $(that._supportRate).val() + '</SupportRate>';
     */

    public OracleLicense(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing oracle license");

            this.setName(xut.getNode("Name", initialData));
            this.setKey(xut.getNode("Key", initialData));
            this.setBasePrice(xut.getNode("BasePrice", initialData));
            this.setHint(xut.getNode("Hint", initialData));
            this.setFUDiscount(xut.getNode("FUDiscount", initialData));
            this.setASFUDiscount(xut.getNode("ASFUDiscount", initialData));
            this.setSupportPrice(xut.getNode("SupportPrice", initialData));

            this.setCoefficients(xut.getNodes("Coefficients/Coefficient", initialData));

            log.info("Oracle license successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey() +
                    "\nBasePrice: " + this.getBasePrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license is not defined correctly: \nKey: " + this.getKey(), e.getDetails());
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

    public Double getASFUDiscount() {
        return ASFUDiscount;
    }

    private void setASFUDiscount(Node ASFUDiscount) throws PCTDataFormatException {
        try {
            this.ASFUDiscount = xut.getDouble(ASFUDiscount);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license ASFUDiscount is not defined correctly", e.getDetails());
        }
    }


    public Double getFUDiscount() {
        return FUDiscount;
    }

    private void setFUDiscount(Node FUDiscount) throws PCTDataFormatException {
        try {
            this.FUDiscount = xut.getDouble(FUDiscount);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license FUDiscount is not defined correctly", e.getDetails());
        }
    }

    public Double getBasePrice() {
        return basePrice;
    }

    private void setBasePrice(Node basePrice) throws PCTDataFormatException {
        try {
            this.basePrice = xut.getDouble(basePrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license basePrice is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license key is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license hint is not defined correctly", e.getDetails());
        }
    }

    public List<Coefficient> getCoefficients() {
        return coefficients;
    }

    private void setCoefficients(NodeList coefficients) throws PCTDataFormatException {
        this.getCoefficients().clear();
        if (coefficients.getLength() > 0) {
            log.info("Found " + coefficients.getLength() + " coefficient(s)");
            for (int i = 0; i < coefficients.getLength(); i++) {
                try {
                    Coefficient tmpCoefficient = new Coefficient(coefficients.item(i));
                    this.getCoefficients().add(tmpCoefficient);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCoefficients().size() + " coefficient(s)");
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
