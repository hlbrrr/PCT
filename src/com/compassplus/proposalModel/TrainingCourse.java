package com.compassplus.proposalModel;

import com.compassplus.configurationModel.*;
import com.compassplus.exception.PCTDataFormatException;
import com.compassplus.utils.CommonUtils;
import com.compassplus.utils.Logger;
import com.compassplus.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/12/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrainingCourse {
    private Logger log = Logger.getInstance();
    private XMLUtils xut = XMLUtils.getInstance();

    private com.compassplus.configurationModel.TrainingCourse trainingCourse = null;

    private ArrayList<String> subscriptions = new ArrayList<String>(0);

    private Integer attendees = 0;
    private boolean include = true;

    private Proposal proposal = null;
    private boolean userDefined = false;

    public TrainingCourse(Node initialData, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        try {
            log.info("Parsing training course");

            this.setTrainingCourse(xut.getNode("Key", initialData), proposal.getConfig());

            this.setAttendees(xut.getNode("Attendees", initialData));
            this.setInclude(xut.getNode("Include", initialData));
            this.setUserDefined(xut.getNode("UserDefined", initialData));

            log.info("Training course successfully parsed: \nName: " + this.getKey());
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course is not defined correctly", e.getDetails());
        }
    }

    public TrainingCourse(com.compassplus.configurationModel.TrainingCourse trainingCourse, Proposal proposal) throws PCTDataFormatException {
        this.proposal = proposal;
        setInclude(trainingCourse.isMandatory());
        setTrainingCourse(trainingCourse.getKey(), proposal.getConfig());
    }

    public boolean isUnused(){
        return !isUserDefined() && subscriptions.size() == 0;
    }

    public void addSubscription(String key){
        subscriptions.add(key);
    }

    public void removeSubscription(String key){
        subscriptions.remove(key);
    }

    public com.compassplus.configurationModel.TrainingCourse getTrainingCourse() {
        return this.trainingCourse;
    }


    private void setTrainingCourse(String key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = key;
            trainingCourse = config.getTrainingCourses().get(keyString);
            if (trainingCourse == null) {
                throw new PCTDataFormatException("No such training course");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course key is not defined correctly", e.getDetails());
        }
    }

    private void setTrainingCourse(Node key, Configuration config) throws PCTDataFormatException {
        try {
            String keyString = xut.getString(key);
            trainingCourse = config.getTrainingCourses().get(keyString);
            if (trainingCourse == null) {
                System.out.println("zzz="+keyString);
                throw new PCTDataFormatException("No such training course");
            }
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course key is not defined correctly", e.getDetails());
        }
    }

    public Boolean getInclude() {
        return this.include;
    }


    public void setInclude(boolean include){
        this.include = include;
    }

    private void setInclude(Node include) throws PCTDataFormatException {
        try {
            this.include = xut.getBoolean(include);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course include is not defined correctly", e.getDetails());
        }
    }

    private void setUserDefined(Node userDefined) throws PCTDataFormatException {
        try {
            this.userDefined = xut.getBoolean(userDefined);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course userDefined is not defined correctly", e.getDetails());
        }
    }

    public Integer getAttendees() {
        if(this.attendees > trainingCourse.getMaxAttendees()){
            return trainingCourse.getMaxAttendees();
        }else if(this.attendees < trainingCourse.getMinAttendees()){
            return trainingCourse.getMinAttendees();
        }
        return this.attendees;
    }

    private void setAttendees(Node attendees) throws PCTDataFormatException {
        try {
            this.attendees = xut.getInteger(attendees);
        } catch (PCTDataFormatException e) {
            throw new PCTDataFormatException("Training course attendees is not defined correctly", e.getDetails());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<TrainingCourse>");
        sb.append("<Key>").append(getKey()).append("</Key>");
        sb.append("<Include>").append(getInclude()).append("</Include>");
        sb.append("<UserDefined>").append(isUserDefined()).append("</UserDefined>");
        sb.append("<Attendees>").append(getAttendees()).append("</Attendees>");
        sb.append("</TrainingCourse>");
        return sb.toString();
    }


    public String getKey() {
        return this.trainingCourse.getKey();
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setAttendees(int val) {
        this.attendees = val;
    }

    public Double getRegionalPrice() {
        return getCleanPrice();
    }

    public Double getCleanPrice() {
        Double ret = 0d;
        if(getInclude()){
            ret += getAttendees() * getTrainingCourse().getPrice();
            ret = proposal.getCurrencyRate() * ret;
        }
        return ret;//CommonUtils.getInstance().toNextHundred(ret);
    }

    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }

    public boolean isUserDefined() {
        return userDefined;
    }

    public Double getPricePerAttendee() {
        Double ret = 0d;
        ret += getTrainingCourse().getPrice();
        ret = proposal.getCurrencyRate() * ret;
        return ret;
    }

    public boolean isRemovable() {
        return isUserDefined() && subscriptions.size() == 0;
    }
}
