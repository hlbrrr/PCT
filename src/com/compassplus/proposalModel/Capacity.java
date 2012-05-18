package com.compassplus.proposalModel;

import com.compassplus.configurationModel.Tier;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/4/11
 * Time: 6:08 PM
 */
public class Capacity {
    private com.compassplus.configurationModel.Capacity capacity;
    private String key;
    private Integer incr;
    private Integer min;
    private Integer user;
    private Integer foc;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Capacity(Node initialData, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        init(initialData, allowedCapacities);
    }

    public Capacity(com.compassplus.configurationModel.Capacity capacity, String key) {
        this.setCapacity(capacity);
        this.key = key;
    }

    private void init(Node initialData, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            log.info("Parsing capacity");

            this.setKey(xut.getNode("Key", initialData), allowedCapacities);

            this.setIncr(xut.getNode("Value", initialData).getAttributes().getNamedItem("incr"));
            this.setMin(xut.getNode("Value", initialData).getAttributes().getNamedItem("min"));
            this.setUser(xut.getNode("Value", initialData).getAttributes().getNamedItem("user"));
            this.setFoc(xut.getNode("Value", initialData).getAttributes().getNamedItem("foc"));

            log.info("Capacity successfully parsed: \nName: " + this.getName() +
                    "\nincr: " + this.getIncr() +
                    "\nmin: " + this.getMin() +
                    "\nuser: " + this.getUser() +
                    "\nfoc: " + this.getFoc());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getCapacity().getName();
    }

    public String getKey() {
        return this.key;
    }

    public Integer getIncr() {
        return this.incr != null ? this.incr : 0;
    }

    private void setIncr(Node incr) throws PCTDataFormatException {
        try {
            this.incr = xut.getInteger(incr, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity incr is not defined correctly", e.getDetails());
        }
    }

    public Integer getUser() {
        return this.user != null ? this.user : 0;
    }

    private void setUser(Node user) throws PCTDataFormatException {
        try {
            this.user = xut.getInteger(user, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity user is not defined correctly", e.getDetails());
        }
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public void setIncr(Integer incr) {
        this.incr = incr;
    }

    public void setFoc(Integer foc) {
        this.foc = foc;
    }

    public Integer getMin() {
        return this.min != null ? this.min : 0;
    }

    private void setMin(Node min) throws PCTDataFormatException {
        try {
            this.min = xut.getInteger(min, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity min is not defined correctly", e.getDetails());
        }
    }

    public Integer getFoc() {
        return this.foc != null ? this.foc : 0;
    }

    private void setFoc(Node foc) throws PCTDataFormatException {
        try {
            this.foc = xut.getInteger(foc, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity foc is not defined correctly", e.getDetails());
        }
    }

    private void setKey(Node key, Map<String, com.compassplus.configurationModel.Capacity> allowedCapacities) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            this.key = keyString;
            this.setCapacity(allowedCapacities.get(keyString));
            if (this.getCapacity() == null) {
                throw new PCTDataFormatException("No such capacity \"" + keyString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Capacity key is not defined correctly", e.getDetails());
        }
    }

    private com.compassplus.configurationModel.Capacity getCapacity() {
        return this.capacity;
    }

    private void setCapacity(com.compassplus.configurationModel.Capacity capacity) {
        this.capacity = capacity;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Capacity>");
        sb.append("<Key>").append(this.getKey()).append("</Key>");
        sb.append("<Value");
        sb.append(" incr=\"").append(this.getIncr()).append("\"");
        sb.append(" min=\"").append(this.getMin()).append("\"");
        sb.append(" user=\"").append(this.getUser()).append("\"");
        sb.append(" foc=\"").append(this.getFoc()).append("\"");
        sb.append(" />");
        sb.append("</Capacity>");
        return sb.toString();
    }

    public Integer getVal() {
        Integer ret = this.getIncr() + this.getUser();
        if (ret < this.getMin()) {
            ret = this.getMin();
        }
        return ret;
    }

    public Integer getChargeable() {
        return this.getVal() - this.getFoc();
    }

    public Double getCleanPrice(Product product){
        Double price = 0d;
        if (this.getCapacity().getType().equals(1)) { // packet
            for (Tier t : this.getCapacity().getTiers()) {
                if (t.getBound() > this.getChargeable()) {
                    break;
                } else {
                    price = t.getPrice() * product.getProposal().getCurrencyRate();
                }
            }
            price = price * this.getChargeable();
        } else if (this.getCapacity().getType().equals(2)) { // level
            Integer used = 0;
            for (Tier t : this.getCapacity().getTiers()) {
                Integer current = t.getBound() - used;
                if (t.getBound() > this.getChargeable()) {
                    current = this.getChargeable() - used;
                }
                price += t.getPrice() * product.getProposal().getCurrencyRate() * current;
                used += current;
                if (t.getBound() > this.getChargeable()) {
                    break;
                }
            }
            if(used < this.getChargeable()){
                Double lastTierPrice = 0d;
                for (Tier t : this.getCapacity().getTiers()) {
                    lastTierPrice = t.getPrice();
                }
                price += lastTierPrice * product.getProposal().getCurrencyRate() * (this.getChargeable() - used);
            }
        }
        return price;
    }

    public Double getPrice(Product product) {
        return CommonUtils.getInstance().toNextThousand(getCleanPrice(product));
    }

    public Double getRegionalPrice(Product product) {
        return CommonUtils.getInstance().toNextThousand(getCleanPrice(product))*product.getProposal().getRegion().getRate();
    }
    /*public Double getPrice() {
        Double price = 0d;
        if (this.getCapacity().getType().equals(1)) { // packet
            for (Tier t : this.getCapacity().getTiers()) {
                if (t.getBound() > this.getValue()) {
                    break;
                } else {
                    price = t.getPrice();
                }
            }
            price = CommonUtils.getInstance().toNextThousand(price * this.getValue());
        } else if (this.getCapacity().getType().equals(2)) { // level
            Integer used = 0;
            for (Tier t : this.getCapacity().getTiers()) {
                Integer current = t.getBound() - used;
                if (t.getBound() > this.getValue()) {
                    current = this.getValue() - used;
                }
                price += t.getPrice() * current;
                used += current;
                if (t.getBound() > this.getValue()) {
                    break;
                }
            }
            price = CommonUtils.getInstance().toNextThousand(price);
        }

        return price;
    }*/
}
