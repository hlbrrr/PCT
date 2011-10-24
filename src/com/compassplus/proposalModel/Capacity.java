package com.compassplus.proposalModel;

import com.compassplus.configurationModel.Tier;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/4/11
 * Time: 6:08 PM
 */
public class Capacity {
    private com.compassplus.configurationModel.Capacity capacity;
    private String key;
    private Integer value;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Capacity(Node initialData, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        init(initialData, allowedCapacities);
    }

    public Capacity(com.compassplus.configurationModel.Capacity capacity, String key) {
        this.setCapacity(capacity);
        this.key = key;
    }

    private void init(Node initialData, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            log.info("Parsing capacity");

            this.setKey(xut.getNode("Key", initialData), allowedCapacities);
            this.setValue(xut.getNode("Value", initialData));

            log.info("Capacity successfully parsed: \nName: " + this.getName() +
                    "\nValue: " + this.getValue());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getCapacity().getName();
    }

    public String getKey() {
        return this.key;
    }

    public Integer getValue() {
        return this.value;
    }

    private void setValue(Node value) throws PCTDataFormatException {
        try {
            this.value = xut.getInteger(value);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity value is not defined correctly", e.getDetails());
        }
    }

    private void setKey(Node key, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            this.key = keyString;
            this.setCapacity(allowedCapacities.get(keyString));
            if (this.getCapacity() == null) {
                throw new PCTDataFormatException("No such capacity \"" + keyString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity key is not defined correctly", e.getDetails());
        }
    }

    private com.compassplus.configurationModel.Capacity getCapacity() {
        return this.capacity;
    }

    private void setCapacity(com.compassplus.configurationModel.Capacity capacity) {
        this.capacity = capacity;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Capacity>");
        sb.append("<Key>").append(this.getKey()).append("</Key>");
        sb.append("<Value>").append(this.getValue()).append("</Value>");
        sb.append("</Capacity>");
        return sb.toString();
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Double getPrice() {
        Double price = 0d;
        if (this.getCapacity().getType().equals(1)) { // packet
            for (Tier t : this.getCapacity().getTiers()) {
                if (t.getBound() > this.getValue()) {
                    break;
                } else {
                    price = t.getPrice();
                }
            }
            price = CommonUtils.getInstance().toNextThousand(price * this.getValue());
        } else if (this.getCapacity().getType().equals(2)) { // level
            Integer used = 0;
            for (Tier t : this.getCapacity().getTiers()) {
                Integer current = t.getBound() - used;
                if (t.getBound() > this.getValue()) {
                    current = this.getValue() - used;
                }
                price += t.getPrice() * current;
                used += current;
                if (t.getBound() > this.getValue()) {
                    break;
                }
            }
            price = CommonUtils.getInstance().toNextThousand(price);
        }

        return price;
    }
}
