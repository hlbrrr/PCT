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
    private Double minimumPrice;
    private ArrayList<Module> modules = new ArrayList<Module>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        modules.clear();
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData));
            this.setMaximumFunctionalityPrice(xut.getNode("MaximumFunctionalityPrice", initialData));
            this.setMinimumPrice(xut.getNode("MinimumPrice", initialData));

            NodeList modules = xut.getNodes("Modules/Module", initialData);
            if (modules.getLength() > 0) {
                log.info("Found " + modules.getLength() + " modules(s)");
                for (int i = 0; i < modules.getLength(); i++) {
                    try {
                        this.modules.add(new Module(modules.item(i)));
                    } catch (PCTDataFormatException e) {
                        log.error(e);
                    }
                }
                log.info("Successfully parsed " + this.modules.size() + " modules(s)");
            } else {
                throw new PCTDataFormatException("No product modules defined");
            }

            if (this.modules.size() == 0) {
                throw new PCTDataFormatException("Product modules are not defined correctly");
            }

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nMaximumFunctionalityPrice: " + this.getMaximumFunctionalityPrice() +
                    "\nMinimumPrice: " + this.getMinimumPrice());
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
}
