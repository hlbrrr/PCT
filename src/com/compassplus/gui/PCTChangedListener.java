package com.compassplus.gui;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/18/11
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PCTChangedListener {
    void act(Object src);
    void setData(String key, Object data);
    Object getData(String key);
}
