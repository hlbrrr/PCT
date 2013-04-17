package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.NodeList;

import java.util.*;

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
    private XMLUtils xut = XMLUtils.getInstance();

    private List<String> doNotExport = new ArrayList<String>();

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

    public void setExportable(boolean exportable, String key){
        if(exportable){
            doNotExport.remove(key);
        }else{
            doNotExport.add(key);
        }
    }

    public boolean isExportable(String key){
        return !doNotExport.contains(key);
    }

    public PSQuote(NodeList services, NodeList states, Proposal proposal) {
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

        this.doNotExport.clear();
        if (states.getLength() > 0) {
            log.info("Found " + states.getLength() + " saved state(s)");
            for (int i = 0; i < states.getLength(); i++) {
                try {
                    this.doNotExport.add(xut.getString(states.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.doNotExport.size() + " saved state(s)");
        }
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<PSQuotePresent>true</PSQuotePresent>");

        sb.append("<SavedState>");
        for (String s : this.doNotExport) {
            sb.append("<DoNotExport>");
            sb.append(s.toString());
            sb.append("</DoNotExport>");
        }
        sb.append("</SavedState>");

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

    public double getPrice() {
        double ret = 0d;
        for (Service s : getServices().values()) {
            ret += s.getRegionalPrice();
        }
        return ret;
    }

    public void setServices(Map<String, Service> services) {
        this.services.clear();
        this.services = services;
    }

    public void moveServiceUp(String key) {

        LinkedHashMap<String, Service> lhm = ((LinkedHashMap<String, Service>) this.getServices());
        LinkedHashMap<String, Service> tmp = new LinkedHashMap<String, Service>();

        Service s = this.getServices().get(key);

        String k_1 = null;
        for (String k : lhm.keySet()) {
            if (key.equals(k)) {
                break;
            }
            if (lhm.get(k).getService().getGroupKey().equals(s.getService().getGroupKey()) &&
                    lhm.get(k).getService().getKey().equals(s.getService().getKey())
                    ) {
                k_1 = k;
            }
        }
        if (k_1 == null) {
            return;
        }
        for (String k : lhm.keySet()) {
            if (k.equals(key)) {

            } else if (k.equals(k_1)) {
                tmp.put(key, lhm.get(key));
                tmp.put(k_1, lhm.get(k_1));
            } else {
                tmp.put(k, lhm.get(k));
            }
        }
        setServices(tmp);
    }

    public void moveServiceDown(String key) {

        LinkedHashMap<String, Service> lhm = ((LinkedHashMap<String, Service>) this.getServices());
        LinkedHashMap<String, Service> tmp = new LinkedHashMap<String, Service>();

        Service s = this.getServices().get(key);

        boolean flag = false;
        String k_1 = null;
        for (String k : lhm.keySet()) {
            if (key.equals(k)) {
                flag = true;
                continue;
            }
            if (lhm.get(k).getService().getGroupKey().equals(s.getService().getGroupKey()) &&
                    lhm.get(k).getService().getKey().equals(s.getService().getKey()) && flag
                    ) {
                k_1 = k;
                break;
            }
        }
        if (k_1 == null) {
            return;
        }
        for (String k : lhm.keySet()) {
            if (k.equals(key)) {

            } else if (k.equals(k_1)) {
                tmp.put(k_1, lhm.get(k_1));
                tmp.put(key, lhm.get(key));
            } else {
                tmp.put(k, lhm.get(k));
            }
        }
        setServices(tmp);
    }
}
