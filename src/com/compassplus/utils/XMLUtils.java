package com.compassplus.utils;

import com.compassplus.exception.PCTDataFormatException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 2:13 PM
 */
public class XMLUtils {
    private static XMLUtils ourInstance = new XMLUtils();
    private XPath xPath = XPathFactory.newInstance().newXPath();

    public static XMLUtils getInstance() {
        return ourInstance;
    }

    private XMLUtils() {
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

    public Boolean getBoolean(Node node) throws PCTDataFormatException {
        return getBoolean(node, false);
    }

    public Boolean getBoolean(Node node, boolean allowEmptyString) throws PCTDataFormatException {
        String stringValue = this.getString(node, allowEmptyString);

        if (stringValue.equals("true")) {
            return true;
        } else if (stringValue.equals("false") || stringValue.equals("")) {
            return false;
        } else {
            throw new PCTDataFormatException("Node value is not valid boolean");
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

    public NodeList getNodes(String xPath, Object src) {
        try {
            return (NodeList) this.xPath.evaluate(xPath, src, XPathConstants.NODESET);
        } catch (Exception e) {
            return null;
        }
    }

    public Node getNode(String xPath, Object src) {
        try {
            return (Node) this.xPath.evaluate(xPath, src, XPathConstants.NODE);
        } catch (Exception e) {
            return null;
        }
    }
}
