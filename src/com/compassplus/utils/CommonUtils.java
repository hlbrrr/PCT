package com.compassplus.utils;

import com.compassplus.exception.PCTDataFormatException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/30/11
 * Time: 10:19 AM
 */
public class CommonUtils {
    private static CommonUtils ourInstance = new CommonUtils();
    private static DecimalFormat formatDouble = new DecimalFormat("#");
    private static final char[] kDigits = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static CommonUtils getInstance() {
        return ourInstance;
    }

    private CommonUtils() {
    }

    private Document getDocumentFromSource(Reader source) throws PCTDataFormatException {
        try {
            Document doc;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(null);
            doc = db.parse(new InputSource(source));
            doc.normalize();
            return doc;
        } catch (Exception e) {
            throw new PCTDataFormatException("Source parsing error");
        }
    }

    public Document getDocumentFromString(String string) throws PCTDataFormatException {
        return getDocumentFromSource(new StringReader(string));
    }

    public Document getDocumentFromFile(String fileName) throws PCTDataFormatException {
        try {
            return getDocumentFromSource(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new PCTDataFormatException("File not found");
        }
    }

    public Document getDocumentFromFile(File file) throws PCTDataFormatException {
        try {
            return getDocumentFromSource(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new PCTDataFormatException("File not found");
        }
    }

    public Double toNextThousand(Double val) {
        return Math.ceil(val / 1000) * 1000;
    }

    public Double toNextInt(Double val) {
        return Math.ceil(val);
    }

    public String formatDouble(Double val) {
        return formatDouble.format(val);
    }

    public String md5(String md5, String encoding) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes(encoding));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }
}
