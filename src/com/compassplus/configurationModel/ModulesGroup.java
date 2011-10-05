package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/5/11
 * Time: 9:22 AM
 */
public class ModulesGroup {
    private String name;

    private HashMap<String, Module> modules = new HashMap<String, Module>();
    private ArrayList<ModulesGroup> groups = new ArrayList<ModulesGroup>();
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public ModulesGroup(String name) {
        this.name = name;
    }

    public ModulesGroup(Node name) throws PCTDataFormatException {
        this.setName(name);
    }

    public String getName() {
        return this.name;
    }

    public void addModule(String key, Module module) {
        this.modules.put(key, module);
    }

    public void addModulesGroup(ModulesGroup modulesGroup) {
        this.groups.add(modulesGroup);
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Modules group name is not defined correctly", e.getDetails());
        }
    }

    public ArrayList<ModulesGroup> getGroups() {
        return this.groups;
    }

    public HashMap<String, Module> getModules() {
        return this.modules;
    }

    public String toString() {
        String ret = this.toString("");
        return ret.endsWith("\n") ? ret.substring(0, ret.length() - 1) : ret;
    }

    public String toString(String pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(this.getName()).append(":\n");
        for (Module m : this.getModules().values()) {
            sb.append(pad).append("  -").append(m.getName()).append("\n");
        }
        for (ModulesGroup mg : this.getGroups()) {
            sb.append(mg.toString(pad + "  "));
        }
        return sb.toString();
    }
}
