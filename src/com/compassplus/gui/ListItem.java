package com.compassplus.gui;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 4/16/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListItem {
    private String title;
    private String value;
    public ListItem(String title, String value){
        this.title = title;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public String getTitle(){
        return title;
    }

    public String toString(){
        return getTitle();
    }
}
