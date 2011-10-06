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
public class Tier {
    private Integer bound;
    private Double price;

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Tier(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing tier");

            this.setBound(xut.getNode("Bound", initialData));
            this.setPrice(xut.getNode("Price", initialData));

            log.info("Tier successfully parsed: \nBound: " + this.getBound() +
                    "\nPrice: " + this.getPrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Tier is not defined correctly", e.getDetails());
        }
    }

    public Integer getBound() {
        return bound;
    }

    private void setBound(Node bound) throws PCTDataFormatException {
        try {
            this.bound = xut.getInteger(bound);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Tier bound is not defined correctly", e.getDetails());
        }
    }

    public Double getPrice() {
        return price;
    }

    private void setPrice(Node price) throws PCTDataFormatException {
        try {
            this.price = xut.getDouble(price);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Tier price is not defined correctly", e.getDetails());
        }
    }
}
