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
public class Module {
    private Boolean deprecated;
    private Boolean hidden;
    private boolean isRadioMember = false;
    private String key;
    private String path;
    private String name;
    private String shortName;
    private String hint;
    private Double weight;
    private Double secondarySalesPrice;
    private ArrayList<String> requireModules = new ArrayList<String>(0);
    private ArrayList<String> excludeModules = new ArrayList<String>(0);
    private HashMap<String, RequireCapacity> requireCapacities = new HashMap<String, RequireCapacity>(0);
    private Double secondarySalesRate;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();


    public Module(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing module");

            this.setKey(xut.getNode("Key", initialData));
            this.setDeprecated(xut.getNode("Deprecated", initialData));
            this.setHidden(xut.getNode("Hidden", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setWeight(xut.getNode("Weight", initialData));
            this.setHint(xut.getNode("Hint", initialData));
            this.setRequireModules(xut.getNodes("Dependencies/Require", initialData));
            this.setExcludeModules(xut.getNodes("Dependencies/Exclude", initialData));
            this.setRequireCapacities(xut.getNodes("Dependencies/RequireCapacity", initialData));

            try {
                this.setSecondarySalesPrice(xut.getNode("SecondarySales/Price", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            try {
                this.setSecondarySalesRate(xut.getNode("SecondarySales/Rate", initialData));
            } catch (PCTDataFormatException e) {
                log.error(e);
            }
            /*if (this.secondarySalesPrice == null && this.secondarySalesRate == null) {
                throw new PCTDataFormatException("Module secondary sales pricing is not defined correctly");
            }*/
            log.info("Module successfully parsed: \nKey: " + this.getKey() +
                    "\nDeprecated: " + this.isDeprecated() +
                    "\nHidden: " + this.isHidden() +
                    "\nName: " + this.getName() +
                    "\nShortName: " + this.getShortName() +
                    "\nWeight: " + this.getWeight() +
                    "\nSecondarySalesPrice: " + this.getSecondarySalesPrice() +
                    "\nSecondarySalesRate: " + this.getSecondarySalesRate());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module is not defined correctly", e.getDetails());
        }
    }

    private void setDeprecated(Node deprecated) throws PCTDataFormatException {
        try {
            this.deprecated = xut.getBoolean(deprecated, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module deprecated-flag is not defined correctly", e.getDetails());
        }
    }

    private void setHidden(Node hidden) throws PCTDataFormatException {
        try {
            this.hidden = xut.getBoolean(hidden, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module hidden-flag is not defined correctly", e.getDetails());
        }
    }

    public Boolean isDeprecated() {
        return this.deprecated != null ? this.deprecated : false;
    }

    public Boolean isHidden() {
        return this.hidden != null ? this.hidden : false;
    }

    private void setRequireModules(NodeList requireModules) {
        this.getRequireModules().clear();
        if (requireModules.getLength() > 0) {

            log.info("Found " + requireModules.getLength() + " \"require\" module(s)");
            for (int i = 0; i < requireModules.getLength(); i++) {
                try {
                    this.getRequireModules().add(xut.getString(requireModules.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRequireModules().size() + " \"require\" module(s)");
        }
    }

    private void setExcludeModules(NodeList excludeModules) {
        this.getExcludeModules().clear();
        if (excludeModules.getLength() > 0) {

            log.info("Found " + excludeModules.getLength() + " \"exclude\" module(s)");
            for (int i = 0; i < excludeModules.getLength(); i++) {
                try {
                    this.getExcludeModules().add(xut.getString(excludeModules.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getExcludeModules().size() + " \"exclude\" module(s)");
        }
    }

    private void setRequireCapacities(NodeList requireCapacities) {
        this.getRequireCapacities().clear();
        if (requireCapacities.getLength() > 0) {

            log.info("Found " + requireCapacities.getLength() + " \"require\" capacity(ies)");
            for (int i = 0; i < requireCapacities.getLength(); i++) {
                try {
                    RequireCapacity tmpRC = new RequireCapacity(requireCapacities.item(i));
                    this.getRequireCapacities().put(tmpRC.getKey(), tmpRC);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRequireCapacities().size() + " \"require\" capacity(ies)");
        }
    }

    public ArrayList<String> getRequireModules() {
        return requireModules;
    }

    public ArrayList<String> getExcludeModules() {
        return excludeModules;
    }

    public HashMap<String, RequireCapacity> getRequireCapacities() {
        return requireCapacities;
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module name is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module key is not defined correctly", e.getDetails());
        }
    }

    public String getShortName() {
        return shortName;
    }

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module short name is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module hint is not defined correctly", e.getDetails());
        }
    }

    public Double getWeight() {
        return weight;
    }

    private void setWeight(Node weight) throws PCTDataFormatException {
        try {
            this.weight = xut.getDouble(weight);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module weight is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesPrice() {
        return secondarySalesPrice;
    }

    private void setSecondarySalesPrice(Node secondarySalesPrice) throws PCTDataFormatException {
        try {
            this.secondarySalesPrice = xut.getDouble(secondarySalesPrice, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales price is not defined correctly", e.getDetails());
        }
    }

    public Double getSecondarySalesRate() {
        return secondarySalesRate;
    }

    private void setSecondarySalesRate(Node secondarySalesRate) throws PCTDataFormatException {
        try {
            this.secondarySalesRate = xut.getDouble(secondarySalesRate, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module secondary sales rate is not defined correctly", e.getDetails());
        }
    }

    public Double getPrice(com.compassplus.proposalModel.Product product) {
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
        return CommonUtils.getInstance().toNextThousand(price);
    }

    public void setIsRadioMember(boolean isRadioMember) {
        this.isRadioMember = isRadioMember;
    }

    public boolean isRadioMember() {
        return this.isRadioMember;
    }
}
