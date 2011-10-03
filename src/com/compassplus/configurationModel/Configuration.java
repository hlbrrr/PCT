package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:50 AM
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();
    private ArrayList<Product> products = new ArrayList<Product>();
    private ArrayList<SupportRate> supportRates = new ArrayList<SupportRate>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private Configuration() {
    }

    public void init(Document initialData) throws PCTDataFormatException {
        this.products.clear();
        try {
            NodeList products = xut.getNodes("/root/Products/Product", initialData);
            if (products.getLength() > 0) {
                log.info("Found " + products.getLength() + " product(s)");
                for (int i = 0; i < products.getLength(); i++) {
                    try {
                        this.products.add(new Product(products.item(i)));
                    } catch (PCTDataFormatException e) {
                        log.error(e);
                    }
                }
                log.info("Successfully parsed " + this.products.size() + " product(s)");
            } else {
                throw new PCTDataFormatException("No products defined");
            }
            if (this.products.size() == 0) {
                throw new PCTDataFormatException("Products are not defined correctly");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad configuration", e.getDetails());
        }
    }
}
