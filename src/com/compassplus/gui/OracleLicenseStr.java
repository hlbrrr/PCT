package com.compassplus.gui;

import com.compassplus.proposalModel.OracleLicense;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 10/29/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class OracleLicenseStr {
    private OracleLicense license;
    public OracleLicenseStr(OracleLicense license){
        this.license = license;
    }

    public String toString(){
        return license.getProduct().getName() + " Oracle Box";
    }

    public OracleLicense getLicense(){
        return license;
    }
}
