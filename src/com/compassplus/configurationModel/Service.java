package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:14 AM
 */
public class Service {
    private Boolean hidden;
    private String key;
    private String name;
    private String hint;
    private Double minMD;
    private Double maxMD;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Service(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing service");

            this.setKey(xut.getNode("Key", initialData));
            this.setHidden(xut.getNode("Hidden", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setMinMD(xut.getNode("MinMD", initialData));
            this.setMaxMD(xut.getNode("MaxMD", initialData));
            this.setHint(xut.getNode("Hint", initialData));
            log.info("Service successfully parsed: \nKey: " + this.getKey() +
                    "\nHidden: " + this.isHidden() +
                    "\nName: " + this.getName() +
                    "\nMinMD: " + this.getMinMD() +
                    "\nMaxMD: " + this.getMaxMD());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service is not defined correctly: \nKey: " + this.getKey(), e.getDetails());
        }
    }

    private void setHidden(Node hidden) throws PCTDataFormatException {
        try {
            this.hidden = xut.getBoolean(hidden, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service hidden-flag is not defined correctly", e.getDetails());
        }
    }

    public Boolean isHidden() {
        return this.hidden != null ? this.hidden : false;
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service key is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service hint is not defined correctly", e.getDetails());
        }
    }

    public Double getMinMD() {
        return minMD;
    }

    private void setMinMD(Node minMD) throws PCTDataFormatException {
        try {
            this.minMD = xut.getDouble(minMD);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service minMD is not defined correctly", e.getDetails());
        }
    }

    public Double getMaxMD() {
        return maxMD;
    }

    private void setMaxMD(Node maxMD) throws PCTDataFormatException {
        try {
            this.maxMD = xut.getDouble(maxMD);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Service maxMD is not defined correctly", e.getDetails());
        }
    }

   /* Double getCleanPrice(com.compassplus.proposalModel.Product product){
        Double price = product.getProduct().getMaximumFunctionalityPrice() * product.getProposal().getCurrencyRate() * this.getWeight() / product.getProduct().getTotalWeight(); // primary sales price
        if (product.getSecondarySale()) {
            if (this.getSecondarySalesPrice() != null) {
                price = this.getSecondarySalesPrice() * product.getProposal().getCurrencyRate();
            } else if (this.getSecondarySalesRate() != null) {
                price *= this.getSecondarySalesRate();
            } else {
                price *= product.getProduct().getSecondarySalesRate();
            }
        }
        return price;
    }

    public Double getPrice(com.compassplus.proposalModel.Product product) {
        return CommonUtils.getInstance().toNextThousand(getCleanPrice(product));
    }

    public Double getRegionalPrice(com.compassplus.proposalModel.Product product){
        return CommonUtils.getInstance().toNextThousand(getCleanPrice(product))*product.getProposal().getRegion().getRate(product.getName());
    }*/

}
