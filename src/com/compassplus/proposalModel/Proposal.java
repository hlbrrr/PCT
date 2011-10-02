package com.compassplus.proposalModel;

import com.compassplus.configurationModel.PCTManager;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 02.10.11
 * Time: 18:39
 */
public class Proposal {
    private PCTManager manager;
    private String clientName;
    private ArrayList<Product> products;

    public Proposal(PCTManager manager) {
        this.manager = manager;
    }

}
