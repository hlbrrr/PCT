package com.compassplus.proposalModel;

import com.compassplus.configurationModel.CapacitiesGroup;
import com.compassplus.configurationModel.ModulesGroup;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
    private CommonUtils cut = CommonUtils.getInstance();
    private static DecimalFormat df = new DecimalFormat("#,##0");
    private Double discount;
    private Proposal proposal;

    public Proposal getProposal() {
        return this.proposal;
    }

    public Product(Node initialData, Map<String, com.compassplus.configurationModel.Product> allowedProducts, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        init(initialData, allowedProducts);
    }

    public Product(com.compassplus.configurationModel.Product product, Proposal proposal) {
        this.proposal = proposal;
        this.setProduct(product);
        this.setCapacities(product);
    }

    private void init(Node initialData, Map<String, com.compassplus.configurationModel.Product> allowedProducts) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData), allowedProducts);
            this.setDiscount(xut.getNode("Discount", initialData));
            this.setSecondarySale(xut.getNode("SecondarySale", initialData));
            this.setModules(xut.getNodes("Modules/Module", initialData));
            this.setCapacities(xut.getNodes("Capacities/Capacity", initialData));

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nSecondarySale: " + this.getSecondarySale());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly", e.getDetails());
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
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getModules().size() + " modules(s)");
        }
    }

    public void addModule(com.compassplus.configurationModel.Module module, String key) {
        Module tmpModule = new Module(module, key);
        this.getModules().put(tmpModule.getKey(), tmpModule);
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
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(ies)");
        }
    }

    private void setCapacities(com.compassplus.configurationModel.Product product) {
        this.getCapacities().clear();
        for (com.compassplus.configurationModel.Capacity c : product.getCapacities().values()) {
            if (c.getMinValue() != null && c.getMinValue() > 0) {
                Capacity tmpCapacity = new Capacity(c, c.getKey());
                tmpCapacity.setUser(c.getMinValue());
                this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
            }
        }
    }

    public void addCapacity(com.compassplus.configurationModel.Capacity capacity, String key) {
        Capacity tmpCapacity = new Capacity(capacity, key);
        this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Product>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("<Discount>").append(this.getDiscount()).append("</Discount>");
        sb.append("<SecondarySale>").append(this.getSecondarySale()).append("</SecondarySale>");
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
            price += c.getPrice(this);
        }

        Double minPrice = CommonUtils.getInstance().toNextThousand(getProduct().getMinimumPrice() * getProposal().getCurrencyRate());
        return price > minPrice ? price : minPrice;
    }

    public boolean canBeEnabled(String mKey, ArrayList<String> extraKeys) {
        if (getProduct().getModules().get(mKey).isDeprecated()) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
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

        for (String key : modulesGroup.getModules().keySet()) {
            if (this.getModules().containsKey(key)) {
                com.compassplus.configurationModel.Module m = modulesGroup.getModules().get(key);
                //sb.append(pad);
                //sb.append("  -");
                sb.append(m.getShortName().equals("") ? m.getName() : m.getShortName());
                sb.append(", ");
                //sb.append("\n");
            }
        }

        for (ModulesGroup mg : modulesGroup.getGroups()) {
            String tres = getSelectedModulesString(mg, pad + "  ");
            if (tres.length() > 0) {
                sb.append(tres).append(", ");
            }
        }

        if (sb.length() > 0) {
            if (appendGroupName) {
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

        for (String key : capacitiesGroup.getCapacities().keySet()) {
            if (this.getCapacities().containsKey(key)) {
                Capacity cc = this.getCapacities().get(key);
                com.compassplus.configurationModel.Capacity c = capacitiesGroup.getCapacities().get(key);
                //sb.append(pad);
                //sb.append("  -");
                sb.append(c.getShortName().equals("") ? c.getName() : c.getShortName());
                sb.append("=").append(df.format(cc.getVal()));
                sb.append(", ");
                //sb.append("\n");
            }
        }

        for (CapacitiesGroup cg : capacitiesGroup.getGroups()) {
            String tres = getSelectedCapacitiesString(cg, pad + "  ");
            if (tres.length() > 0) {
                sb.append(tres).append(", ");
            }
        }

        if (sb.length() > 0) {
            if (appendGroupName) {
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
            return "";
        }
    }

    public void delModule(String key) {
        getModules().remove(key);
    }

    public void delCapacity(String key) {
        getCapacities().remove(key);
    }
}
