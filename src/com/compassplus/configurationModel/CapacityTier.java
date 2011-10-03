package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:24 AM
 */
public class CapacityTier {
    private Integer lowerBound;
    private Integer upperBound;
    private Integer packageSize;
    private Double packagePrice;

    private Logger log = Logger.getInstance();
    private XMLUtils nut = XMLUtils.getInstance();

    public CapacityTier(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing capacity tier");

            this.setLowerBound(nut.getNode("LowerBound", initialData));
            this.setUpperBound(nut.getNode("UpperBound", initialData));
            this.setPackageSize(nut.getNode("PackageSize", initialData));
            this.setPackagePrice(nut.getNode("PackagePrice", initialData));

            log.info("Capacity tier successfully parsed: \nLowerBound: " + this.getLowerBound() +
                    "\nUpperBound: " + this.getUpperBound() +
                    "\nPackageSize: " + this.getPackageSize() +
                    "\nPackagePrice: " + this.getPackagePrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity tier is not defined correctly", e.getDetails());
        }
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    private void setLowerBound(Node lowerBound) throws PCTDataFormatException {
        try {
            this.lowerBound = nut.getInteger(lowerBound);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity tier lower bound is not defined correctly", e.getDetails());
        }
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    private void setUpperBound(Node upperBound) throws PCTDataFormatException {
        try {
            this.upperBound = nut.getInteger(upperBound);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity tier upper bound is not defined correctly", e.getDetails());
        }
    }

    public Integer getPackageSize() {
        return packageSize;
    }

    private void setPackageSize(Node packageSize) throws PCTDataFormatException {
        try {
            this.packageSize = nut.getInteger(packageSize);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity tier package size is not defined correctly", e.getDetails());
        }
    }

    public Double getPackagePrice() {
        return packagePrice;
    }

    private void setPackagePrice(Node packagePrice) throws PCTDataFormatException {
        try {
            this.packagePrice = nut.getDouble(packagePrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity tier package price is not defined correctly", e.getDetails());
        }
    }
}
