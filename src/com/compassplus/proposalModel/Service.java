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
    private com.compassplus.configurationModel.Service service;
    private String key;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Service(Node initialData, Map<String, com.compassplus.configurationModel.Service> allowedServices) throws PCTDataFormatException {
        init(initialData, allowedServices);
    }

    public Service(com.compassplus.configurationModel.Service service, String key) {
        this.setService(service);
        this.key = key;
    }

    private void init(Node initialData, Map<String, com.compassplus.configurationModel.Service> allowedServices) throws PCTDataFormatException {
        try {
            log.info("Parsing service");

            this.setKey(xut.getNode("Key", initialData), allowedServices);

            log.info("Service successfully parsed: \nName: " + this.getName());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getService().getName();
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node key, Map<String, com.compassplus.configurationModel.Service> allowedServices) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            this.key = keyString;
            this.setService(allowedServices.get(keyString));
            if (this.getService() == null) {
                throw new PCTDataFormatException("No such service \"" + keyString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service key is not defined correctly", e.getDetails());
        }
    }

    private com.compassplus.configurationModel.Service getService() {
        return this.service;
    }

    private void setService(com.compassplus.configurationModel.Service service) {
        this.service = service;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Service>");
        sb.append("<Key>").append(this.getKey()).append("</Key>");
        sb.append("</Service>");
        return sb.toString();
    }

 /*   public Double getPrice(Product product) {
        return this.getService().getPrice(product);
    }*/
}
