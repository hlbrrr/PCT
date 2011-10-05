package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:44 AM
 */
public class Product {
    private String name;
    private Double maximumFunctionalityPrice;

    private Double totalWeight;
    private Double minimumPrice;
    private ModulesGroup modulesRoot = new ModulesGroup("Modules");
    private CapacitiesGroup capacitiesRoot = new CapacitiesGroup("Capacities");
    private ArrayList<Module> modules = new ArrayList<Module>();
    private ArrayList<Capacity> capacities = new ArrayList<Capacity>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData));
            this.setMaximumFunctionalityPrice(xut.getNode("MaximumFunctionalityPrice", initialData));
            this.setMinimumPrice(xut.getNode("MinimumPrice", initialData));
            this.setModules(xut.getNode("Modules", initialData));
            this.setCapacities(xut.getNode("Capacities", initialData));
            this.setTotalWeight();

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nMaximumFunctionalityPrice: " + this.getMaximumFunctionalityPrice() +
                    "\nMinimumPrice: " + this.getMinimumPrice() +
                    "\nStructure: \n" + modulesRoot.toString() + "\n" + capacitiesRoot.toString());

        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public Double getMaximumFunctionalityPrice() {
        return maximumFunctionalityPrice;
    }

    private void setMaximumFunctionalityPrice(Node maximumFunctionalityPrice) throws PCTDataFormatException {
        try {
            this.maximumFunctionalityPrice = xut.getDouble(maximumFunctionalityPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product maximum functionality price is not defined correctly", e.getDetails());
        }
    }

    public Double getMinimumPrice() {
        return minimumPrice;
    }

    private void setMinimumPrice(Node minimumPrice) throws PCTDataFormatException {
        try {
            this.minimumPrice = xut.getDouble(minimumPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product minimum price is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    private void setModules(Node modulesNode) {
        setModules(modulesNode, null);
    }

    private void setModules(Node modulesNode, ModulesGroup modulesGroup) {
        if (modulesGroup == null) {
            this.getModules().clear();
            modulesGroup = this.getModulesRoot();
            modulesGroup.getModules().clear();
            modulesGroup.getGroups().clear();
        }

        log.info("Parsing modules group");
        NodeList modules = xut.getNodes("Module", modulesNode);
        if (modules.getLength() > 0) {
            log.info("Found " + modules.getLength() + " modules(s)");
            for (int i = 0; i < modules.getLength(); i++) {
                try {
                    Module tmpModule = new Module(modules.item(i));
                    modulesGroup.addModule(tmpModule);
                    this.getModules().add(tmpModule);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + modulesGroup.getModules().size() + " modules(s)");
        }

        NodeList groups = xut.getNodes("Group", modulesNode);
        if (groups.getLength() > 0) {
            log.info("Found " + groups.getLength() + " subgroup(s)");
            for (int i = 0; i < groups.getLength(); i++) {
                try {
                    ModulesGroup tmpModulesGroup = new ModulesGroup(xut.getNode("Name", groups.item(i)));
                    modulesGroup.addModulesGroup(tmpModulesGroup);
                    this.setModules(xut.getNode("Modules", groups.item(i)), tmpModulesGroup);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + modulesGroup.getGroups().size() + " subgroup(s)");
        }

        log.info("Modules group successfully parsed: \nName: " + modulesGroup.getName());
    }

    public ArrayList<Capacity> getCapacities() {
        return this.capacities;
    }

    private void setCapacities(Node capacitiesNode) {
        setCapacities(capacitiesNode, null);
    }

    private void setCapacities(Node capacitiesNode, CapacitiesGroup capacitiesGroup) {
        if (capacitiesGroup == null) {
            this.getCapacities().clear();
            capacitiesGroup = this.getCapacitiesRoot();
            capacitiesGroup.getCapacities().clear();
            capacitiesGroup.getGroups().clear();
        }

        log.info("Parsing capacities group");
        NodeList capacities = xut.getNodes("Capacity", capacitiesNode);
        if (capacities.getLength() > 0) {
            log.info("Found " + capacities.getLength() + " capacity(s)");
            for (int i = 0; i < capacities.getLength(); i++) {
                try {
                    Capacity tmpCapacity = new Capacity(capacities.item(i));
                    capacitiesGroup.addCapacity(tmpCapacity);
                    this.getCapacities().add(tmpCapacity);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(s)");
        }

        NodeList groups = xut.getNodes("Group", capacitiesNode);
        if (groups.getLength() > 0) {
            log.info("Found " + groups.getLength() + " subgroup(s)");
            for (int i = 0; i < groups.getLength(); i++) {
                try {
                    CapacitiesGroup tmpCapacitiesGroup = new CapacitiesGroup(xut.getNode("Name", groups.item(i)));
                    capacitiesGroup.addCapacitiesGroup(tmpCapacitiesGroup);
                    this.setCapacities(xut.getNode("Capacities", groups.item(i)), tmpCapacitiesGroup);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + capacitiesGroup.getGroups().size() + " subgroup(s)");
        }

        log.info("Capacities group successfully parsed: \nName: " + capacitiesGroup.getName());
    }

    public ModulesGroup getModulesRoot() {
        return this.modulesRoot;
    }

    public CapacitiesGroup getCapacitiesRoot() {
        return this.capacitiesRoot;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    private void setTotalWeight() {
        this.totalWeight = 0d;
        for (Module m : this.getModules()) {
            this.totalWeight += m.getWeight();
        }
    }
}
