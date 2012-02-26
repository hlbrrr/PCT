package com.compassplus.utils;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 9/29/11
 * Time: 1:15 PM
 */
public class Logger {
    private static Logger ourInstance = new Logger();

    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
    }

    public void info(String message) {
        System.out.println("[I] " + message);
    }

    public void error(Exception exception) {
        this.error(exception.getMessage());
    }

    public void error(String message) {
        System.out.println("[E] " + message);
    }
}
