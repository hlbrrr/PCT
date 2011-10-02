package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.NodeUtils;
import org.w3c.dom.Element;
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
    private NodeUtils nut = NodeUtils.getInstance();

    public Product(Element initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Element initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(initialData.getElementsByTagName("Name").item(0));
            this.setMaximumFunctionalityPrice(initialData.getElementsByTagName("MaximumFunctionalityPrice").item(0));
            this.setMinimumPrice(initialData.getElementsByTagName("MinimumPrice").item(0));

            NodeList modulesNodes = initialData.getElementsByTagName("Modules");
            if (modulesNodes.getLength() == 1) {
                Element modulesElement = (Element) modulesNodes.item(0);
                NodeList moduleNodes = modulesElement.getElementsByTagName("Module");
                if (moduleNodes.getLength() > 0) {
                    log.info("Found " + moduleNodes.getLength() + " modules(s)");
                    for (int i = 0; i < moduleNodes.getLength(); i++) {
                        Element moduleElement = (Element) moduleNodes.item(i);
                        try {
                            modules.add(new Module(moduleElement));
                        } catch (PCTDataFormatException e) {
                            log.error(e);
                        }
                    }
                    log.info("Successfully parsed " + modules.size() + " modules(s)");
                } else {
                    throw new PCTDataFormatException("No product modules defined");
                }
            } else {
                throw new PCTDataFormatException("Product modules nodes length is not equals 1");
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
            this.name = nut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public Double getMaximumFunctionalityPrice() {
        return maximumFunctionalityPrice;
    }

    private void setMaximumFunctionalityPrice(Node maximumFunctionalityPrice) throws PCTDataFormatException {
        try {
            this.maximumFunctionalityPrice = nut.getDouble(maximumFunctionalityPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product maximum functionality price is not defined correctly", e.getDetails());
        }
    }

    public Double getMinimumPrice() {
        return minimumPrice;
    }

    private void setMinimumPrice(Node minimumPrice) throws PCTDataFormatException {
        try {
            this.minimumPrice = nut.getDouble(minimumPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product minimum price is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
