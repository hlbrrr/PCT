package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:44
 */
public class Module {
    private String name;
    private Integer capacity;

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Module(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            this.setName(xut.getNode("Name", initialData));
            this.setCapacity(xut.getNode("Capacity", initialData));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module is not defined correctly", e.getDetails());
        }
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

    public Integer getCapacity() {
        return capacity;
    }

    private void setCapacity(Node capacity) throws PCTDataFormatException {
        try {
            this.capacity = xut.getInteger(capacity, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module capacity is not defined correctly", e.getDetails());
        }
    }
}
