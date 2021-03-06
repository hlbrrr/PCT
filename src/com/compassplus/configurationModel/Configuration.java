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
    private Map<String, Recommendation> recommendations = new LinkedHashMap<String, Recommendation>();
    private Map<String, TrainingCourse> trainingCourses = new LinkedHashMap<String, TrainingCourse>();

    private ServicesGroup servicesRoot = new ServicesGroup("Modules", "");
    private Map<String, Service> services = new LinkedHashMap<String, Service>();
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
    private Double mdrDiscount = 0d;
    private Double psDiscount = 0d;
    private String expDateString;
    private Integer minBuild;
    private Integer build;
    private Boolean salesSupport;
    private Map<String,OracleLicense> oracleLicenses = new HashMap<String, OracleLicense>();
    private Map<String, OracleOption> oracleOptions = new HashMap<String, OracleOption>();
    private Double oracleDiscount = 0d;

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

    public Double getMDRDiscount() {
        return mdrDiscount;
    }

    public Double getPSDiscount() {
        return psDiscount;
    }

    public Double getOracleDiscount() {
        return oracleDiscount;
    }

    private Configuration() {
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.checkMinBuild(xut.getNode("/root/MinBuild", initialData));
            this.checkExpiration(xut.getNode("/root/Expiration", initialData));
            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
            this.setRegions(xut.getNodes("/root/Regions/Region", initialData));
            try {
                this.setRecommendations(xut.getNodes("/root/Recommendations/Recommendation", initialData));
                this.setServices(xut.getNode("/root/Services", initialData));
            } catch (Exception e) {
            }
            this.setTrainingCourses(xut.getNodes("/root/TrainingCourses/TrainingCourse", initialData));
            this.setCurrencies(xut.getNodes("/root/Currencies/Currency", initialData));
            this.setSupportPlans(xut.getNodes("/root/SupportPlans/SupportPlan", initialData));
            this.setAuthLevels(xut.getNodes("/root/AuthLevels/AuthLevel", initialData));
            this.setOracleLicenses(xut.getNodes("/root/Oracle/Oracle[Type=\"License\"]", initialData));
            this.setOracleOptions(xut.getNodes("/root/Oracle/Oracle[Type=\"Option\"]", initialData));

            this.setUserLevels(xut.getNodes("/root/Users/User/Levels/Level", initialData));

            this.setUserName(xut.getNode("/root/Users/User/Name", initialData));
            this.setMaxDiscount(xut.getNode("/root/Users/User/MaxProductDiscount", initialData));
            this.setMaxSupportDiscount(xut.getNode("/root/Users/User/MaxSupportDiscount", initialData));
            this.setMDRDiscount(xut.getNode("/root/Users/User/MDRDiscount", initialData));
            this.setPSDiscount(xut.getNode("/root/Users/User/PSDiscount", initialData));
            this.setOracleDiscount(xut.getNode("/root/Users/User/OracleDiscount", initialData));
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

    private void setMDRDiscount(Node discount) throws PCTDataFormatException {
        try {
            this.mdrDiscount = xut.getDouble(discount);
            this.mdrDiscount = this.mdrDiscount / 100d;
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User MDR discount is not defined correctly", e.getDetails());
        }
    }

    private void setPSDiscount(Node discount) throws PCTDataFormatException {
        try {
            this.psDiscount = xut.getDouble(discount);
            this.psDiscount = this.psDiscount / 100d;
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User PS discount is not defined correctly", e.getDetails());
        }
    }

    private void setOracleDiscount(Node discount) throws PCTDataFormatException {
        try {
            this.oracleDiscount = xut.getDouble(discount);
            this.oracleDiscount = this.oracleDiscount / 100d;
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("User Oracle discount is not defined correctly", e.getDetails());
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
            log.info("Successfully parsed " + this.getUserLevels().size() + " user level(s)");
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
            log.info("Successfully parsed " + this.getAuthLevels().size() + " authority level(s)");
        } /*else {
            throw new PCTDataFormatException("No authority levels defined");
        }
        if (this.getAuthLevels().size() == 0) {
            throw new PCTDataFormatException("Authority levels are not defined correctly");
        }*/
    }

    private void setOracleLicenses(NodeList oracleLicenses) throws PCTDataFormatException {
        this.getOracleLicenses().clear();
        if (oracleLicenses.getLength() > 0) {
            log.info("Found " + oracleLicenses.getLength() + " oracle license(s)");
            for (int i = 0; i < oracleLicenses.getLength(); i++) {
                try {
                    OracleLicense oracleLicense = new OracleLicense(oracleLicenses.item(i));
                    this.getOracleLicenses().put(oracleLicense.getKey(), oracleLicense);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getAuthLevels().size() + " oracle license(s)");
        }
    }

    private void setOracleOptions(NodeList oracleOptions) throws PCTDataFormatException {
        this.getOracleOptions().clear();
        if (oracleOptions.getLength() > 0) {
            log.info("Found " + oracleOptions.getLength() + " oracle option(s)");
            for (int i = 0; i < oracleOptions.getLength(); i++) {
                try {
                    OracleOption oracleOption = new OracleOption(oracleOptions.item(i));
                    this.getOracleOptions().put(oracleOption.getKey(), oracleOption);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getAuthLevels().size() + " oracle option(s)");
        }
    }

    public Map<String, OracleOption> getOracleOptions() {
        return this.oracleOptions;
    }

    public Map<String, OracleLicense> getOracleLicenses() {
        return this.oracleLicenses;
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

    public Map<String, Recommendation> getRecommendations() {
        return this.recommendations;
    }

    private void setRecommendations(NodeList recommendations) throws PCTDataFormatException {
        this.getRecommendations().clear();
        if (recommendations.getLength() > 0) {
            log.info("Found " + recommendations.getLength() + " recommendation(s)");
            for (int i = 0; i < recommendations.getLength(); i++) {
                try {
                    Recommendation tmpRecommendation = new Recommendation(recommendations.item(i));
                    this.getRecommendations().put(tmpRecommendation.getKey(), tmpRecommendation);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getRecommendations().size() + " recommendations(s)");
        }
    }

    public Map<String, TrainingCourse> getTrainingCourses() {
        return this.trainingCourses;
    }

    private void setTrainingCourses(NodeList trainingCourses) throws PCTDataFormatException {
        this.getTrainingCourses().clear();
        if (trainingCourses.getLength() > 0) {
            log.info("Found " + trainingCourses.getLength() + " training course(s)");
            for (int i = 0; i < trainingCourses.getLength(); i++) {
                try {
                    TrainingCourse tmpTrainingCourse = new TrainingCourse(trainingCourses.item(i));
                    this.getTrainingCourses().put(tmpTrainingCourse.getKey(), tmpTrainingCourse);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getTrainingCourses().size() + " training course(s)");
        }
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public ServicesGroup getServicesRoot() {
        return this.servicesRoot;
    }

    private void setServices(Node servicesNode) {
        setServices(servicesNode, null);
    }

    private void setServices(Node servicesNode, ServicesGroup servicesGroup) {
        if (servicesGroup == null) {
            this.getServices().clear();
            servicesGroup = this.getServicesRoot();
            servicesGroup.getServices().clear();
            servicesGroup.getGroups().clear();
        }

        log.info("Parsing services group");
        NodeList services = xut.getNodes("Service", servicesNode);
        if (services.getLength() > 0) {
            log.info("Found " + services.getLength() + " service(s)");
            for (int i = 0; i < services.getLength(); i++) {
                try {
                    Service tmpService = new Service(services.item(i), servicesGroup.getKey());
                    servicesGroup.addService(tmpService.getKey(), tmpService);
                    this.getServices().put(tmpService.getKey(), tmpService);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + servicesGroup.getServices().size() + " service(s)");
        }

        NodeList groups = xut.getNodes("Group", servicesNode);
        if (groups.getLength() > 0) {
            log.info("Found " + groups.getLength() + " subgroup(s)");
            for (int i = 0; i < groups.getLength(); i++) {
                try {
                    ServicesGroup tmpServicesGroup = new ServicesGroup(xut.getNode("Key", groups.item(i)), xut.getNode("Name", groups.item(i)), xut.getNode("Hint", groups.item(i)), xut.getNode("Hidden", groups.item(i)));
                    servicesGroup.addServicesGroup(tmpServicesGroup);
                    this.setServices(xut.getNode("Services", groups.item(i)), tmpServicesGroup);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + servicesGroup.getGroups().size() + " subgroup(s)");
        }

        log.info("Services group successfully parsed: \nName: " + servicesGroup.getName());
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
