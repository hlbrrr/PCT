package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Result;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 10:44 AM
 */
public class Product {
    private String name;
    private String shortName;
    private Double maximumFunctionalityPrice;

    private Double totalWeight;
    private Double minimumPrice;
    private Map<String, License> licenses = new LinkedHashMap<String, License>(0);
    private ModulesGroup modulesRoot = new ModulesGroup("Modules", "");
    private CapacitiesGroup capacitiesRoot = new CapacitiesGroup("Capacities", "");
    private Map<String, Module> modules = new HashMap<String, Module>();
    private Map<String, Capacity> capacities = new HashMap<String, Capacity>();
    private Double secondarySalesRate;
    private ArrayList<String> trainingCourses = new ArrayList<String>(0);
    private ArrayList<String> oracleOptions = new ArrayList<String>(0);
    private ArrayList<String> oracleLicenses = new ArrayList<String>(0);
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Product(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    public String toString() {
        return this.getName();
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing product");

            this.setName(xut.getNode("Name", initialData));
            this.setShortName(xut.getNode("ShortName", initialData));
            this.setLicenses(xut.getNodes("Licenses/License", initialData));
            this.setMaximumFunctionalityPrice(xut.getNode("MaximumFunctionalityPrice", initialData));
            this.setMinimumPrice(xut.getNode("MinimumPrice", initialData));
            this.setModules(xut.getNode("Modules", initialData));
            this.setCapacities(xut.getNode("Capacities", initialData));
            this.setSecondarySalesRate(xut.getNode("SecondarySalesRate", initialData));
            this.setTrainingCourses(xut.getNodes("Dependencies/TrainingCourse", initialData));
            this.setOracleOptions(xut.getNodes("Dependencies/OracleOption", initialData));
            this.setOracleLicenses(xut.getNodes("Dependencies/OracleLicense", initialData));


            for (Capacity c : this.getCapacities().values()) {
                if (c.getLinkKey() != null) {
                    if (this.getCapacities().containsKey(c.getLinkKey())) {
                        Capacity proto = this.getCapacities().get(c.getLinkKey());
                        c.setType(proto.getType());
                        c.setDeprecated(proto.isDeprecated());
                        c.setHidden(proto.isHidden());
                        c.setMinValue(proto.getMinValue());
                        c.setTiers(proto.getTiers());
                        c.setLinkKey(null);
                    } else {
                        throw new PCTDataFormatException("Capacity \"" + c.getKey() + "\" is missing");
                    }
                }
            }

            this.checkKeys();
            this.setTotalWeight();

            log.info("Product successfully parsed: \nName: " + this.getName() +
                    "\nShortName: " + this.getShortName() +
                    "\nMaximumFunctionalityPrice: " + this.getMaximumFunctionalityPrice() +
                    "\nMinimumPrice: " + this.getMinimumPrice() +
                    "\nSecondarySalesRate: " + this.getSecondarySalesRate() +
                    "\nStructure: \n" + modulesRoot.toString() + "\n" + capacitiesRoot.toString());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public ArrayList<String> getTrainingCourses() {
        return trainingCourses;
    }

    private void setTrainingCourses(NodeList trainingCourses) {
        this.getTrainingCourses().clear();
        if (trainingCourses.getLength() > 0) {

            log.info("Found " + trainingCourses.getLength() + " training course(s)");
            for (int i = 0; i < trainingCourses.getLength(); i++) {
                try {
                    this.getTrainingCourses().add(xut.getString(trainingCourses.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getTrainingCourses().size() + " product training course(s)");
        }
    }

    public ArrayList<String> getOracleOptions() {
        return oracleOptions;
    }

    private void setOracleOptions(NodeList oracleOptions) {
        this.getOracleOptions().clear();
        if (oracleOptions.getLength() > 0) {

            log.info("Found " + oracleOptions.getLength() + " oracle option(s)");
            for (int i = 0; i < oracleOptions.getLength(); i++) {
                try {
                    this.getOracleOptions().add(xut.getString(oracleOptions.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getOracleOptions().size() + " product oracle option(s)");
        }
    }

    public ArrayList<String> getOracleLicenses() {
        return oracleLicenses;
    }

    private void setOracleLicenses(NodeList oracleLicenses) {
        this.getOracleLicenses().clear();
        if (oracleLicenses.getLength() > 0) {

            log.info("Found " + oracleLicenses.getLength() + " oracle license(s)");
            for (int i = 0; i < oracleLicenses.getLength(); i++) {
                try {
                    this.getOracleLicenses().add(xut.getString(oracleLicenses.item(i)));
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getOracleOptions().size() + " product oracle license(s)");
        }
    }

    public Map<String, License> getLicenses() {
        return this.licenses;
    }

    private void setLicenses(NodeList licenses) throws PCTDataFormatException {
        this.getLicenses().clear();
        if (licenses.getLength() > 0) {
            log.info("Found " + licenses.getLength() + " license(s)");
            for (int i = 0; i < licenses.getLength(); i++) {
                try {
                    License tmpLicense = new License(licenses.item(i));
                    this.getLicenses().put(tmpLicense.getKey(), tmpLicense);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getLicenses().size() + " license(s)");
        }
    }

    public void checkKeys() throws PCTDataFormatException {
        boolean normal = true;
        for (Module m : this.getModules().values()) {
            for (String s : m.getExcludeModules()) {
                if (!this.getModules().containsKey(s)) {
                    normal = false;
                    log.error("No such module \"" + s + "\" for " + m.getPath());
                }
            }
            for (String s : m.getRequireModules()) {
                String keys[] = s.split("\\s+");
                for (int i = 0; i < keys.length; i++) {
                    if (!this.getModules().containsKey(keys[i])) {
                        normal = false;
                        log.error("No such module \"" + keys[i] + "\" for " + m.getPath());
                    }
                }
            }
            for (RequireCapacity c : m.getRequireCapacities().values()) {
                if (!this.getCapacities().containsKey(c.getKey())) {
                    normal = false;
                    log.error("No such capacity \"" + c.getKey() + "\" for " + m.getPath());
                }
            }
        }
        for (Capacity c : this.getCapacities().values()) {
            for (String s : c.getRequireModules()) {
                String keys[] = s.split("\\s+");
                for (int i = 0; i < keys.length; i++) {
                    if (!this.getModules().containsKey(keys[i])) {
                        normal = false;
                        log.error("No such module \"" + keys[i] + "\" for " + c.getPath());
                    }
                }
            }
        }

        if (!normal) {
            throw new PCTDataFormatException("Product modules/capacities dependencies contains unknown keys");
        }
    }

    public Double getSecondarySalesRate() {
        return secondarySalesRate;
    }

    private void setSecondarySalesRate(Node secondarySalesRate) throws PCTDataFormatException {
        try {
            this.secondarySalesRate = xut.getDouble(secondarySalesRate);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product secondary sales rate is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product name is not defined correctly", e.getDetails());
        }
    }

    public String getShortName() {
        return shortName;
    }

    private void setShortName(Node shortName) throws PCTDataFormatException {
        try {
            this.shortName = xut.getString(shortName, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product short name is not defined correctly", e.getDetails());
        }
    }

    public Double getMaximumFunctionalityPrice() {
        return maximumFunctionalityPrice;
    }

    private void setMaximumFunctionalityPrice(Node maximumFunctionalityPrice) throws PCTDataFormatException {
        try {
            this.maximumFunctionalityPrice = xut.getDouble(maximumFunctionalityPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product maximum functionality price is not defined correctly", e.getDetails());
        }
    }

    public Double getMinimumPrice() {
        return minimumPrice;
    }

    private void setMinimumPrice(Node minimumPrice) throws PCTDataFormatException {
        try {
            this.minimumPrice = xut.getDouble(minimumPrice);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Product minimum price is not defined correctly", e.getDetails());
        }
    }

    public Map<String, Module> getModules() {
        return modules;
    }

    private void setModules(Node modulesNode) {
        setModules(modulesNode, null, "/");
    }

    private void setModules(Node modulesNode, ModulesGroup modulesGroup, String prefix) {
        if (modulesGroup == null) {
            this.getModules().clear();
            modulesGroup = this.getModulesRoot();
            modulesGroup.getModules().clear();
            modulesGroup.getGroups().clear();
        }

        log.info("Parsing modules group");
        NodeList modules = xut.getNodes("Module", modulesNode);
        if (modules.getLength() > 0) {
            log.info("Found " + modules.getLength() + " modules(s)");
            for (int i = 0; i < modules.getLength(); i++) {
                try {
                    Module tmpModule = new Module(modules.item(i));
                    tmpModule.setPath(prefix + tmpModule.getName());
                    modulesGroup.addModule(tmpModule.getKey(), tmpModule);
                    this.getModules().put(tmpModule.getKey(), tmpModule);
                    tmpModule.setIsRadioMember(modulesGroup.isRadioButtonGroup());
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + modulesGroup.getModules().size() + " modules(s)");
        }

        NodeList groups = xut.getNodes("Group", modulesNode);
        if (groups.getLength() > 0) {
            log.info("Found " + groups.getLength() + " subgroup(s)");
            for (int i = 0; i < groups.getLength(); i++) {
                try {
                    ModulesGroup tmpModulesGroup = new ModulesGroup(xut.getNode("Name", groups.item(i)), xut.getNode("ShortName", groups.item(i)), xut.getNode("Hint", groups.item(i)), xut.getNode("RadioButtonGroup", groups.item(i)), xut.getNode("DefaultModuleKey", groups.item(i)), xut.getNode("Hidden", groups.item(i)));
                    modulesGroup.addModulesGroup(tmpModulesGroup);
                    this.setModules(xut.getNode("Modules", groups.item(i)), tmpModulesGroup, prefix + tmpModulesGroup.getName() + "/");
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + modulesGroup.getGroups().size() + " subgroup(s)");
        }

        log.info("Modules group successfully parsed: \nName: " + modulesGroup.getName());
    }

    public Map<String, Capacity> getCapacities() {
        return this.capacities;
    }

    private void setCapacities(Node capacitiesNode) {
        setCapacities(capacitiesNode, null, "/");
    }

    private void setCapacities(Node capacitiesNode, CapacitiesGroup capacitiesGroup, String prefix) {
        if (capacitiesGroup == null) {
            this.getCapacities().clear();
            capacitiesGroup = this.getCapacitiesRoot();
            capacitiesGroup.getCapacities().clear();
            capacitiesGroup.getGroups().clear();
        }

        log.info("Parsing capacities group");
        NodeList capacities = xut.getNodes("Capacity", capacitiesNode);
        if (capacities.getLength() > 0) {
            log.info("Found " + capacities.getLength() + " capacity(ies)");
            for (int i = 0; i < capacities.getLength(); i++) {
                try {
                    Capacity tmpCapacity = new Capacity(capacities.item(i));
                    tmpCapacity.setPath(prefix + tmpCapacity.getName());
                    capacitiesGroup.addCapacity(tmpCapacity.getKey(), tmpCapacity);
                    this.getCapacities().put(tmpCapacity.getKey(), tmpCapacity);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getCapacities().size() + " capacity(ies)");
        }

        NodeList groups = xut.getNodes("Group", capacitiesNode);
        if (groups.getLength() > 0) {
            log.info("Found " + groups.getLength() + " subgroup(s)");
            for (int i = 0; i < groups.getLength(); i++) {
                try {
                    CapacitiesGroup tmpCapacitiesGroup = new CapacitiesGroup(xut.getNode("Name", groups.item(i)), xut.getNode("ShortName", groups.item(i)), xut.getNode("Hint", groups.item(i)), xut.getNode("Hidden", groups.item(i)));
                    capacitiesGroup.addCapacitiesGroup(tmpCapacitiesGroup);
                    this.setCapacities(xut.getNode("Capacities", groups.item(i)), tmpCapacitiesGroup, prefix + tmpCapacitiesGroup.getName() + "/");
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + capacitiesGroup.getGroups().size() + " subgroup(s)");
        }

        log.info("Capacities group successfully parsed: \nName: " + capacitiesGroup.getName());
    }

    public ModulesGroup getModulesRoot() {
        return this.modulesRoot;
    }

    public CapacitiesGroup getCapacitiesRoot() {
        return this.capacitiesRoot;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    private void setTotalWeight() {
        this.totalWeight = 0d;
        for (Module m : this.getModules().values()) {
            this.totalWeight += m.getWeight();
        }
    }
}
