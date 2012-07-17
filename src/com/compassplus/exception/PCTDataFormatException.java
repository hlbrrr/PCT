package com.compassplus.exception;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 11:31 AM
 */
public class PCTDataFormatException extends Exception {
    private static final String appender = "Bad data format: ";
    private static final String separator = " <- ";

    private LinkedList<String> messages = new LinkedList<String>();

    @Override
    public String getMessage() {
        StringBuilder result = new StringBuilder(appender);
        boolean first = true;
        for (String msg : messages) {
            if (!first || (first = false)) {
                result.append(separator);
            }
            result.append(msg);
        }
        return result.toString();
    }

    public String getCleanMessage() {
        return messages.getFirst();
    }

    public void addDetails(String message) {
        this.messages.add(message);
    }

    public void addDetails(LinkedList<String> messages) {
        this.messages.addAll(messages);
    }

    public LinkedList<String> getDetails() {
        return messages;
    }

    /*public PCTDataFormatException() {
        super();
    }*/

    public PCTDataFormatException(String message) {
        super();
        this.addDetails(message);
    }

    public PCTDataFormatException(String message, LinkedList<String> messages) {
        super();
        this.addDetails(message);
        this.addDetails(messages);
    }

    /*public PCTDataFormatException(String message, Throwable cause) {
        super(cause);
        this.addDetails(message);
    }

    public PCTDataFormatException(Throwable cause) {
        super(cause);
    }*/
}
