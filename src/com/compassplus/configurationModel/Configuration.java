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
    private String expirationFormat = "dd/MM/yyyy";
    private Map<String, Currency> currencies = new LinkedHashMap<String, Currency>();
    private Map<String, Region> regions = new LinkedHashMap<String, Region>();
    private Map<String, SupportPlan> supportPlans = new LinkedHashMap<String, SupportPlan>();
    private Map<String, Product> products = new LinkedHashMap<String, Product>();
    private ArrayList<SupportRate> supportRates = new ArrayList<SupportRate>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    private String userName = "test";
    private Double maxDiscount = 0.25d;
    private Double maxSupportDiscount = 0.30d;

    public static Configuration getInstance() {
        return ourInstance;
    }

    public Double getMaxDiscount() {
        return maxDiscount;
    }
    public Double getMaxSupportDiscount() {
        return maxSupportDiscount;
    }

    private Configuration() {
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.checkExpiration(xut.getNode("/root/Expiration", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
            this.setRegions(xut.getNodes("/root/Regions/Region", initialData));
            this.setCurrencies(xut.getNodes("/root/Currencies/Currency", initialData));
            this.setSupportPlans(xut.getNodes("/root/SupportPlans/SupportPlan", initialData));
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad configuration", e.getDetails());
        }
    }

    public String getUserName() {
        return userName;
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

    public Map<String, SupportPlan> getSupportPlans() {
        return this.supportPlans;
    }

    private void setSupportPlans(NodeList supportPlans) throws PCTDataFormatException {
        this.getSupportPlans().clear();
        if (supportPlans.getLength() > 0) {
            log.info("Found " + supportPlans.getLength() + " support plan(s)");
            for (int i = 0; i < supportPlans.getLength(); i++) {
                try {
                    SupportPlan tmpSupportPlan = new SupportPlan(supportPlans.item(i));
                    this.getSupportPlans().put(tmpSupportPlan.getKey(), tmpSupportPlan);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getSupportPlans().size() + " support plan(s)");
        } else {
            throw new PCTDataFormatException("No support plans defined");
        }
        if (this.getSupportPlans().size() == 0) {
            throw new PCTDataFormatException("Support plans are not defined correctly");
        }
    }

    public Map<String, Currency> getCurrencies() {
        return this.currencies;
    }

    private void setCurrencies(NodeList currencies) throws PCTDataFormatException {
        this.getCurrencies().clear();
        if (currencies.getLength() > 0) {
            log.info("Found " + currencies.getLength() + " currency(ies)");
            for (int i = 0; i < currencies.getLength(); i++) {
                try {
                    Currency tmpCurrency = new Currency(currencies.item(i));
                    this.getCurrencies().put(tmpCurrency.getName(), tmpCurrency);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCurrencies().size() + " currency(ies)");
        } else {
            throw new PCTDataFormatException("No currencies defined");
        }
        if (this.getCurrencies().size() == 0) {
            throw new PCTDataFormatException("Currencies are not defined correctly");
        }
    }

    public Map<String, Region> getRegions() {
        return this.regions;
    }

    private void setRegions(NodeList regions) throws PCTDataFormatException {
        this.getRegions().clear();
        if (regions.getLength() > 0) {
            log.info("Found " + regions.getLength() + " region(s)");
            for (int i = 0; i < regions.getLength(); i++) {
                try {
                    Region tmpRegion = new Region(regions.item(i));
                    this.getRegions().put(tmpRegion.getKey(), tmpRegion);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRegions().size() + " region(s)");
        } else {
            throw new PCTDataFormatException("No regions defined");
        }
        if (this.getRegions().size() == 0) {
            throw new PCTDataFormatException("Regions are not defined correctly");
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
