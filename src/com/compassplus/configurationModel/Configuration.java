package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private Map<String, AuthLevel> authLevels = new LinkedHashMap<String, AuthLevel>();

    private Map<String, UserLevel> userLevels = new HashMap<String, UserLevel>();

    private Map<String, Product> products = new LinkedHashMap<String, Product>();
    private ArrayList<SupportRate> supportRates = new ArrayList<SupportRate>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    private String userName = "";
    private Double maxDiscount = 0d;
    private Double maxSupportDiscount = 0d;
    private String expDateString;
    private Integer minBuild;
    private Integer build;
    private Boolean salesSupport;

    public boolean isSalesSupport() {
        //return true;
        return salesSupport != null ? salesSupport : false;
    }

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
            this.checkMinBuild(xut.getNode("/root/MinBuild", initialData));
            this.checkExpiration(xut.getNode("/root/Expiration", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
            this.setRegions(xut.getNodes("/root/Regions/Region", initialData));
            this.setCurrencies(xut.getNodes("/root/Currencies/Currency", initialData));
            this.setSupportPlans(xut.getNodes("/root/SupportPlans/SupportPlan", initialData));
            this.setAuthLevels(xut.getNodes("/root/AuthLevels/AuthLevel", initialData));

            this.setUserLevels(xut.getNodes("/root/Users/User/Levels/Level", initialData));

            this.setUserName(xut.getNode("/root/Users/User/Name", initialData));
            this.setMaxDiscount(xut.getNode("/root/Users/User/MaxProductDiscount", initialData));
            this.setMaxSupportDiscount(xut.getNode("/root/Users/User/MaxSupportDiscount", initialData));
            this.setSalesSupport(xut.getNode("/root/Users/User/SalesSupport", initialData));

        } catch (PCTDataFormatException e) {
            if ("Expired".equals(e.getCleanMessage()) || "BadBuild".equals(e.getCleanMessage())) {
                throw e;
            }
            throw new PCTDataFormatException("Bad configuration", e.getDetails());
        }
    }

    private void setMaxDiscount(Node maxDiscount) throws PCTDataFormatException {
        try {
            this.maxDiscount = xut.getDouble(maxDiscount);
            this.maxDiscount = this.maxDiscount / 100d;
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User max product discount is not defined correctly", e.getDetails());
        }
    }

    private void setMaxSupportDiscount(Node maxSupportDiscount) throws PCTDataFormatException {
        try {
            this.maxSupportDiscount = xut.getDouble(maxSupportDiscount);
            this.maxSupportDiscount = this.maxSupportDiscount / 100d;
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User max support discount is not defined correctly", e.getDetails());
        }
    }

    private void setUserName(Node userName) throws PCTDataFormatException {
        try {
            this.userName = xut.getString(userName);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User name is not defined correctly", e.getDetails());
        }
    }

    private void setSalesSupport(Node salesSupport) throws PCTDataFormatException {
        try {
            this.salesSupport = xut.getBoolean(salesSupport, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("SalesSupport flag is not defined correctly", e.getDetails());
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

    public Map<String, AuthLevel> getAuthLevels() {
        return this.authLevels;
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

    public Map<String, UserLevel> getUserLevels() {
        return this.userLevels;
    }

    private void setUserLevels(NodeList levels) throws PCTDataFormatException {
        this.getUserLevels().clear();
        if (levels.getLength() > 0) {
            log.info("Found " + levels.getLength() + " user level(s)");
            for (int i = 0; i < levels.getLength(); i++) {
                try {
                    UserLevel tmpUserLevel = new UserLevel(levels.item(i));
                    if (this.getAuthLevels().containsKey(tmpUserLevel.getKey())
                            && this.getAuthLevels().get(tmpUserLevel.getKey()).getLevels().containsKey(tmpUserLevel.getSubKey())) {
                        this.getUserLevels().put(tmpUserLevel.getKey(), tmpUserLevel);
                    } else {
                        throw new PCTDataFormatException("No such auth level: \nKey: " + tmpUserLevel.getKey() + "\nSubKey: " + tmpUserLevel.getSubKey());
                    }
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getUserLevels().size() + " user levels(s)");
        }
    }

    private void setAuthLevels(NodeList authLevels) throws PCTDataFormatException {
        this.getAuthLevels().clear();
        if (authLevels.getLength() > 0) {
            log.info("Found " + authLevels.getLength() + " authority level(s)");
            for (int i = 0; i < authLevels.getLength(); i++) {
                try {
                    AuthLevel tmpAuthLevel = new AuthLevel(authLevels.item(i));
                    this.getAuthLevels().put(tmpAuthLevel.getKey(), tmpAuthLevel);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getAuthLevels().size() + " authority levels(s)");
        } /*else {
            throw new PCTDataFormatException("No authority levels defined");
        }
        if (this.getAuthLevels().size() == 0) {
            throw new PCTDataFormatException("Authority levels are not defined correctly");
        }*/
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

    public Integer getBuild() {
        if (this.build == null) {
            try {
                InputStream is = this.getClass().getResourceAsStream("build.properties");
                BufferedReader d = new BufferedReader(new InputStreamReader(is));
                String buildNumberString = d.readLine();
                build = Integer.parseInt(buildNumberString);
                d.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.build;
    }

    public Integer getMinBuild() {
        return this.minBuild;
    }

    private void checkMinBuild(Node minBuild) throws PCTDataFormatException {
        try {
            Integer minBuildInt = xut.getInteger(minBuild, true);
            if (minBuildInt != null) {
                if (minBuildInt > this.getBuild()) {
                    throw new PCTDataFormatException("BadBuild");
                }
            }
            this.minBuild = minBuildInt;
        } catch (PCTDataFormatException e) {
            if ("BadBuild".equals(e.getCleanMessage())) {
                throw e;
            }
            throw new PCTDataFormatException("Minimum build number is not defined correctly", e.getDetails());
        }

    }

    private void checkExpiration(Node expiration) throws PCTDataFormatException {
        try {
            String dateString = xut.getString(expiration);

            if (dateString.startsWith("Expiration=")) {
                dateString = dateString.substring("Expiration=".length());
            }
            SimpleDateFormat sdf = new SimpleDateFormat(this.expirationFormat);
            Date date;
            try {
                date = sdf.parse(dateString);
            } catch (Exception e) {
                throw new PCTDataFormatException("Expiration format is not " + this.expirationFormat);
            }
            Date currentDate = new Date();

            if (!currentDate.before(date)) {
                throw new PCTDataFormatException("Expired");
            }
            this.expDateString = dateString;
        } catch (PCTDataFormatException e) {
            if ("Expired".equals(e.getCleanMessage())) {
                throw e;
            }
            throw new PCTDataFormatException("Expiration is not defined correctly", e.getDetails());
        }

    }

    public String getExpirationDateString() {
        return this.expDateString;
    }

}
