package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 03/04/2017.
 */

public class AirtimeBeneficiaryPojo {

    String name, phoneno;

    public AirtimeBeneficiaryPojo(String name, String phoneno) {
        this.name = name;
        this.phoneno = phoneno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}
