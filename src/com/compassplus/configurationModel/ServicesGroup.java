package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.proposalModel.PSQuote;
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
public class ServicesGroup {
    private String name;
    private Boolean hidden;
    private String key;
    private String hint;

    private Map<String, Service> services = new LinkedHashMap<String, Service>();
    private ArrayList<ServicesGroup> groups = new ArrayList<ServicesGroup>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public ServicesGroup(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey(){
        return this.key;
    }

    public ServicesGroup(Node key, Node name, Node hint, Node hidden) throws PCTDataFormatException {
        this.setKey(key);
        this.setName(name);
        this.setHint(hint);
        this.setHidden(hidden);
    }

    private void setHidden(Node hidden) throws PCTDataFormatException {
        try {
            this.hidden = xut.getBoolean(hidden, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Group hidden-flag is not defined correctly", e.getDetails());
        }
    }

    public Boolean isHidden() {
        return this.hidden != null ? this.hidden : false;
    }

    public String getName() {
        return this.name;
    }

    public String getHint() {
        return this.hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Services group hint is not defined correctly", e.getDetails());
        }
    }


    public void addService(String key, Service service) {
        this.services.put(key, service);
    }

    public void addServicesGroup(ServicesGroup servicesGroup) {
        this.groups.add(servicesGroup);
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Services group name is not defined correctly", e.getDetails());
        }
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Services group key is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<ServicesGroup> getGroups() {
        return this.groups;
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public String toString() {
        String ret = this.toString("");
        return ret.endsWith("\n") ? ret.substring(0, ret.length() - 1) : ret;
    }

    public String toString(String pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(this.getName()).append(":\n");
        for (Service s : this.getServices().values()) {
            sb.append(pad).append("  -").append(s.getName()).append("\n");
        }
        for (ServicesGroup sg : this.getGroups()) {
            sb.append(sg.toString(pad + "  "));
        }
        return sb.toString();
    }

    public double getRegionalPrice(PSQuote quote) {
        double ret = 0d;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(getServices().containsKey(s.getService().getKey())){
                ret += s.getRegionalPrice();
            }
        }
        return ret;
    }

    public double getCleanPrice(PSQuote quote) {
        double ret = 0d;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(getServices().containsKey(s.getService().getKey())){
                ret += s.getCleanPrice();
            }
        }
        return ret;
    }

    public boolean notEmpty(PSQuote quote) {
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(s.getService().getGroupKey().equals(this.getKey())){
                return true;
            }
        }
        return false;
    }

    public double getTotalMD(PSQuote quote) {
        double ret = 0;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(s.getService().getGroupKey().equals(this.getKey())){
                ret += s.getTotalValue();
            }
        }
        return ret;
    }
    public double getChargeableMD(PSQuote quote) {
        double ret = 0;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(s.getService().getGroupKey().equals(this.getKey())){
                if(s.getCharge()){
                    ret += s.getTotalValue();
                }
            }
        }
        return ret;
    }

    public double getTotalOnsiteMD(PSQuote quote) {
        double ret = 0;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(s.getService().getGroupKey().equals(this.getKey())){
                ret += s.getOnsiteTotalValue();
            }
        }
        return ret;
    }

    public double getTotalOnsiteTrips(PSQuote quote) {
        double ret = 0;
        for(com.compassplus.proposalModel.Service s: quote.getServices().values()){
            if(s.getService().getGroupKey().equals(this.getKey())){
                ret += s.getTripTotalValue();
            }
        }
        return ret;
    }
}
