package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/5/11
 * Time: 9:22 AM
 */
public class CapacitiesGroup {
    private String name;

    private ArrayList<Capacity> capacities = new ArrayList<Capacity>();
    private ArrayList<CapacitiesGroup> groups = new ArrayList<CapacitiesGroup>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public CapacitiesGroup(String name) {
        this.name = name;
    }

    public CapacitiesGroup(Node name) throws PCTDataFormatException {
        this.setName(name);
    }

    public String getName() {
        return this.name;
    }

    public void addCapacity(Capacity capacity) {
        this.capacities.add(capacity);
    }

    public void addCapacitiesGroup(CapacitiesGroup capacitiesGroup) {
        this.groups.add(capacitiesGroup);
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacities group name is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<CapacitiesGroup> getGroups() {
        return this.groups;
    }

    public ArrayList<Capacity> getCapacities() {
        return this.capacities;
    }


    public String toString() {
        return this.toString("");
    }

    public String toString(String pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(this.getName()).append(":\n");
        for (Capacity c : this.getCapacities()) {
            sb.append(pad).append("  -").append(c.getName()).append("\n");
        }
        for (CapacitiesGroup cg : this.getGroups()) {
            sb.append(cg.toString(pad + "  "));
        }
        String ret = sb.toString();
        if(ret.endsWith("\n")){
            ret = ret.substring(0, ret.length()-1);
        }
        return ret;
    }
}
