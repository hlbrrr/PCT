package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class PSQuote {
    private Proposal proposal;
    private Map<String, Service> services = new LinkedHashMap<String, Service>();
    private Logger log = Logger.getInstance();
    private boolean enabled = false;

    public PSQuote(Proposal proposal) {
        this.proposal = proposal;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public PSQuote(NodeList services, Proposal proposal) {
        this.proposal = proposal;

        this.getServices().clear();
        if (services.getLength() > 0) {
            log.info("Found " + services.getLength() + " service(s)");
            for (int i = 0; i < services.getLength(); i++) {
                try {
                    Service tmpService = new Service(services.item(i), proposal);
                    this.getServices().put(tmpService.getKey(), tmpService);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getServices().size() + " services(s)");
        }
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<PSQuotePresent>true</PSQuotePresent>");
        if (this.getServices() != null && this.getServices().size() > 0) {
            sb.append("<Services>");
            for (Service s : this.getServices().values()) {
                sb.append(s.toString());
            }
            sb.append("</Services>");
        }
        return sb.toString();
    }

    public void addService(Service service) {
        this.getServices().put(service.getKey(), service);
    }

    public void delService(String key) {
        this.getServices().remove(key);
    }

    public double getPrice(){
        double ret = 0d;
        for(Service s: getServices().values()){
            ret += s.getRegionalPrice();
        }
        return ret;
    }
}
