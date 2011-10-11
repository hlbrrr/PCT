package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/5/11
 * Time: 9:22 AM
 */
public class CapacitiesGroup {
    private String name;
    private String shortName;

    private Map<String, Capacity> capacities = new LinkedHashMap<String, Capacity>();
    private ArrayList<CapacitiesGroup> groups = new ArrayList<CapacitiesGroup>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public CapacitiesGroup(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public CapacitiesGroup(Node name, Node shortName) throws PCTDataFormatException {
        this.setName(name);
        this.setShortName(shortName);
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }


    public void addCapacity(String key, Capacity capacity) {
        this.capacities.put(key, capacity);
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

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacities group short name is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<CapacitiesGroup> getGroups() {
        return this.groups;
    }

    public Map<String, Capacity> getCapacities() {
        return this.capacities;
    }


    public String toString() {
        String ret = this.toString("");
        return ret.endsWith("\n") ? ret.substring(0, ret.length() - 1) : ret;
    }

    public String toString(String pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(this.getName()).append(":\n");
        for (Capacity c : this.getCapacities().values()) {
            sb.append(pad).append("  -").append(c.getName()).append("\n");
        }
        for (CapacitiesGroup cg : this.getGroups()) {
            sb.append(cg.toString(pad + "  "));
        }
        return sb.toString();
    }
}
