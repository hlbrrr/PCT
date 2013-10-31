package com.compassplus.proposalModel;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/12/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class OracleLicense {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    private com.compassplus.configurationModel.OracleLicense license = null;

    private com.compassplus.configurationModel.Product product = null;

    private List<String> children = new ArrayList<String>();

    private Integer cores = 2;
    private Coefficient coefficient = null;
    private String licensingModel = "ASFU";
    private Double discount = 0d;

    private Proposal proposal = null;


    public OracleLicense(Node initialData, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        try {
            log.info("Parsing oracle license");

            this.setOracleLicense(xut.getNode("Key", initialData), proposal.getConfig());
            this.setProduct(xut.getNode("ProductKey", initialData), proposal.getConfig());

            this.setCores(xut.getNode("Cores", initialData));
            this.setCoefficient(xut.getNode("Coefficient", initialData));
            this.setDiscount(xut.getNode("Discount", initialData));
            this.setLicensingModel(xut.getNode("LicensingModel", initialData));
            this.setChildren(xut.getNodes("Children/Child", initialData));

            log.info("Oracle license successfully parsed: \nKey+ProductKey: " + this.getKey() + "+" + this.getProductKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license is not defined correctly", e.getDetails());
        }
    }

    public OracleLicense(com.compassplus.configurationModel.OracleLicense oracleLicense, Proposal proposal, com.compassplus.configurationModel.Product product) throws PCTDataFormatException {
        this.proposal = proposal;
        coefficient =  oracleLicense.getCoefficients().get(0);
        setOracleLicense(oracleLicense.getKey(), proposal.getConfig());
        setProduct(product.getName(), proposal.getConfig());
    }

    public com.compassplus.configurationModel.OracleLicense getOracleLicense() {
        return this.license;
    }

    private void setOracleLicense(String key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = key;
            license = config.getOracleLicenses().get(keyString);
            if (license == null) {
                throw new PCTDataFormatException("No such oracle license");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license key is not defined correctly", e.getDetails());
        }
    }

    private void setOracleLicense(Node key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            license = config.getOracleLicenses().get(keyString);
            if (license == null) {
                throw new PCTDataFormatException("No such oracle license");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license key is not defined correctly", e.getDetails());
        }
    }

    private void setProduct(Node key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            product = config.getProducts().get(keyString);
            if (product == null) {
                throw new PCTDataFormatException("No such product");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license product is not defined correctly", e.getDetails());
        }
    }

    private void setProduct(String key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = key;
            product = config.getProducts().get(keyString);
            if (product == null) {
                throw new PCTDataFormatException("No such product");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license product is not defined correctly", e.getDetails());
        }
    }

    private void setCores(Node cores) throws PCTDataFormatException {
        try {
            this.cores = xut.getInteger(cores);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license cores is not defined correctly", e.getDetails());
        }
    }

    public void setCores(int val) {
        this.cores = val;
    }

    public Integer getCores() {
        return this.cores;
    }

    private void setDiscount(Node discount) throws PCTDataFormatException {
        try {
            this.discount = xut.getDouble(discount);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license discount is not defined correctly", e.getDetails());
        }
    }

    public void setDiscount(double val) {
        this.discount = val;
    }

    public Double getDiscount() {
        return this.discount;
    }

    private void setCoefficient(Node coefficient) throws PCTDataFormatException {
        try {
            String coefficientKey = xut.getString(coefficient);
            for(Coefficient c : getOracleLicense().getCoefficients()){
                if(coefficientKey.equals(c.getKey())){
                    this.coefficient = c;
                    break;
                }
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license coefficient is not defined correctly", e.getDetails());
        }
    }

    public void setCoefficient(Coefficient coefficient) {
        this.coefficient = coefficient;
    }

    public Coefficient getCoefficient() {
        return this.coefficient;
    }

    private void setLicensingModel(Node licensingModel) throws PCTDataFormatException {
        try {
            this.licensingModel = xut.getString(licensingModel);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Oracle license licensingModel is not defined correctly", e.getDetails());
        }
    }

    public void setLicensingModel(String val) {
        this.licensingModel = val;
    }

    public String getLicensingModel() {
        return this.licensingModel;
    }

    public void addChild(String key){
        children.add(key);
    }

    public void delChild(String key){
        if(children.contains(key)){
            children.remove(key);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<OracleLicense>");
        sb.append("<Key>").append(getKey()).append("</Key>");
        sb.append("<ProductKey>").append(getProductKey()).append("</ProductKey>");
        sb.append("<Cores>").append(getCores()).append("</Cores>");
        sb.append("<Discount>").append(getDiscount()).append("</Discount>");
        sb.append("<Coefficient>").append(getCoefficient().getKey()).append("</Coefficient>");
        sb.append("<LicensingModel>").append(getLicensingModel()).append("</LicensingModel>");
        sb.append("<Children>");
        for(String s:getChildren()){
            sb.append("<Child>").append(s).append("</Child>");
        }
        sb.append("</Children>");
        sb.append("</OracleLicense>");
        return sb.toString();
    }

    public List<String> getChildren(){
        return children;
    }

    public String getProductKey(){
        return this.getProduct().getName();
    }

    public String getKey() {
        return this.license.getKey();
    }

    public Proposal getProposal() {
        return proposal;
    }

    public Double getRegionalPrice() {
        return getCleanPrice();
    }

    public void setProduct(com.compassplus.configurationModel.Product product){
        this.product = product;
    }

    public com.compassplus.configurationModel.Product getProduct(){
        return this.product;
    }

    public Double getCleanPrice() {
        Double ret = 0d;
        /*if(getInclude()){
            ret += getAttendees() * getTrainingCourse().getPrice();
            ret = proposal.getCurrencyRate() * ret;
        }*/
        return ret;//CommonUtils.getInstance().toNextHundred(ret);
    }

    public double getCPDiscount() {
        if("ASFU".equals(getLicensingModel())){
            return getOracleLicense().getASFUDiscount();
        }else if("FU".equals(getLicensingModel())){
            return getOracleLicense().getFUDiscount();
        }

        return 0;
    }

    private void setChildren(NodeList children) throws PCTDataFormatException {
        this.getChildren().clear();
        if (children.getLength() > 0) {
            log.info("Found " + children.getLength() + " coefficient(s)");
            for (int i = 0; i < children.getLength(); i++) {
                try {
                    String tmpChild = xut.getString(children.item(i));
                    this.getChildren().add(tmpChild);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getChildren().size() + " child(ren)");
        }
    }

    public boolean isShared() {
        return children.size()>0;
    }

    public Double getLicensePrice() {
        return CommonUtils.getInstance().toNextHundred(getOracleLicense().getBasePrice() * getProposal().getCurrencyRate() * CommonUtils.getInstance().toNextInt(getCoefficient().getValue() * getCores()));
    }

    private double getOptionPrice(OracleOption o){
        return  CommonUtils.getInstance().toNextHundred(o.getBasePrice() * getProposal().getCurrencyRate());
    }

    public double getOptionsPrice(boolean isSupportPrice) {
        double optionsPrice = 0d;
        for(String s:getProduct().getOracleOptions()){
            OracleOption o = getProposal().getConfig().getOracleOptions().get(s);
            if(o != null){
                if(isSupportPrice && o.isInclude() || !isSupportPrice){
                    optionsPrice += getOptionPrice(o);
                }
            }
        }
        return optionsPrice;
    }

    public double getOptionsPrice() {
        return getOptionsPrice(false);
    }

    public double getOracleSupportPrice() {
        return CommonUtils.getInstance().toNextHundred(getProposal().getOracleQuote().getOracleLicenses().get(getProduct().getName()).getOracleLicense().getSupportRate() * (getOptionsPrice(true) + getLicensePrice()));
    }

    public double getOracleTotalPrice() {
        return getOracleSupportPrice() + getOptionsPrice() + getLicensePrice();
    }

    public double getOracleCPPrice() {
        return CommonUtils.getInstance().toNextHundred(getOracleTotalPrice() * (100d - getCPDiscount()) / 100d);
    }

    public double getOracleCustomerPrice() {
        return CommonUtils.getInstance().toNextHundred(getOracleTotalPrice() * (1 - getDiscount()));
    }

    public double getOracleCPMargin(){
        return getOracleCustomerPrice() - getOracleCPPrice();
    }

    public boolean canBeMoved() {
        for(OracleLicense l:getProposal().getOracleQuote().getOracleLicenses().values()){
            if(l != this){
                if(l.containsAllOptions(this)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAllOptions(OracleLicense l){
        if(this == l){
            return false;
        }
        for(String s:l.getProduct().getOracleOptions()){
            boolean flag = false;
            for(String d:this.getProduct().getOracleOptions()){
                if(d.equals(s)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                return false;
            }
        }

        return true;
    }

    public boolean isMemberOfAnotherBox(){
        String parentName = getParentName();
        return parentName != null;
    }

    public String getParentName(){
        for(OracleLicense ol:getProposal().getOracleQuote().getOracleLicenses().values()){
            if(ol.getChildren().contains(getProduct().getName())){
                return ol.getProduct().getName() + " Oracle Box";
            }
        }
        return null;
    }
}
