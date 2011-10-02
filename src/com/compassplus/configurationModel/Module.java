package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.NodeUtils;
import org.w3c.dom.Element;
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
    private String name;
    private Double weight;
    private Double secondarySalesPrice;
    private Double secondarySalesRate;
    private Double capacityStaticPrice;
    private ArrayList<CapacityTier> capacityTiers;
    private Logger log = Logger.getInstance();
    private NodeUtils nut = NodeUtils.getInstance();


    public Module(Element initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Element initialData) throws PCTDataFormatException {

        try {
            log.info("Parsing module");

            this.setName(initialData.getElementsByTagName("Name").item(0));
            this.setWeight(initialData.getElementsByTagName("Weight").item(0));
            this.setSecondarySales(initialData.getElementsByTagName("SecondarySales"));
            this.setCapacityPricing(initialData.getElementsByTagName("CapacityPricing"));

            log.info("Module successfully parsed: \nName: " + this.getName() +
                    "\nWeight: " + this.getWeight() +
                    "\nSecondarySalesPrice: " + this.getSecondarySalesPrice() +
                    "\nSecondarySalesRate: " + this.getSecondarySalesRate() +
                    "\nCapacityStaticPrice: " + this.getCapacityStaticPrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = nut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module name is not defined correctly", e.getDetails());
        }
    }

    public Double getWeight() {
        return weight;
    }

    private void setWeight(Node weight) throws PCTDataFormatException {
        try {
            this.weight = nut.getDouble(weight);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module weight is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesPrice() {
        return secondarySalesPrice;
    }

    private void setSecondarySalesPrice(Node secondarySalesPrice) throws PCTDataFormatException {
        try {
            this.secondarySalesPrice = nut.getDouble(secondarySalesPrice, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales price is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesRate() {
        return secondarySalesRate;
    }

    private void setSecondarySalesRate(Node secondarySalesRate) throws PCTDataFormatException {
        try {
            this.secondarySalesRate = nut.getDouble(secondarySalesRate, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales rate is not defined correctly", e.getDetails());
        }
    }

    private void setSecondarySales(NodeList secondarySales) throws PCTDataFormatException {
        if (secondarySales.getLength() == 1) {
            Element secondarySalesElement = (Element) secondarySales.item(0);
            try {
                this.setSecondarySalesPrice(secondarySalesElement.getElementsByTagName("Price").item(0));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setSecondarySalesRate(secondarySalesElement.getElementsByTagName("Rate").item(0));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            if (this.secondarySalesPrice == null && this.secondarySalesRate == null) {
                throw new PCTDataFormatException("Module secondary sales is not defined correctly");
            }
        } else {
            throw new PCTDataFormatException("Module secondary sales nodes length is not equals 1");
        }
    }

    public Double getCapacityStaticPrice() {
        return capacityStaticPrice;
    }

    private void setCapacityStaticPrice(Node capacityStaticPrice) throws PCTDataFormatException {
        try {
            this.capacityStaticPrice = nut.getDouble(capacityStaticPrice, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module capacity static price is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<CapacityTier> getCapacityTiers() {
        return capacityTiers;
    }

    private void setCapacityTiers(NodeList capacityTiers) throws PCTDataFormatException {
        if (capacityTiers.getLength() == 1) {
            this.capacityTiers = new ArrayList<CapacityTier>();
            Element capacityTiersElement = (Element) capacityTiers.item(0);
            NodeList tierNodes = capacityTiersElement.getElementsByTagName("Tier");
            if (tierNodes.getLength() > 0) {
                log.info("Found " + tierNodes.getLength() + " capacity tier(s)");
                for (int i = 0; i < tierNodes.getLength(); i++) {
                    Element tierElement = (Element) tierNodes.item(i);
                    try {
                        this.capacityTiers.add(new CapacityTier(tierElement));
                    } catch (PCTDataFormatException e) {
                        log.error(e);
                    }
                }
                log.info("Successfully parsed " + this.capacityTiers.size() + " capacity tier(s)");
            } else {
                throw new PCTDataFormatException("No module capacity tiers defined");
            }
        } else {
            throw new PCTDataFormatException("Module capacity pricing nodes length is not equals 1");
        }
    }

    private void setCapacityPricing(NodeList capacityPricing) throws PCTDataFormatException {
        if (capacityPricing.getLength() == 1) {
            Element capacityPricingElement = (Element) capacityPricing.item(0);
            try {
                this.setCapacityStaticPrice(capacityPricingElement.getElementsByTagName("StaticPrice").item(0));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setCapacityTiers(capacityPricingElement.getElementsByTagName("CapacityTiers"));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            if (this.capacityStaticPrice == null && (this.capacityTiers == null || this.capacityTiers.size() == 0)) {
                throw new PCTDataFormatException("Module capacity pricing is not defined correctly");
            }
        } else {
            throw new PCTDataFormatException("Module capacity pricing nodes length is not equals 1");
        }
    }
}
