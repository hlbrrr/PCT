package com.compassplus.proposalModel;

import com.compassplus.configurationModel.ServicesGroup;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class OracleQuote {
    private Proposal proposal;
    private Map<String, OracleLicense> oracleLicenses = new LinkedHashMap<String, OracleLicense>();
    private XMLUtils xut = XMLUtils.getInstance();

    private List<String> doNotExport = new ArrayList<String>();

    private Logger log = Logger.getInstance();
    private boolean enabled = true;
    private double oracleDiscount = 0d;

    public OracleQuote(Proposal proposal) {
        this.proposal = proposal;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void setExportable(boolean exportable, String key) {
        if (exportable) {
            doNotExport.remove(key);
        } else {
            doNotExport.add(key);
        }
    }

    public boolean isExportable(String key) {
        return !doNotExport.contains(key);
    }

    public OracleQuote(NodeList oracleLicenses, NodeList states, Proposal proposal, double oracleDiscount) {
        this.proposal = proposal;
        this.oracleDiscount = oracleDiscount;

        this.getOracleLicenses().clear();
        if (oracleLicenses.getLength() > 0) {
            log.info("Found " + oracleLicenses.getLength() + " oracle license(s)");
            for (int i = 0; i < oracleLicenses.getLength(); i++) {
                try {
                    OracleLicense tmpOracleLicense = new OracleLicense(oracleLicenses.item(i), proposal);
                    this.getOracleLicenses().put(tmpOracleLicense.getProductKey(), tmpOracleLicense);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getOracleLicenses().size() + " oracle license(s)");
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

    public Map<String, OracleLicense> getOracleLicenses() {
        return this.oracleLicenses;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<OracleQuotePresent>true</OracleQuotePresent>");
        sb.append("<OracleDiscount>" + oracleDiscount + "</OracleDiscount>");

        sb.append("<OracleSavedState>");
        for (String s : this.doNotExport) {
            sb.append("<DoNotExport>");
            sb.append(s.toString());
            sb.append("</DoNotExport>");
        }
        sb.append("</OracleSavedState>");

        if (this.getOracleLicenses() != null && this.getOracleLicenses().size() > 0) {
            sb.append("<OracleLicenses>");
            for (OracleLicense s : this.getOracleLicenses().values()) {
                sb.append(s.toString());
            }
            sb.append("</OracleLicenses>");
        }
        return sb.toString();
    }

    public void addOracleLicense(OracleLicense oracleLicense) {

        this.getOracleLicenses().put(
                oracleLicense.getProductKey(),
                oracleLicense
        );
    }

    public void delOracleLicense(String key) {
        this.getOracleLicenses().remove(key);
    }

    public double getLicenseTotal() {
        double licenseTotal = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                licenseTotal += ol.getLicensePrice();
            }
        }

        return licenseTotal;
    }

    public double getOptionsTotal() {
        double optionsTotal = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                optionsTotal += ol.getOptionsPrice();
            }
        }

        return optionsTotal;
    }

    public double getSupportTotal() {
        double total = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                total += ol.getOracleTotalPrice();
            }
        }

        return total;
    }

    public double getTotalTotal() {
        double total = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                total += ol.getOracleTotalPrice();
            }
        }

        return total;
    }

    public double getCPTotal() {
        double total = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                total += ol.getOracleCPPrice();
            }
        }

        return total;
    }


    public double getCustomerTotal() {
        double total = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                total += ol.getOracleCustomerPrice();
            }
        }

        return total;
    }


    public double getTotalMargin() {
        double total = 0d;

        for(OracleLicense ol:getOracleLicenses().values()){
            if(!ol.isMemberOfAnotherBox()){
                total += ol.getOracleCPMargin();
            }
        }

        return total;
    }
}
