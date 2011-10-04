package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:44
 */
public class Product {
    private com.compassplus.configurationModel.Product product;
    private ArrayList<Module> modules = new ArrayList<Module>();
    private ArrayList<Capacity> capacities = new ArrayList<Capacity>();
    private Boolean secondarySale = false;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        init(initialData, allowedProducts);
    }

    public Product(com.compassplus.configurationModel.Product product) {
        this.setProduct(product);
    }

    private void init(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData), allowedProducts);
            this.setSecondarySale(xut.getNode("SecondarySale", initialData));
            this.setModules(xut.getNodes("Modules/Module", initialData));
            this.setCapacities(xut.getNodes("Capacities/Capacity", initialData));

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nSecondarySale: " + this.getSecondarySale());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getProduct().getName();
    }

    private void setProduct(com.compassplus.configurationModel.Product product) {
        this.product = product;
    }

    private com.compassplus.configurationModel.Product getProduct() {
        return this.product;
    }

    private void setName(Node name, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);
            for (com.compassplus.configurationModel.Product p : allowedProducts) {
                if (p.getName().equals(nameString)) {
                    this.setProduct(p);
                    break;
                }
            }
            if (this.getProduct() == null) {
                throw new PCTDataFormatException("No such product \"" + nameString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    private void setModules(NodeList modules) {
        this.getModules().clear();
        if (modules.getLength() > 0) {
            log.info("Found " + modules.getLength() + " modules(s)");
            for (int i = 0; i < modules.getLength(); i++) {
                try {
                    this.getModules().add(new Module(modules.item(i), this.getProduct().getModules()));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getModules().size() + " modules(s)");
        }
    }

    public void addModule(com.compassplus.configurationModel.Module module) {
        this.getModules().add(new Module(module));
    }

    public ArrayList<Capacity> getCapacities() {
        return this.capacities;
    }

    private void setCapacities(NodeList capacities) {
        this.getCapacities().clear();
        if (capacities.getLength() > 0) {
            log.info("Found " + capacities.getLength() + " capacity(s)");
            for (int i = 0; i < capacities.getLength(); i++) {
                try {
                    this.getCapacities().add(new Capacity(capacities.item(i), this.getProduct().getCapacities()));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(s)");
        }
    }

    public void addCapacity(com.compassplus.configurationModel.Capacity capacity) {
        this.getCapacities().add(new Capacity(capacity));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Product>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<SecondarySale>").append(this.getSecondarySale()).append("</SecondarySale>");
        if (this.getModules() != null && this.getModules().size() > 0) {
            sb.append("<Modules>");
            for (Module m : this.getModules()) {
                sb.append(m.toString());
            }
            sb.append("</Modules>");
        }
        if (this.getCapacities() != null && this.getCapacities().size() > 0) {
            sb.append("<Capacities>");
            for (Capacity c : this.getCapacities()) {
                sb.append(c.toString());
            }
            sb.append("</Capacities>");
        }
        sb.append("</Product>");
        return sb.toString();
    }

    public Boolean getSecondarySale() {
        return this.secondarySale;
    }

    private void setSecondarySale(Node secondarySale) throws PCTDataFormatException {
        try {
            this.secondarySale = xut.getBoolean(secondarySale, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product secondary sale is not defined correctly", e.getDetails());
        }
    }

    public void setSecondarySale(Boolean secondarySale) {
        this.secondarySale = secondarySale;
    }
}
