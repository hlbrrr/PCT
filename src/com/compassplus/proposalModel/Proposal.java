package com.compassplus.proposalModel;

import com.compassplus.configurationModel.*;
import com.compassplus.configurationModel.OracleLicense;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:39
 */
public class Proposal {

    private Configuration config;
    private String name = "";
    private String date = "";
    private String clientName = "";
    private String userName;
    private String projectName = "";
    private Double currencyRate;
    private Map<String, Product> products = new LinkedHashMap<String, Product>();

    private Map<String, String> selectedAls = new HashMap<String, String>();
    private Map<String, String> alsTxt = new HashMap<String, String>();

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private com.compassplus.configurationModel.Region region;
    private com.compassplus.configurationModel.Currency currency;
    private SupportPlan supportPlan;
    private PSQuote psQuote;
    private OracleQuote oracleQuote;

    public Proposal(Configuration config) {
        this.setConfig(config);
        psQuote = new PSQuote(this);
        oracleQuote = new OracleQuote(this);
        userName = config.getUserName();
        for (AuthLevel l : config.getAuthLevels().values()) {
            this.alsTxt.put(l.getKey(), l.getMemoText());
        }
    }

    public Map<String, String> getSelectedAls() {
        return this.selectedAls;
    }

    public Map<String, String> getAlsTxt() {
        return this.alsTxt;
    }

    public boolean isAllAlsDefined() {
        return this.getSelectedAls().size() == this.getConfig().getAuthLevels().size();
    }

    public boolean isApproved() {
        boolean ret = false;
        try {
            for (String alkey : this.getSelectedAls().keySet()) {
                String selectedValue = this.getSelectedAls().get(alkey);
                Double selectedPriority = this.getConfig().getAuthLevels().get(alkey).getLevels().get(selectedValue).getPriority();
                String userValue = this.getConfig().getUserLevels().get(alkey).getSubKey();
                Double userPriority = this.getConfig().getAuthLevels().get(alkey).getLevels().get(userValue).getPriority();
                if (userPriority < selectedPriority) {
                    return false;
                }
            }
            ret = true;
        } catch (Exception e) {

        }

        return ret && isAllAlsDefined();
    }

