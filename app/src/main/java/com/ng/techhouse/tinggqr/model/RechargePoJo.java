package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 27/01/2017.
 */

public class RechargePoJo {

    String BillerId,PaymentItemName,BillerName,PaymentAmount,BillerShortName,PaymentItemCode;




    public RechargePoJo(String billerId, String paymentItemName, String billerName, String paymentAmount, String billerShortName, String paymentItemCode) {
        BillerId = billerId;
        PaymentItemName = paymentItemName;
        BillerName = billerName;
        PaymentAmount = paymentAmount;
        BillerShortName = billerShortName;
        PaymentItemCode = paymentItemCode;
    }

    public String getBillerId() {
        return BillerId;
    }

    public void setBillerId(String billerId) {
        BillerId = billerId;
    }

    public String getPaymentItemName() {
        return PaymentItemName;
    }

    public void setPaymentItemName(String paymentItemName) {
        PaymentItemName = paymentItemName;
    }

    public String getBillerName() {
        return BillerName;
    }

    public void setBillerName(String billerName) {
        BillerName = billerName;
    }

    public String getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String getBillerShortName() {
        return BillerShortName;
    }

    public void setBillerShortName(String billerShortName) {
        BillerShortName = billerShortName;
    }

    public String getPaymentItemCode() {
        return PaymentItemCode;
    }

    public void setPaymentItemCode(String paymentItemCode) {
        PaymentItemCode = paymentItemCode;
    }
}
