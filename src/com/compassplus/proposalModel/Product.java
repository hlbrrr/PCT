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
    private String name;
    private ArrayList<Module> modules = new ArrayList<Module>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        init(initialData, allowedProducts);
    }

    private void init(Node initialData, ArrayList<com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        modules.clear();
        try {
            this.setName(xut.getNode("Name", initialData));

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

    public ArrayList<Module> getModules() {
        return modules;
    }
}