    public boolean containsDeprecated() {
        for (Product p : getProducts().values()) {
            for (String key : p.getModules().keySet()) {
                if (p.getProduct().getModules().get(key).isDeprecated()) {
                    return true;
                }
            }
            for (String key : p.getCapacities().keySet()) {
                if (p.getProduct().getCapacities().get(key).isDeprecated()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void init(Document initialData) throws PCTDataFormatException {
        try {
            this.setName(xut.getNode("/root/Name", initialData));
            this.setClientName(xut.getNode("/root/ClientName", initialData));
            this.setProjectName(xut.getNode("/root/ProjectName", initialData));
            this.setCurrencyRate(xut.getNode("/root/CurrencyRate", initialData));
            this.setUserName(xut.getNode("/root/UserName", initialData));
            this.setDate(xut.getNode("/root/Date", initialData));
            this.setRegion(xut.getNode("/root/Region", initialData), config.getRegions());
            this.setCurrency(xut.getNode("/root/Currency", initialData), config.getCurrencies());
            this.setSupportPlan(xut.getNode("/root/SupportPlan", initialData), config.getSupportPlans());
            this.setSelectedAls(xut.getNodes("/root/Levels/Level", initialData));

            this.setProducts(xut.getNodes("/root/Products/Product", initialData));
            this.setPSQuote(initialData);
            this.setOracleQuote(initialData);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Bad proposal", e.getDetails());
        }
    }

    public void setSupportPlan(com.compassplus.configurationModel.SupportPlan supportPlan) {
        this.supportPlan = supportPlan;
    }

    public SupportPlan getSupportPlan() {
        return this.supportPlan;
    }

    public Double getSupportRate() {
        SupportPlan plan = getSupportPlan();
        Double rate = 0d;
        if (plan != null) {
            rate = plan.getRate();
            if (plan.getRegionSettings().containsKey(getRegion().getKey())) {
                rate = plan.getRegionSettings().get(getRegion().getKey());
            }
        }
        return rate;
    }

    private void setSupportPlan(Node supportPlan, Map<String, com.compassplus.configurationModel.SupportPlan> allowedSupportPlans) throws PCTDataFormatException {
        try {
            String supportPlanString = xut.getString(supportPlan);
            this.setSupportPlan(allowedSupportPlans.get(supportPlanString));
            if (this.getSupportPlan() == null) {
                throw new PCTDataFormatException("No such support plan \"" + supportPlanString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product support plan is not defined correctly", e.getDetails());
        }
    }

    private void setSelectedAls(NodeList levels) throws PCTDataFormatException {
        this.getSelectedAls().clear();
        if (levels.getLength() > 0) {
            for (int i = 0; i < levels.getLength(); i++) {
                try {
                    String akey = xut.getString(xut.getNode("Key", levels.item(i)));
                    String asubkey = xut.getString(xut.getNode("SubKey", levels.item(i)));
                    String text = xut.getString(xut.getNode("Text", levels.item(i)), true);


                    if (getConfig().getAuthLevels().containsKey(akey) && getConfig().getAuthLevels().get(akey).getLevels().containsKey(asubkey)) {
                        this.getSelectedAls().put(akey, asubkey);
                        this.getAlsTxt().put(akey, text);
                    }
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
        }
    }


    public void setCurrency(com.compassplus.configurationModel.Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    private void setCurrency(Node currency, Map<String, com.compassplus.configurationModel.Currency> allowedCurrencies) throws PCTDataFormatException {
        try {
            String currencyString = xut.getString(currency);
            this.setCurrency(allowedCurrencies.get(currencyString));
            if (this.getCurrency() == null) {
                throw new PCTDataFormatException("No such currency \"" + currencyString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product currency is not defined correctly", e.getDetails());
        }
    }

    public void setRegion(com.compassplus.configurationModel.Region region) {
        this.region = region;
    }

    public com.compassplus.configurationModel.Region getRegion() {
        return this.region;
    }

    private void setRegion(Node region, Map<String, com.compassplus.configurationModel.Region> allowedRegions) throws PCTDataFormatException {
        try {
            String regionString = xut.getString(region);
            this.setRegion(allowedRegions.get(regionString));
            if (this.getRegion() == null) {
                throw new PCTDataFormatException("No such region \"" + regionString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product region is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal name is not defined correctly", e.getDetails());
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    private void setClientName(Node clientName) throws PCTDataFormatException {
        try {
            this.clientName = xut.getString(clientName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal client name is not defined correctly", e.getDetails());
        }
    }

    public String getUserName() {
        return this.userName;
    }

    private void setUserName(Node userName) throws PCTDataFormatException {
        try {
            this.userName = xut.getString(userName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal user name is not defined correctly", e.getDetails());
        }
    }

    public String getDate() {
        if (this.date != "") {
            return this.date;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            return dateFormat.format(date);
        }
    }

    private void setDate(Node date) throws PCTDataFormatException {
        try {
            this.date = xut.getString(date, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal date is not defined correctly", e.getDetails());
        }
    }

    public String getProjectName() {
        return this.projectName;
    }

    private void setProjectName(Node projectName) throws PCTDataFormatException {
        try {
            this.projectName = xut.getString(projectName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal project name is not defined correctly", e.getDetails());
        }
    }

    public Double getCurrencyRate() {
        return this.currencyRate != null ? this.currencyRate : 0d;
    }

    public void setCurrencyRate(Double currencyRate) {
        this.currencyRate = currencyRate;
    }

    private void setCurrencyRate(Node currencyRate) throws PCTDataFormatException {
        try {
            this.currencyRate = xut.getDouble(currencyRate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Proposal currency rate is not defined correctly", e.getDetails());
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
                    Product tmpProduct = new Product(products.item(i), this.getConfig().getProducts(), this);
                    this.getProducts().put(tmpProduct.getName(), tmpProduct);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getProducts().size() + " product(s)");
        }
    }

    private void setPSQuote(Document initialData) throws PCTDataFormatException {
        if (xut.getNode("/root/PSQuotePresent", initialData) != null) {
            NodeList services = xut.getNodes("/root/Services/Service", initialData);
            NodeList states = xut.getNodes("/root/SavedState/DoNotExport", initialData);
            NodeList oracleLicenses = xut.getNodes("/root/OracleLicenses/OracleLicense", initialData);

            double MDDiscount = 0d;
            double PSDiscount = 0d;
            try{
                MDDiscount = xut.getDouble(xut.getNode("/root/MDDiscount", initialData));
            }catch(Exception e){

            }
            try{
                PSDiscount = xut.getDouble(xut.getNode("/root/PSDiscount", initialData));
            }catch(Exception e){

            }

            this.psQuote = new PSQuote(services, oracleLicenses,  states, this, MDDiscount, PSDiscount);
            this.getPSQuote().setEnabled(true);
        }
    }

    private void setOracleQuote(Document initialData) throws PCTDataFormatException {
        if (xut.getNode("/root/OracleQuotePresent", initialData) != null) {
            NodeList states = xut.getNodes("/root/OracleSavedState/DoNotExport", initialData);
            NodeList oracleLicenses = xut.getNodes("/root/OracleLicenses/OracleLicense", initialData);

            double oracleDiscount = 0d;
            try{
                oracleDiscount = xut.getDouble(xut.getNode("/root/OracleDiscount", initialData));
            }catch(Exception e){

            }

            this.oracleQuote = new OracleQuote(oracleLicenses,  states, this, oracleDiscount);
            this.getOracleQuote().setEnabled(true);
        }
    }

    public void addProduct(Product product) {
        this.getProducts().put(product.getName(), product);

        com.compassplus.configurationModel.Product cProduct = getConfig().getProducts().get(product.getName());

        if (cProduct.getTrainingCourses().size() > 0) {
            for (String rKey : cProduct.getTrainingCourses()) {
                com.compassplus.configurationModel.TrainingCourse r = getConfig().getTrainingCourses().get(rKey);
                if (r != null) {
                    //recommendations.add(r.getKey());
                    com.compassplus.proposalModel.TrainingCourse tc = null;
                    if(getPSQuote().getTrainingCourses().containsKey(rKey)){
                        tc = getPSQuote().getTrainingCourses().get(rKey);
                    }else{
                        try{
                            tc = new com.compassplus.proposalModel.TrainingCourse(r, this);
                        }catch(Exception e){}
                        getPSQuote().addTrainingCourse(tc);
                    }
                    tc.addSubscription(product.getName());
                }
            }
        }else{
        }

        if (cProduct.getOracleLicenses().size() > 0) {
            for (String rKey : cProduct.getOracleLicenses()) {
                OracleLicense r = getConfig().getOracleLicenses().get(rKey);
                if (r != null) {
                    //recommendations.add(r.getKey());
                    com.compassplus.proposalModel.OracleLicense tc = null;
                    if(getOracleQuote().getOracleLicenses().containsKey(cProduct.getName())){
                        tc = getOracleQuote().getOracleLicenses().get(cProduct.getName());
                    }else{
                        try{
                            tc = new com.compassplus.proposalModel.OracleLicense(r, this, cProduct);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        getOracleQuote().addOracleLicense(tc);
                    }
                }
            }
        }else{
        }
    }

    public void delProduct(Product product) {
        for(String key:product.getRecommendations()){
            getPSQuote().delService(key);
        }
        for(String key:getConfig().getTrainingCourses().keySet()){
            for(String mKey:product.getModules().keySet()){
                getPSQuote().delTrainingCourse(key, mKey);
            }
            getPSQuote().delTrainingCourse(key, product.getName());
        }

        getOracleQuote().delOracleLicense(product.getName());
        this.getProducts().remove(product.getName());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClientName(String clientName) {

        this.clientName = clientName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<ClientName>").append(this.getClientName()).append("</ClientName>");
        sb.append("<ProjectName>").append(this.getProjectName()).append("</ProjectName>");
        sb.append("<UserName>").append(this.getConfig().getUserName()).append("</UserName>");
        sb.append("<Date>").append(this.getDate()).append("</Date>");
        sb.append("<CurrencyRate>").append(this.getCurrencyRate()).append("</CurrencyRate>");
        sb.append("<Region>").append(this.getRegion().getKey()).append("</Region>");
        sb.append("<Currency>").append(this.getCurrency().getName()).append("</Currency>");
        sb.append("<SupportPlan>").append(this.getSupportPlan().getKey()).append("</SupportPlan>");

        if (this.getSelectedAls() != null && this.getSelectedAls().size() > 0) {
            sb.append("<Levels>");
            for (String key : this.getSelectedAls().keySet()) {
                sb.append("<Level>");
                sb.append("<Key>");
                sb.append(key);
                sb.append("</Key>");
                sb.append("<SubKey>");
                sb.append(this.getSelectedAls().get(key));
                sb.append("</SubKey>");
                sb.append("<Text>");
                sb.append(this.getAlsTxt().get(key));
                sb.append("</Text>");
                sb.append("</Level>");
            }
            sb.append("</Levels>");
        }

        if (this.getProducts() != null && this.getProducts().size() > 0) {
            sb.append("<Products>");
            for (Product p : this.getProducts().values()) {
                sb.append(p.toString());
            }
            sb.append("</Products>");
        }
        if (this.getPSQuote().enabled()) {
            sb.append(this.getPSQuote().toString());
        }
        if (this.getOracleQuote().enabled()) {
            sb.append(this.getOracleQuote().toString());
        }
        sb.append("</root>");
        return sb.toString();
    }

    public Configuration getConfig() {
        return config;
    }

    private void setConfig(Configuration config) {
        this.config = config;
    }


    public Double getPrice() {
        Double ret = 0d;
        for (Product p : this.products.values()) {
            ret += p.getPrice();
        }
        return ret;
    }

    public Double getRegionalPrice() {
        Double ret = 0d;
        for (Product p : this.products.values()) {
            ret += p.getRegionPrice(true);
        }
        return ret;
    }

    public boolean isPrimarySale() {
        boolean primary = true;
        for (Product p : this.products.values()) {
            primary &= !p.getSecondarySale();
        }
        return primary;
    }

    public PSQuote getPSQuote() {
        return this.psQuote;
    }

    public OracleQuote getOracleQuote() {
        return this.oracleQuote;
    }


    public void createPSQuote() {
        this.getPSQuote().setEnabled(true);
    }

    public void delPSQuote() {
        this.getPSQuote().setEnabled(false);
    }
}
