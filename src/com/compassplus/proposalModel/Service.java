package com.compassplus.proposalModel;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/12/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Service {
    private final static java.util.Random random = new java.util.Random();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    private Recommendation recommendation = null;
    private com.compassplus.configurationModel.Service service = null;
    private String name = null;
    private String hint = null;

    private Double increment = 0d;
    private Double substitute = 0d;
    private Boolean charge = true;
    private Double osdIncrement = 0d;
    private Double osdSubstitute = 0d;
    private Double ostIncrement = 0d;
    private Double ostSubstitute = 0d;
    private String randomKey = null;


    /* public Service(Node initialData, Map<String, com.compassplus.configurationModel.Service> allowedServices) throws PCTDataFormatException {
        init(initialData, allowedServices);
    }*/

/*    public Service(com.compassplus.configurationModel.Service service, String key) {
        this.setService(service);
        this.key = key;
    }*/

    public Service(Node initialData, Configuration config) throws PCTDataFormatException {
        try {
            log.info("Parsing service");

            this.setRecommendation(xut.getNode("RecommendationKey", initialData), config);
            if (this.getRecommendation() == null) {
                this.setService(xut.getNode("ServiceKey", initialData), config);
            } else {
                this.setService(getRecommendation().getServiceKey(), config);
            }
            try {
                this.setName(xut.getNode("Name", initialData));
            } catch (PCTDataFormatException e) {
                if (this.getRecommendation() != null) this.name = getRecommendation().getName();
            }
            try {
                this.setHint(xut.getNode("Hint", initialData));
            } catch (PCTDataFormatException e) {
                if (this.getRecommendation() != null) this.hint = getRecommendation().getHint();
            }

            this.setIncrement(xut.getNode("Increment", initialData));
            this.setSubstitute(xut.getNode("Substitute", initialData));
            this.setCharge(xut.getNode("Charge", initialData));

            this.setOSDIncrement(xut.getNode("OSDIncrement", initialData));
            this.setOSDSubstitute(xut.getNode("OSDSubstitute", initialData));

            this.setOSTIncrement(xut.getNode("OSTIncrement", initialData));
            this.setOSTSubstitute(xut.getNode("OSTSubstitute", initialData));

            log.info("Service successfully parsed: \nName: " + this.getName());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service is not defined correctly", e.getDetails());
        }
    }

    public Service(Recommendation recommendation, Configuration config) throws PCTDataFormatException {
        setRecommendation(recommendation);
        this.name = getRecommendation().getName();
        this.hint = getRecommendation().getHint();
        this.setService(getRecommendation().getServiceKey(), config);
    }

    public Recommendation getRecommendation() {
        return this.recommendation;
    }

    private void setRecommendation(Recommendation recommendation){
        this.recommendation = recommendation;
    }

    private void setRecommendation(Node key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key, true);
            if (!"".equals(keyString)) {
                recommendation = config.getRecommendations().get(keyString);
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service recommendation key is not defined correctly", e.getDetails());
        }
    }

    public com.compassplus.configurationModel.Service getService() {
        return this.service;
    }


    private void setService(String key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = key;
            service = config.getServices().get(keyString);
            if (service == null) {
                throw new PCTDataFormatException("No such service");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service service key is not defined correctly", e.getDetails());
        }
    }

    private void setService(Node key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            service = config.getServices().get(keyString);
            if (service == null) {
                throw new PCTDataFormatException("No such service");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service service key is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service name is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return this.hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service hint is not defined correctly", e.getDetails());
        }
    }

    public Boolean getCharge() {
        return this.charge;
    }

    private void setCharge(Node charge) throws PCTDataFormatException {
        try {
            this.charge = xut.getBoolean(charge);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service charge is not defined correctly", e.getDetails());
        }
    }

    public Double getIncrement() {
        return this.increment;
    }

    private void setIncrement(Node increment) throws PCTDataFormatException {
        try {
            this.increment = xut.getDouble(increment);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service increment is not defined correctly", e.getDetails());
        }
    }

    public Double getSubstitute() {
        return this.substitute;
    }

    private void setSubstitute(Node substitute) throws PCTDataFormatException {
        try {
            this.substitute = xut.getDouble(substitute);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service substitute is not defined correctly", e.getDetails());
        }
    }

    public Double getOSDIncrement() {
        return this.osdIncrement;
    }

    private void setOSDIncrement(Node osdIncrement) throws PCTDataFormatException {
        try {
            this.osdIncrement = xut.getDouble(osdIncrement);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service osdIncrement is not defined correctly", e.getDetails());
        }
    }

    public Double getOSDSubstitute() {
        return this.osdSubstitute;
    }

    private void setOSDSubstitute(Node osdSubstitute) throws PCTDataFormatException {
        try {
            this.osdSubstitute = xut.getDouble(osdSubstitute);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service osdSubstitute is not defined correctly", e.getDetails());
        }
    }

    public Double getOSTIncrement() {
        return this.ostIncrement;
    }

    private void setOSTIncrement(Node ostIncrement) throws PCTDataFormatException {
        try {
            this.ostIncrement = xut.getDouble(ostIncrement);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service ostIncrement is not defined correctly", e.getDetails());
        }
    }

    public Double getOSTSubstitute() {
        return this.ostSubstitute;
    }

    private void setOSTSubstitute(Node ostSubstitute) throws PCTDataFormatException {
        try {
            this.ostSubstitute = xut.getDouble(ostSubstitute);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service ostSubstitute is not defined correctly", e.getDetails());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Service>");
        if (getRecommendation() != null) {
            sb.append("<RecommendationKey>").append(getRecommendation().getKey()).append("</RecommendationKey>");
        }
        sb.append("<ServiceKey>").append(getService().getKey()).append("</ServiceKey>");
        sb.append("<Name>").append(getName()).append("</Name>");
        sb.append("<Hint>").append(getHint()).append("</Hint>");
        sb.append("<Charge>").append(getCharge()).append("</Charge>");
        sb.append("<Increment>").append(getIncrement()).append("</Increment>");
        sb.append("<Substitute>").append(getSubstitute()).append("</Substitute>");
        sb.append("<OSDIncrement>").append(getOSDIncrement()).append("</OSDIncrement>");
        sb.append("<OSDSubstitute>").append(getOSDSubstitute()).append("</OSDSubstitute>");
        sb.append("<OSTIncrement>").append(getOSTIncrement()).append("</OSTIncrement>");
        sb.append("<OSTSubstitute>").append(getOSTSubstitute()).append("</OSTSubstitute>");
        sb.append("</Service>");
        return sb.toString();
    }

    public String getKey() {
        if (getRecommendation() != null) {
            return getRecommendation().getKey();
        } else if (this.randomKey != null) {
        } else {
            this.randomKey = (new Integer(random.nextInt())).toString();
        }
        return this.randomKey;
    }

    /*   public Double getPrice(Product product) {
        return this.getService().getPrice(product);
    }*/
}