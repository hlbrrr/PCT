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
    private Integer moreThan;
    private Double price;

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Tier(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing tier");

            this.setMoreThan(xut.getNode("MoreThan", initialData));
            this.setPrice(xut.getNode("Price", initialData));

            log.info("Tier successfully parsed: \nMoreThan: " + this.getMoreThan() +
                    "\nPrice: " + this.getPrice());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Tier is not defined correctly", e.getDetails());
        }
    }

    public Integer getMoreThan() {
        return moreThan;
    }

    private void setMoreThan(Node moreThan) throws PCTDataFormatException {
        try {
            this.moreThan = xut.getInteger(moreThan);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Tier \"more than\" is not defined correctly", e.getDetails());
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
