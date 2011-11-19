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
public class Currency {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private String name;
    private String symbol;
    private Double rate;
    private List<String> allowedRegions = new ArrayList<String>(0);

    public Currency(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing currency");

            this.setName(xut.getNode("Name", initialData));
            this.setSymbol(xut.getNode("Symbol", initialData));
            this.setRate(xut.getNode("Rate", initialData));
            this.setAllowedRegions(xut.getNodes("Regions/Region/Key", initialData));

            log.info("Currency successfully parsed: \nName: " + this.getName() +
                    "\nSymbol: " + this.getSymbol() +
                    "\nRate: " + this.getRate());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Currency is not defined correctly", e.getDetails());
        }
    }

    public Double getRate() {
        return rate;
    }

    private void setRate(Node rate) throws PCTDataFormatException {
        try {
            this.rate = xut.getDouble(rate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Currency rate is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Currency name is not defined correctly", e.getDetails());
        }
    }

    public String getSymbol() {
        return symbol;
    }

    private void setSymbol(Node symbol) throws PCTDataFormatException {
        try {
            this.symbol = xut.getString(symbol, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Currency symbol is not defined correctly", e.getDetails());
        }
    }

    public List<String> getAllowedRegions() {
        return allowedRegions;
    }

    private void setAllowedRegions(NodeList allowedRegions) throws PCTDataFormatException {
        this.getAllowedRegions().clear();
        if (allowedRegions.getLength() > 0) {
            log.info("Found " + allowedRegions.getLength() + " allowed region(s)");
            String regionKey;
            for (int i = 0; i < allowedRegions.getLength(); i++) {
                try {
                    regionKey = xut.getString(allowedRegions.item(i));
                    this.getAllowedRegions().add(regionKey);
                    log.info("Region Key: " + regionKey);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getAllowedRegions().size() + " allowed region(s)");
        }
    }
}
