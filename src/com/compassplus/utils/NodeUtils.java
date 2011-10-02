package com.compassplus.utils;

import com.compassplus.exception.PCTDataFormatException;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 2:13 PM
 */
public class NodeUtils {
    private static NodeUtils ourInstance = new NodeUtils();

    public static NodeUtils getInstance() {
        return ourInstance;
    }

    private NodeUtils() {
    }

    public String getString(Node node) throws PCTDataFormatException {
        return getString(node, false);
    }

    public String getString(Node node, boolean allowEmptyString) throws PCTDataFormatException {
        String errorMessage;
        if (node != null) {
            if (node.getTextContent() != null) {
                if (!node.getTextContent().equals("")) {
                    return node.getTextContent();
                } else {
                    errorMessage = "Node value is empty";
                }
            } else {
                errorMessage = "Node value is null";
            }
        } else {
            errorMessage = "Node is null";
        }
        if (allowEmptyString) {
            return "";
        } else {
            throw new PCTDataFormatException(errorMessage);
        }
    }

    public Double getDouble(Node node) throws PCTDataFormatException {
        return getDouble(node, false);
    }

    public Double getDouble(Node node, boolean allowEmptyString) throws PCTDataFormatException {
        String stringValue = this.getString(node, allowEmptyString);

        if (stringValue.equals("")) {
            return null;
        } else {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                throw new PCTDataFormatException("Node value is not valid double");
            }
        }

    }

    public Integer getInteger(Node node) throws PCTDataFormatException {
        return getInteger(node, false);
    }

    public Integer getInteger(Node node, boolean allowEmptyString) throws PCTDataFormatException {
        String stringValue = this.getString(node, allowEmptyString);

        if (stringValue.equals("")) {
            return null;
        } else {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                throw new PCTDataFormatException("Node value is not valid double");
            }
        }

    }
}
