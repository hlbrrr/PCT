package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/4/11
 * Time: 6:08 PM
 */
public class Capacity {
    private com.compassplus.configurationModel.Capacity capacity;
    private Integer value;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Capacity(Node initialData, ArrayList<com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        init(initialData, allowedCapacities);
    }

    public Capacity(com.compassplus.configurationModel.Capacity capacity) {
        this.setCapacity(capacity);
    }

    private void init(Node initialData, ArrayList<com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            log.info("Parsing capacity");

            this.setName(xut.getNode("Name", initialData), allowedCapacities);
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

    private void setName(Node name, ArrayList<com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);

            for (com.compassplus.configurationModel.Capacity c : allowedCapacities) {
                if (c.getName().equals(nameString)) {
                    this.setCapacity(c);
                    break;
                }
            }
            if (this.getCapacity() == null) {
                throw new PCTDataFormatException("No such capacity \"" + nameString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity name is not defined correctly", e.getDetails());
        }
    }

    private com.compassplus.configurationModel.Capacity getCapacity() {
        return this.capacity;
    }

    private void setCapacity(com.compassplus.configurationModel.Capacity capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Capacity>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<Value>").append(this.getValue()).append("</Value>");
        sb.append("</Capacity>");
        return sb.toString();
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
