package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.*;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:14 AM
 */
public class Module {
    private String key;
    private String path;
    private String name;
    private String shortName;
    private Double weight;
    private Double secondarySalesPrice;
    private ArrayList<String> requireModules = new ArrayList<String>(0);
    private ArrayList<String> excludeModules = new ArrayList<String>(0);
    private Double secondarySalesRate;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Module(Node initialData) throws PCTDataFormatException {
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
            log.info("Parsing module");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setWeight(xut.getNode("Weight", initialData));
            this.setRequireModules(xut.getNodes("Dependencies/Require", initialData));
            this.setExcludeModules(xut.getNodes("Dependencies/Exclude", initialData));

            try {
                this.setSecondarySalesPrice(xut.getNode("SecondarySales/Price", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setSecondarySalesRate(xut.getNode("SecondarySales/Rate", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            if (this.secondarySalesPrice == null && this.secondarySalesRate == null) {
                throw new PCTDataFormatException("Module secondary sales pricing is not defined correctly");
            }
            log.info("Module successfully parsed: \nKey: " + this.getKey() +
                    "\nName: " + this.getName() +
                    "\nShortName: " + this.getShortName() +
                    "\nWeight: " + this.getWeight() +
                    "\nSecondarySalesPrice: " + this.getSecondarySalesPrice() +
                    "\nSecondarySalesRate: " + this.getSecondarySalesRate());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module is not defined correctly", e.getDetails());
        }
    }

    private void setRequireModules(NodeList requireModules) {
        this.getRequireModules().clear();
        if (requireModules.getLength() > 0) {

            log.info("Found " + requireModules.getLength() + " \"require\" module(s)");
            for (int i = 0; i < requireModules.getLength(); i++) {
                try {
                    this.getRequireModules().add(xut.getString(requireModules.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRequireModules().size() + " \"require\" module(s)");
        }
    }

    private void setExcludeModules(NodeList excludeModules) {
        this.getExcludeModules().clear();
        if (excludeModules.getLength() > 0) {

            log.info("Found " + excludeModules.getLength() + " \"exclude\" module(s)");
            for (int i = 0; i < excludeModules.getLength(); i++) {
                try {
                    this.getExcludeModules().add(xut.getString(excludeModules.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getExcludeModules().size() + " \"exclude\" module(s)");
        }
    }

    public ArrayList<String> getRequireModules() {
        return requireModules;
    }

    public ArrayList<String> getExcludeModules() {
        return excludeModules;
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module key is not defined correctly", e.getDetails());
        }
    }

    public String getShortName() {
        return shortName;
    }

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module short name is not defined correctly", e.getDetails());
        }
    }

    public Double getWeight() {
        return weight;
    }

    private void setWeight(Node weight) throws PCTDataFormatException {
        try {
            this.weight = xut.getDouble(weight);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module weight is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesPrice() {
        return secondarySalesPrice;
    }

    private void setSecondarySalesPrice(Node secondarySalesPrice) throws PCTDataFormatException {
        try {
            this.secondarySalesPrice = xut.getDouble(secondarySalesPrice, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales price is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesRate() {
        return secondarySalesRate;
    }

    private void setSecondarySalesRate(Node secondarySalesRate) throws PCTDataFormatException {
        try {
            this.secondarySalesRate = xut.getDouble(secondarySalesRate, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales rate is not defined correctly", e.getDetails());
        }
    }

    public Double getPrice(com.compassplus.proposalModel.Product product) {
        Double price = product.getProduct().getMaximumFunctionalityPrice() * this.getWeight() / product.getProduct().getTotalWeight(); // primary sales price
        if (product.getSecondarySale()) {
            if (this.getSecondarySalesPrice() != null) {
                price = this.getSecondarySalesPrice();
            } else {
                price *= this.getSecondarySalesRate();
            }
        }
        return CommonUtils.getInstance().toNextThousand(price);
    }
}
