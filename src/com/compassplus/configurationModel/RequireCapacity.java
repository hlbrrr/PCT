package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 06.11.11
 * Time: 0:13
 */
public class RequireCapacity {
    private String key;
    private Boolean incremental;
    private Boolean freeOfCharge;
    private Integer value;
    private XMLUtils xut = XMLUtils.getInstance();

    public RequireCapacity(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        this.setKey(initialData);
        this.setIncremental((initialData != null && initialData.getAttributes() != null) ? initialData.getAttributes().getNamedItem("incremental") : null);
        this.setFreeOfCharge((initialData != null && initialData.getAttributes() != null) ? initialData.getAttributes().getNamedItem("freeofcharge") : null);
        this.setValue((initialData != null && initialData.getAttributes() != null) ? initialData.getAttributes().getNamedItem("value") : null);
    }

    public String getKey() {
        return this.key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        this.key = xut.getString(key);
    }

    private void setIncremental(Node incremental) throws PCTDataFormatException {
        this.incremental = xut.getBoolean(incremental, true);
    }

    public Boolean isIncremental() {
        return this.incremental != null ? this.incremental : false;
    }

    private void setFreeOfCharge(Node freeOfCharge) throws PCTDataFormatException {
        this.freeOfCharge = xut.getBoolean(freeOfCharge, true);
    }

    public Boolean isFreeOfCharge() {
        return this.freeOfCharge != null ? this.freeOfCharge : false;
    }

    public Integer getValue() {
        return value;
    }

    private void setValue(Node value) throws PCTDataFormatException {
        this.value = xut.getInteger(value);
    }

}
