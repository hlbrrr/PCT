package com.compassplus.proposalModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:44
 */
public class Module {
    private com.compassplus.configurationModel.Module module;
    private String key;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public Module(Node initialData, HashMap<String, com.compassplus.configurationModel.Module> allowedModules) throws PCTDataFormatException {
        init(initialData, allowedModules);
    }

    public Module(com.compassplus.configurationModel.Module module) {
        this.setModule(module);
    }

    private void init(Node initialData, HashMap<String, com.compassplus.configurationModel.Module> allowedModules) throws PCTDataFormatException {
        try {
            log.info("Parsing module");

            this.setKey(xut.getNode("Name", initialData), allowedModules);

            log.info("Module successfully parsed: \nName: " + this.getName());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return this.getModule().getName();
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node name, HashMap<String, com.compassplus.configurationModel.Module> allowedModules) throws PCTDataFormatException {
        try {
            String nameString = xut.getString(name);
            this.key = nameString;
            this.setModule(allowedModules.get(nameString));
            if (this.getModule() == null) {
                throw new PCTDataFormatException("No such module \"" + nameString + "\"");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Module name is not defined correctly", e.getDetails());
        }
    }

    private com.compassplus.configurationModel.Module getModule() {
        return this.module;
    }

    private void setModule(com.compassplus.configurationModel.Module module) {
        this.module = module;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<Module>");
        sb.append("<Name>").append(this.getName()).append("</Name>");
        sb.append("</Module>");
        return sb.toString();
    }
}
