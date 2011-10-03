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
    private XMLUtils nut = XMLUtils.getInstance();


    public Module(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {

        try {
            log.info("Parsing module");

            this.setName(nut.getNode("Name", initialData));
            this.setWeight(nut.getNode("Weight", initialData));

            try {
                this.setSecondarySalesPrice(nut.getNode("SecondarySales/Price", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setSecondarySalesRate(nut.getNode("SecondarySales/Rate", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            if (this.secondarySalesPrice == null && this.secondarySalesRate == null) {
                throw new PCTDataFormatException("Module secondary sales pricing is not defined correctly");
            }
            try {
                this.setCapacityStaticPrice(nut.getNode("CapacityPricing/StaticPrice", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setCapacityTiers(nut.getNodes("CapacityPricing/CapacityTiers/Tier", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            if (this.capacityStaticPrice == null && (this.capacityTiers == null || this.capacityTiers.size() == 0)) {
                throw new PCTDataFormatException("Module capacity pricing is not defined correctly");
            }

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

    private void setCapacityTiers(NodeList tiers) throws PCTDataFormatException {
        this.capacityTiers = new ArrayList<CapacityTier>();
        if (tiers.getLength() > 0) {
            log.info("Found " + tiers.getLength() + " capacity tier(s)");
            for (int i = 0; i < tiers.getLength(); i++) {
                try {
                    this.capacityTiers.add(new CapacityTier(tiers.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.capacityTiers.size() + " capacity tier(s)");
        } else {
            throw new PCTDataFormatException("No module capacity tiers defined");
        }
    }
}
