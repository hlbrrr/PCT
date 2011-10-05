package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/5/11
 * Time: 9:22 AM
 */
public class ModulesGroup {
    private String name;

    private ArrayList<Module> modules = new ArrayList<Module>();
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

    public void addModule(Module module) {
        this.modules.add(module);
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

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public String toString() {
        return this.toString("");
    }

    public String toString(String pad) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(this.getName()).append(":\n");
        for (Module m : this.getModules()) {
            sb.append(pad).append("  -").append(m.getName()).append("\n");
        }
        for (ModulesGroup mg : this.getGroups()) {
            sb.append(mg.toString(pad + "  "));
        }
        String ret = sb.toString();
        if(ret.endsWith("\n")){
            ret = ret.substring(0, ret.length()-1);
        }
        return ret;
    }
}
