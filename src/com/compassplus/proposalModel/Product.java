package com.compassplus.proposalModel;

import com.compassplus.configurationModel.*;
import com.compassplus.configurationModel.TrainingCourse;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:44
 */
public class Product {
    private static String bullet = "\u2022";
    private static ResourceBundle dict = ResourceBundle.getBundle("dictionary");
    private com.compassplus.configurationModel.Product product;
    private Map<String, Module> modules = new HashMap<String, Module>();
    private Map<String, Capacity> capacities = new HashMap<String, Capacity>();
    private Boolean secondarySale = false;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    //private CommonUtils cut = CommonUtils.getInstance();
    private static DecimalFormat df = new DecimalFormat("#,##0");
    private Double discount;
    private Proposal proposal;
    private Double supportDiscount;
    private Double markUp;
    private License license;
    private java.util.List<String> recommendations = new ArrayList<String>();

    public Proposal getProposal() {
        return this.proposal;
    }

    public Product(Node initialData, Map<String, com.compassplus.configurationModel.Product> allowedProducts, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        init(initialData, allowedProducts);
        setDefaultLicense();
    }

    public Product(com.compassplus.configurationModel.Product product, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        this.setProduct(product);
        this.setCapacities(product);
        this.setModules(product);
        this.setDefaultLicense();
    }

