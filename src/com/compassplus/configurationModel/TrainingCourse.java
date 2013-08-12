package com.compassplus.configurationModel;

import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 11/19/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrainingCourse {
    private String key;
    private String name;
    private String hint;
    private String trainingCourseKey;

    private Boolean mandatory;
    private Integer minAttendees;
    private Integer maxAttendees;
    private double length;
    private double price;

    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    public TrainingCourse(Node initialData) throws PCTDataFormatException {
        init(initialData);
    }

    private void init(Node initialData) throws PCTDataFormatException {
        try {
            log.info("Parsing training course");

            this.setKey(xut.getNode("Key", initialData));
            this.setName(xut.getNode("Name", initialData));
            this.setHint(xut.getNode("Hint", initialData));

            this.setTrainingCourseKey(xut.getNode("TrainingCourseKey", initialData));
            this.setMandatory(xut.getNode("Mandatory", initialData));
            this.setMinAttendees(xut.getNode("MinAttendees", initialData));
            this.setMaxAttendees(xut.getNode("MaxAttendees", initialData));
            this.setLength(xut.getNode("Length", initialData));
            this.setPrice(xut.getNode("Price", initialData));

            log.info("Training course successfully parsed: \nName: " + this.getName() +
                    "\nKey: " + this.getKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course is not defined correctly: \nName: " + this.getName(), e.getDetails());
        }
    }

    public String getTrainingCourseKey() {
        return trainingCourseKey;
    }

    private void setTrainingCourseKey(Node trainingCourseKey) throws PCTDataFormatException {
        try {
            this.trainingCourseKey = xut.getString(trainingCourseKey, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course trainingCourseKey is not defined correctly", e.getDetails());
        }
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    private void setMaxAttendees(Node maxAttendees) throws PCTDataFormatException {
        try {
            this.maxAttendees = xut.getInteger(maxAttendees);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course maxAttendees is not defined correctly", e.getDetails());
        }
    }

    public Integer getMinAttendees() {
        return minAttendees;
    }

    private void setMinAttendees(Node minAttendees) throws PCTDataFormatException {
        try {
            this.minAttendees = xut.getInteger(minAttendees);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course minAttendees is not defined correctly", e.getDetails());
        }
    }

    public Boolean isMandatory() {
        return this.mandatory != null ? this.mandatory : false;
    }

    private void setMandatory(Node mandatory) throws PCTDataFormatException {
        try {
            this.mandatory = xut.getBoolean(mandatory, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course mandatory-flag is not defined correctly", e.getDetails());
        }
    }

    public String getHint() {
        return hint;
    }

    private void setHint(Node hint) throws PCTDataFormatException {
        try {
            this.hint = xut.getString(hint, true);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course hint is not defined correctly", e.getDetails());
        }
    }

    public Double getLength() {
        return length;
    }

    private void setLength(Node length) throws PCTDataFormatException {
        try {
            this.length = xut.getDouble(length);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course length is not defined correctly", e.getDetails());
        }
    }

    public Double getPrice() {
        return price;
    }

    private void setPrice(Node price) throws PCTDataFormatException {
        try {
            this.price = xut.getDouble(price);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course price is not defined correctly", e.getDetails());
        }
    }

    public String getKey() {
        return key;
    }

    private void setKey(Node key) throws PCTDataFormatException {
        try {
            this.key = xut.getString(key);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course key is not defined correctly", e.getDetails());
        }
    }

    public String getName() {
        return name;
    }

    private void setName(Node name) throws PCTDataFormatException {
        try {
            this.name = xut.getString(name);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course name is not defined correctly", e.getDetails());
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
