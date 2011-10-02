package com.compassplus.pctModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:50 AM
 */
public class PCTManager {
    private static PCTManager ourInstance = new PCTManager();
    private ArrayList<Product> products = new ArrayList<Product>();
    private ArrayList<SupportRate> supportRates = new ArrayList<SupportRate>();
    private Logger log = Logger.getInstance();

    public static PCTManager getInstance() {
        return ourInstance;
    }

    private PCTManager() {
    }

    public void init(Document initialData) throws PCTDataFormatException {
        NodeList productsNodes = initialData.getElementsByTagName("Products");
        if (productsNodes.getLength() == 1) {
            Element productsElement = (Element) productsNodes.item(0);
            NodeList productNodes = productsElement.getElementsByTagName("Product");
            if (productNodes.getLength() > 0) {
                log.info("Found " + productNodes.getLength() + " product(s)");
                for (int i = 0; i < productNodes.getLength(); i++) {
                    Element productElement = (Element) productNodes.item(i);
                    try {
                        this.products.add(new Product(productElement));
                    } catch (PCTDataFormatException e) {
                        log.error(e);
                    }
                }
                log.info("Successfully parsed " + products.size() + " product(s)");
            } else {
                throw new PCTDataFormatException("No products defined");
            }
        } else {
            throw new PCTDataFormatException("Products nodes length is not equals 1");
        }

        if (this.products.size() == 0) {
            throw new PCTDataFormatException("Products are not defined correctly");
        }
    }
}
