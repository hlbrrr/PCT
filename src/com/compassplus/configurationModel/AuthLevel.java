package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 14.08.12
 * Time: 22:19
 */
public class AuthLevel {
    private String key;
    private String name;
    private String description;
    private String memoHeader;
    private String memoText;
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();
    private Map<String, AuthLevelLevel> levels = new LinkedHashMap<String, AuthLevelLevel>(0);           //LinkedHashMap

    public AuthLevel(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing authority level");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setMemoHeader(xut.getNode("MemoHeader", initialData));
            this.setDescription(xut.getNode("Description", initialData));
            this.setMemoText(xut.getNode("MemoText", initialData));

            this.setLevels(xut.getNodes("Levels/Level", initialData));

            log.info("Authority level successfully parsed: \nName: " + this.getName() +
                    "\nMemoHeader: " + this.getMemoHeader() +
                    "\nKey: " + this.getKey() +
                    "\nMemoText: " + this.getMemoText() +
                    "\nDescription: " + this.getDescription());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level name is not defined correctly", e.getDetails());
        }
    }

    public String getMemoHeader() {
        return memoHeader;
    }

    private void setMemoHeader(Node memoHeader) throws PCTDataFormatException {
        try {
            this.memoHeader = xut.getString(memoHeader, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level memo header is not defined correctly", e.getDetails());
        }
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(Node description) throws PCTDataFormatException {
        try {
            this.description = xut.getString(description, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level description is not defined correctly", e.getDetails());
        }
    }

    public String getMemoText() {
        return memoText;
    }

    private void setMemoText(Node memoText) throws PCTDataFormatException {
        try {
            this.memoText = xut.getString(memoText, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level memo text is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Authority level key is not defined correctly", e.getDetails());
        }
    }

    public Map<String, AuthLevelLevel> getLevels() {
        return levels;
    }

    private void setLevels(NodeList levels) throws PCTDataFormatException {
        this.getLevels().clear();
        if (levels.getLength() > 0) {
            log.info("Found " + levels.getLength() + " authority level(s)");
            for (int i = 0; i < levels.getLength(); i++) {
                try {
                    AuthLevelLevel tmpAuthLevelLevel = new AuthLevelLevel(levels.item(i));
                    this.getLevels().put(tmpAuthLevelLevel.getKey(), tmpAuthLevelLevel);
                } catch (PCTDataFormatException e) {
                    log.error(e);
                }
            }
            log.info("Successfully parsed " + this.getLevels().size() + " authority level levels(s)");
        } else {
            throw new PCTDataFormatException("No authority level levels defined");
        }
        if (this.getLevels().size() == 0) {
            throw new PCTDataFormatException("Authority level levels are not defined correctly");
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
