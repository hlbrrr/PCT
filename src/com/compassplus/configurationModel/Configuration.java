package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:50 AM
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();
    private String expirationFormat = "dd/MM/yyyy HH:mm:ss";
    private Map<String, Product> products = new LinkedHashMap<String, Product>();
    private ArrayList<SupportRate> supportRates = new ArrayList<SupportRate>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private Configuration() {
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.checkExpiration(xut.getNode("/root/Expiration", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad configuration", e.getDetails());
        }
    }

    public Map<String, Product> getProducts() {
        return this.products;
    }

    private void setProducts(NodeList products) throws PCTDataFormatException {
        this.getProducts().clear();
        if (products.getLength() > 0) {
            log.info("Found " + products.getLength() + " product(s)");
            for (int i = 0; i < products.getLength(); i++) {
                try {
                    Product tmpProduct = new Product(products.item(i));
                    this.getProducts().put(tmpProduct.getName(), tmpProduct);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getProducts().size() + " product(s)");
        } else {
            throw new PCTDataFormatException("No products defined");
        }
        if (this.getProducts().size() == 0) {
            throw new PCTDataFormatException("Products are not defined correctly");
        }
    }

    private void checkExpiration(Node expiration) throws PCTDataFormatException {
        try {
            String dateString = xut.getString(expiration);
            SimpleDateFormat sdf = new SimpleDateFormat(this.expirationFormat);
            Date date;
            try {
                date = sdf.parse(dateString);
            } catch (Exception e) {
                throw new PCTDataFormatException("Expiration format is not " + this.expirationFormat);
            }
            Date currentDate = new Date();

            if (!currentDate.before(date)) {
                throw new PCTDataFormatException("Already expired");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Expiration is not defined correctly", e.getDetails());
        }

    }
}