    private void init(Node initialData, Map<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData), allowedProducts);
            this.setDiscount(xut.getNode("Discount", initialData));
            this.setSupportDiscount(xut.getNode("SupportDiscount", initialData));
            this.setSecondarySale(xut.getNode("SecondarySale", initialData));
            this.setLicense(xut.getNode("LicenseKey", initialData));
            this.setMarkUp(xut.getNode("MarkUp", initialData));
            this.setCapacities(xut.getNodes("Capacities/Capacity", initialData));
            this.setModules(xut.getNodes("Modules/Module", initialData));

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nDiscount: " + this.getDiscount() +
                    "\nSupportDiscount: " + this.getSupportDiscount() +
                    "\nMarkUp: " + this.getMarkUp() +
                    "\nLicenseKey: " + (this.getLicense() != null ? this.getLicense().getKey() : "") +
                    "\nSecondarySale: " + this.getSecondarySale());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly", e.getDetails());
        }
    }

    private void setDefaultLicense() {
        if (this.license == null && getProduct().getLicenses().size() > 0) {
            this.license = (License) getProduct().getLicenses().values().toArray()[0];
        }
    }

    private void setLicense(Node license) throws PCTDataFormatException {
        try {
            String licenseKey = xut.getString(license, true);
            this.license = getProduct().getLicenses().get(licenseKey);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product license key is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getProduct().getName();
    }

    private void setProduct(com.compassplus.configurationModel.Product product) {
        this.product = product;
    }

    public com.compassplus.configurationModel.Product getProduct() {
        return this.product;
    }

    private void setName(Node name, Map<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);
            this.setProduct(allowedProducts.get(nameString));
            if (this.getProduct() == null) {
                throw new PCTDataFormatException("No such product \"" + nameString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public Map<String, Module> getModules() {
        return this.modules;
    }

    private void setModules(NodeList modules) {
        this.getModules().clear();
        if (modules.getLength() > 0) {
            log.info("Found " + modules.getLength() + " modules(s)");
            for (int i = 0; i < modules.getLength(); i++) {
                try {
                    Module tmpModule = new Module(modules.item(i), this.getProduct().getModules());
                    this.getModules().put(tmpModule.getKey(), tmpModule);
                    addModuleRecommendation(tmpModule.getKey());
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getModules().size() + " modules(s)");
        }
    }

    public void addModule(com.compassplus.configurationModel.Module module, String key) throws PCTDataFormatException {
        Module tmpModule = new Module(module, key);
        this.getModules().put(tmpModule.getKey(), tmpModule);
        addModuleRecommendation(key);
        addModuleTrainingCourse(key);
    }

    private void addModuleTrainingCourse(String key){
        com.compassplus.configurationModel.Module module = proposal.getConfig().getProducts().get(this.getName()).getModules().get(key);
        if (module.getTrainingCourses().size() > 0) {
            for (String rKey : module.getTrainingCourses()) {
                TrainingCourse r = proposal.getConfig().getTrainingCourses().get(rKey);
                if (r != null) {
                    //recommendations.add(r.getKey());
                    com.compassplus.proposalModel.TrainingCourse tc = null;
                    if(proposal.getPSQuote().getTrainingCourses().containsKey(rKey)){
                        tc = proposal.getPSQuote().getTrainingCourses().get(rKey);
                    }else{
                        try{
                        tc = new com.compassplus.proposalModel.TrainingCourse(r, proposal);
                        }catch(Exception e){}
                        proposal.getPSQuote().addTrainingCourse(tc);
                    }
                    tc.addSubscription(key);
                }
            }
        }else{
        }
    }

    private void addModuleRecommendation(String key) throws PCTDataFormatException {
        com.compassplus.configurationModel.Module module = proposal.getConfig().getProducts().get(this.getName()).getModules().get(key);
        if (module.getRecommendations().size() > 0) {
            for (String rKey : module.getRecommendations()) {
                Recommendation r = proposal.getConfig().getRecommendations().get(rKey);
                if (r != null) {
                    recommendations.add(r.getKey());
                    proposal.getPSQuote().addService(new Service(r, proposal, null, null));
                }
            }
        }else{
        }
    }

    private void addCapacityRecommendation(String key) throws PCTDataFormatException {
        com.compassplus.configurationModel.Capacity capacity = proposal.getConfig().getProducts().get(this.getName()).getCapacities().get(key);
        if (capacity.getRecommendations().size() > 0) {
            for (String rKey : capacity.getRecommendations()) {
                Recommendation r = proposal.getConfig().getRecommendations().get(rKey);
                if (r != null) {
                    recommendations.add(r.getKey());
                    proposal.getPSQuote().addService(new Service(r, proposal, key, this.getName()));
                }
            }
        }else{
        }
    }

    public Map<String, Capacity> getCapacities() {
        return this.capacities;
    }

    private void setCapacities(NodeList capacities) {
        this.getCapacities().clear();
        if (capacities.getLength() > 0) {
            log.info("Found " + capacities.getLength() + " capacity(ies)");
            for (int i = 0; i < capacities.getLength(); i++) {
                try {
                    Capacity tmpCapacity = new Capacity(capacities.item(i), this.getProduct().getCapacities());
                    this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
                    addCapacityRecommendation(tmpCapacity.getKey());
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(ies)");
        }
    }

    /*if (modulesGroup.getDefaultModuleKey() != null && !modulesGroup.getDefaultModuleKey().equals("")) {
        if (key.equals(modulesGroup.getDefaultModuleKey())) {
            checkBoxesToCheck.add(mc);
        }
    } else if (firstModule) {
        checkBoxesToCheck.add(mc);
    }*/
    private void setModules(com.compassplus.configurationModel.Product product) throws PCTDataFormatException {
        //this.getCapacities().clear();
        setModules(product, product.getModulesRoot());
    }

    private void setModules(com.compassplus.configurationModel.Product product, ModulesGroup group) throws PCTDataFormatException {
        for (ModulesGroup g : group.getGroups()) {
            setModules(product, g);
        }
        if (group.isRadioButtonGroup()) {
            com.compassplus.configurationModel.Module tmpModule = null;
            if (group.getDefaultModuleKey() != null && !group.getDefaultModuleKey().equals("")) {

                tmpModule = product.getModules().get(group.getDefaultModuleKey());
                if (tmpModule != null && tmpModule.isDeprecated()) {
                    tmpModule = null;
                }
            } else {
                for (com.compassplus.configurationModel.Module m : group.getModules().values()) {
                    if (!m.isDeprecated()) {
                        tmpModule = m;
                        break;
                    }
                }
            }
            if (tmpModule != null) {
                this.getModules().put(tmpModule.getKey(), new Module(tmpModule, tmpModule.getKey()));
                addModuleRecommendation(tmpModule.getKey());
                for (RequireCapacity rc : tmpModule.getRequireCapacities().values()) {
                    if (!getCapacities().containsKey(rc.getKey())) {
                        addCapacity(getProduct().getCapacities().get(rc.getKey()), rc.getKey());
                    }
                    if (rc.isIncremental()) {
                        getCapacities().get(rc.getKey()).addIncr(rc.getValue());
                        if (rc.isFreeOfCharge()) {
                            getCapacities().get(rc.getKey()).addFoc(rc.getValue());
                        }
                    } else {
                        getCapacities().get(rc.getKey()).addMin(rc.getValue());
                    }
                }
            }
        }
    }

    private void setCapacities(com.compassplus.configurationModel.Product product) throws PCTDataFormatException {
        this.getCapacities().clear();
        for (com.compassplus.configurationModel.Capacity c : product.getCapacities().values()) {
            if (c.getMinValue() != null && c.getMinValue() > 0 && !c.isDeprecated()) {
                Capacity tmpCapacity = new Capacity(c, c.getKey());
                tmpCapacity.setUser(c.getMinValue());
                this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
                addCapacityRecommendation(tmpCapacity.getKey());
            }
        }
    }

    public void addCapacity(com.compassplus.configurationModel.Capacity capacity, String key) throws PCTDataFormatException {
        Capacity tmpCapacity = new Capacity(capacity, key);
        this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
        addCapacityRecommendation(tmpCapacity.getKey());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Product>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<Discount>").append(this.getDiscount()).append("</Discount>");
        sb.append("<SupportDiscount>").append(this.getSupportDiscount()).append("</SupportDiscount>");
        sb.append("<MarkUp>").append(this.getMarkUp()).append("</MarkUp>");
        sb.append("<SecondarySale>").append(this.getSecondarySale()).append("</SecondarySale>");
        if (getLicense() != null) {
            sb.append("<LicenseKey>").append(this.getLicense().getKey()).append("</LicenseKey>");
        }
        if (this.getModules() != null && this.getModules().size() > 0) {
            sb.append("<Modules>");
            for (Module m : this.getModules().values()) {
                sb.append(m.toString());
            }
            sb.append("</Modules>");
        }
        if (this.getCapacities() != null && this.getCapacities().size() > 0) {
            sb.append("<Capacities>");
            for (Capacity c : this.getCapacities().values()) {
                sb.append(c.toString());
            }
            sb.append("</Capacities>");
        }
        sb.append("</Product>");
        return sb.toString();
    }

    public Boolean getSecondarySale() {
        return this.secondarySale;
    }

    private void setSecondarySale(Node secondarySale) throws PCTDataFormatException {
        try {
            this.secondarySale = xut.getBoolean(secondarySale, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product secondary sale is not defined correctly", e.getDetails());
        }
    }

    public void setSecondarySale(Boolean secondarySale) {
        this.secondarySale = secondarySale;
    }

    public Double getDiscount() {
        return this.discount != null ? this.discount : 0d;
    }

    private void setDiscount(Node discount) throws PCTDataFormatException {
        try {
            this.discount = xut.getDouble(discount, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product discount is not defined correctly", e.getDetails());
        }
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getPrice() {
        Double price = 0d;
        for (Module m : this.getModules().values()) {
            price += m.getPrice(this);
        }

        for (Capacity c : this.getCapacities().values()) {
            //String licenseKey = getProduct().getCapacities().get(c.getKey()).getLicence;
            //if (licenseKey.equals("") || getLicense() != null && licenseKey.equals(getLicense().getKey())) {
            if (getProduct().getCapacities().get(c.getKey()).checkLicenseKey(getLicense() != null ? getLicense().getKey() : null)) {
                price += c.getPrice(this);
            }
        }

        Double minPrice = CommonUtils.getInstance().toNextThousand(getProduct().getMinimumPrice() * getProposal().getCurrencyRate());
        return (price > minPrice || getSecondarySale()) ? price : minPrice;
    }

    public Double getEndUserPrice() {
        //return getRegionPrice() - getRegionPrice(true) * getDiscount();
        return getRegionPrice() - getRegionPrice() * getDiscount();
    }

    public Double getRegionPrice(boolean clean) {
        return getPrice() * getProposal().getRegion().getRate(this.getName()) * (clean ? 1 : getMarkUp());
    }

    public Double getRegionPrice() {
        return getRegionPrice(false);
    }

    public Double getSupportPrice() {
        /*return CommonUtils.getInstance().toNextInt(getSupportPriceUndiscounted() -
                getSupportPriceUndiscounted(true) * getSupportDiscount());*/
        return getSupportPriceUndiscounted() - getSupportPriceUndiscounted() * getSupportDiscount();
    }

    public Double getSupportPriceUndiscounted() {
        return getSupportPriceUndiscounted(false);
    }

    public Double getSupportPriceUndiscounted(boolean clean) {
        return CommonUtils.getInstance().toNextInt(getRegionPrice(clean) * getProposal().getSupportRate());
    }

    public boolean canBeSwitchedToZero(String mKey, String extraKey) {
        for (Module m : getModules().values()) {
            if (!m.getKey().equals(extraKey) && getProduct().getModules().get(m.getKey()).getRequireCapacities().containsKey(mKey)) {
                return false;
            }
        }
        return true;
    }

    public boolean canBeEnabled(String mKey, ArrayList<String> extraKeys) {
        if (getProduct().getModules().get(mKey).isDeprecated()) {
            return false;
        }
        ArrayList<String> excludeKeys = new ArrayList<String>(0);
        for (String key : getProduct().getModules().get(mKey).getExcludeModules()) {
            if (getModules().containsKey(key) || extraKeys.contains(key)) {
                excludeKeys.add(key);
            }
        }
        ArrayList<String> excludeThisKeys = new ArrayList<String>(0);
        for (String key : getModules().keySet()) {
            if (getProduct().getModules().get(key).getExcludeModules().contains(mKey)) {
                excludeThisKeys.add(key);
            }
        }
        if (excludeKeys.size() > 0 || excludeThisKeys.size() > 0) {
            return false;
        } else {
            ArrayList<String> requireKeys = new ArrayList<String>(0);
            for (String key : getProduct().getModules().get(mKey).getRequireModules()) {
                if (!getModules().containsKey(key) && !extraKeys.contains(key)) {
                    requireKeys.add(key);
                }
            }
            if (requireKeys.size() > 0) {
                return false;
            }
        }
        for (String key : extraKeys) {
            if (!key.equals(mKey)) {
                if (key.split("\\s+").length > 1 || getProduct().getModules().get(key).isRadioMember()) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProduct().getShortName().equals("") ? getProduct().getName() : getProduct().getShortName());
        String ms = getSelectedModulesString();
        String cs = getSelectedCapacitiesString();
        if (ms.length() != 0 || cs.length() != 0) {
            sb.append(":\n");
        }
        if (ms.length() != 0) {
            sb.append(ms);
            sb.append(";\n");
        }
        if (cs.length() != 0) {
            sb.append(cs);
            sb.append(";\n");
        }
        if (sb.toString().endsWith("\n")) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    private String getSelectedModulesString() {
        return getSelectedModulesString(null, "");
    }

    private String getSelectedModulesString(ModulesGroup modulesGroup, String pad) {
        StringBuilder sb = new StringBuilder();
        boolean appendGroupName = true;
        if (modulesGroup == null) {
            modulesGroup = getProduct().getModulesRoot();
            appendGroupName = false;
        }
        boolean containsModules = false;
        for (String key : modulesGroup.getModules().keySet()) {
            com.compassplus.configurationModel.Module m = modulesGroup.getModules().get(key);
            if (this.getModules().containsKey(key)) {
                containsModules = true;
                if (!m.isHidden()) {
                    //sb.append(pad);
                    //sb.append("  -");
                    sb.append(m.getShortName().equals("") ? m.getName() : m.getShortName());
                    sb.append(", ");
                    //sb.append("\n");
                }
            }
        }
        for (ModulesGroup mg : modulesGroup.getGroups()) {
            String tres = getSelectedModulesString(mg, pad + "  ");
            if (tres.length() > 0) {
                sb.append(tres).append(", ");
            }
        }

        if (sb.length() > 0) {
            if (appendGroupName && !modulesGroup.isHidden()) {
                //sb.insert(0, ":\n");
                sb.insert(0, " (");
                sb.insert(0, modulesGroup.getShortName().equals("") ? modulesGroup.getName() : modulesGroup.getShortName());
                //sb.insert(0, pad);
                if (sb.toString().endsWith(", ")) {
                    sb.setLength(sb.length() - 2);
                }
                sb.append("), ");
            }

            if (sb.toString().endsWith(", ")) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        } else {
            if (containsModules && !modulesGroup.isHidden()) {
                sb.append(modulesGroup.getShortName().equals("") ? modulesGroup.getName() : modulesGroup.getShortName());
                return sb.toString();
            }
            return "";
        }
    }

    private String getSelectedCapacitiesString() {
        return getSelectedCapacitiesString(null, "");
    }

    private String getSelectedCapacitiesString(CapacitiesGroup capacitiesGroup, String pad) {
        StringBuilder sb = new StringBuilder();
        boolean appendGroupName = true;
        if (capacitiesGroup == null) {
            capacitiesGroup = getProduct().getCapacitiesRoot();
            appendGroupName = false;
        }
        boolean containsCapacities = false;
        for (String key : capacitiesGroup.getCapacities().keySet()) {
            com.compassplus.configurationModel.Capacity c = capacitiesGroup.getCapacities().get(key);
            if (this.getCapacities().containsKey(key)) {
                containsCapacities = true;
                if (!c.isHidden()) {
                    Capacity cc = this.getCapacities().get(key);
                    //sb.append(pad);
                    //sb.append("  -");
                    //String licenseKey = c.getLicenseKey();
                    if (c.checkLicenseKey(getLicense() != null ? getLicense().getKey() : null)) {
                        sb.append(c.getShortName().equals("") ? c.getName() : c.getShortName());
                        sb.append("=").append(df.format(cc.getVal()));
                        sb.append(", ");
                    }
                    //sb.append("\n");
                }
            }
        }
        for (CapacitiesGroup cg : capacitiesGroup.getGroups()) {
            String tres = getSelectedCapacitiesString(cg, pad + "  ");
            if (tres.length() > 0) {
                sb.append(tres).append(", ");
            }
        }

        if (sb.length() > 0) {
            if (appendGroupName && !capacitiesGroup.isHidden()) {
                //sb.insert(0, ":\n");
                sb.insert(0, " (");
                sb.insert(0, capacitiesGroup.getShortName().equals("") ? capacitiesGroup.getName() : capacitiesGroup.getShortName());
                //sb.insert(0, pad);
                if (sb.toString().endsWith(", ")) {
                    sb.setLength(sb.length() - 2);
                }
                sb.append("), ");
            }

            if (sb.toString().endsWith(", ")) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        } else {
            if (containsCapacities && !capacitiesGroup.isHidden()) {
                sb.append(capacitiesGroup.getShortName().equals("") ? capacitiesGroup.getName() : capacitiesGroup.getShortName());
                return sb.toString();
            }
            return "";
        }
    }

    public void delModule(String key) {
        getModules().remove(key);
        removeModuleRecommendation(key);
    }

    private void removeModuleRecommendation(String key){
        com.compassplus.configurationModel.Module module = proposal.getConfig().getProducts().get(this.getName()).getModules().get(key);
        if (module.getRecommendations().size() > 0) {
            for (String rKey : module.getRecommendations()) {
                Recommendation r = proposal.getConfig().getRecommendations().get(rKey);
                if (r != null) {
                    recommendations.remove(r.getKey());
                    proposal.getPSQuote().delService(r.getKey());
                }
            }
        }
    }

    private void removeCapacityRecommendation(String key){
        com.compassplus.configurationModel.Capacity capacity = proposal.getConfig().getProducts().get(this.getName()).getCapacities().get(key);
        if (capacity.getRecommendations().size() > 0) {
            for (String rKey : capacity.getRecommendations()) {
                Recommendation r = proposal.getConfig().getRecommendations().get(rKey);
                if (r != null) {
                    recommendations.remove(r.getKey());
                    proposal.getPSQuote().delService(r.getKey());
                }
            }
        }
    }

    private void removeModuleTrainingCourse(String key){
        com.compassplus.configurationModel.Module module = proposal.getConfig().getProducts().get(this.getName()).getModules().get(key);
        if (module.getTrainingCourses().size() > 0) {
            for (String rKey : module.getTrainingCourses()) {
                proposal.getPSQuote().delTrainingCourse(rKey, key);
            }
        }
    }
    public void delCapacity(String key) {
        getCapacities().remove(key);
        removeCapacityRecommendation(key);
    }

    public Double getSupportDiscount() {
        return this.supportDiscount != null ? this.supportDiscount : 0d;
    }

    private void setSupportDiscount(Node supportDiscount) throws PCTDataFormatException {
        try {
            this.supportDiscount = xut.getDouble(supportDiscount, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product support discount is not defined correctly", e.getDetails());
        }
    }

    public void setSupportDiscount(Double supportDiscount) {
        this.supportDiscount = supportDiscount;
    }

    public void setMarkUp(Double markUp) {
        this.markUp = markUp;
    }

    private void setMarkUp(Node markUp) throws PCTDataFormatException {
        try {
            this.markUp = xut.getDouble(markUp, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product mark up is not defined correctly", e.getDetails());
        }
    }

    public Double getMarkUp() {
        return this.markUp != null ? this.markUp : 1d;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public License getLicense() {
        return this.license;
    }

    public String checkForConsistence() {
        Product p = this;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbDeprecated = new StringBuilder();
        StringBuilder sbDependencies = new StringBuilder();
        for (String key : p.getModules().keySet()) {
            for (String rkey : p.getProduct().getModules().get(key).getRequireModules()) {
                String rkeys[] = rkey.split("\\s+");
                boolean contains = false;
                for (int i = 0; i < rkeys.length; i++) {
                    if (p.getModules().containsKey(rkeys[i])) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    if (rkeys.length == 1) {
                        sbDependencies.append("\nModule \"").append(p.getProduct().getModules().get(key).getPath()).append("\" requires disabled module \"").append(p.getProduct().getModules().get(rkey).getPath()).append("\"");
                    } else if (rkeys.length > 1) {
                        sbDependencies.append("\nModule \"").append(p.getProduct().getModules().get(key).getPath()).append("\" requires one of disabled modules: ");
                        for (int i = 0; i < rkeys.length; i++) {
                            if (i > 0) {
                                sbDependencies.append(" or ");
                            }
                            sbDependencies.append("\"").append(p.getProduct().getModules().get(rkeys[i]).getPath()).append("\"");
                        }
                    }
                }
            }
            for (String rkey : p.getProduct().getModules().get(key).getExcludeModules()) {
                if (p.getModules().containsKey(rkey)) {
                    sbDependencies.append("\nModule \"").append(p.getProduct().getModules().get(key).getPath()).append("\" conflicts with module \"").append(p.getProduct().getModules().get(rkey).getPath()).append("\"");
                }
            }
            if (p.getProduct().getModules().get(key).isDeprecated()) {
                sbDeprecated.append("\nDeprecated module \"").append(p.getProduct().getModules().get(key).getPath()).append("\"");
            }
        }
        for (String key : p.getCapacities().keySet()) {
            for (String rkey : p.getProduct().getCapacities().get(key).getRequireModules()) {
                String rkeys[] = rkey.split("\\s+");
                boolean contains = false;
                for (int i = 0; i < rkeys.length; i++) {
                    if (p.getModules().containsKey(rkeys[i])) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    if (rkeys.length == 1) {
                        sbDependencies.append("\nCapacity \"").append(p.getProduct().getCapacities().get(key).getPath()).append("\" requires disabled module \"").append(p.getProduct().getModules().get(rkey).getPath()).append("\"");
                    } else if (rkeys.length > 1) {
                        sbDependencies.append("\nCapacity \"").append(p.getProduct().getCapacities().get(key).getPath()).append("\" requires one of disabled modules: ");
                        for (int i = 0; i < rkeys.length; i++) {
                            if (i > 0) {
                                sbDependencies.append(" or ");
                            }
                            sbDependencies.append("\"").append(p.getProduct().getModules().get(rkeys[i]).getPath()).append("\"");
                        }
                    }
                }
            }
            if (p.getProduct().getCapacities().get(key).isDeprecated()) {
                sbDeprecated.append("\nDeprecated capacity \"").append(p.getProduct().getCapacities().get(key).getPath()).append("\"");
            }
        }
        if (sbDependencies.length() > 0 || sbDeprecated.length() > 0) {
            sb.append("\nProduct ").append(p.getName()).append(" contains following error(s):").append(sbDependencies).append(sbDeprecated);
        }
        return sb.toString();
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
