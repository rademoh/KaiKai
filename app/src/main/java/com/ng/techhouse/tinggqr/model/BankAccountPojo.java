package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 13/06/2017.
 */

public class BankAccountPojo {

    String accountNo,bankCode;

    public BankAccountPojo(String accountNo, String bankCode) {
        this.accountNo = accountNo;
        this.bankCode = bankCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

}
