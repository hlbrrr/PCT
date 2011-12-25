package com.compassplus.gui;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 25.12.11
 * Time: 18:30
 */
public class XLSTemplate {
    private String fullName;
    private String nameToShow;
    private String format;

    public XLSTemplate(String fullName, String nameToShow) {
        this.fullName = fullName;
        if (nameToShow.endsWith(".xls")) {
            this.nameToShow = nameToShow.substring(0, nameToShow.length() - 4);
            format = ".xls";
        } else if (nameToShow.endsWith(".xlsx")) {
            this.nameToShow = nameToShow.substring(0, nameToShow.length() - 5);
            format = ".xlsx";
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String toString() {
        return nameToShow;
    }

    public String formatDestination(String destination) {
        if (!destination.endsWith(format)) {
            if (destination.endsWith(".xls")) {
                destination = destination.substring(0, destination.length() - 4);
            } else if (destination.endsWith(".xlsx")) {
                destination = destination.substring(0, destination.length() - 5);
            }
            destination += format;
        }
        return destination;
    }
}
