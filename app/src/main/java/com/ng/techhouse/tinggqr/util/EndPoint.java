package com.ng.techhouse.tinggqr.util;

/**
 * Created by rabiu on 27/02/2017.
 */

public class EndPoint {


    public String getLogin() {

        String strURL = "";

        for(int c : AppConstant.baseStation)
            strURL +=(char)c;

        return strURL;
    }
    public String getlogin() {
        String strURL = "";
        for(int c : AppConstant.Login)
            strURL +=(char)c;
        return strURL;}
    public String getPurchase() {
        String strURL = "";
        for(int c : AppConstant.PurchaseAirtime)
            strURL +=(char)c;
        return strURL;}
    public String getDataOps() {
        String strURL = "";
        for(int c : AppConstant.DataOps)
            strURL +=(char)c;
        return strURL;}
    public String getBillerOps() {
        String strURL = "";
        for(int c : AppConstant.BillerOps)
            strURL +=(char)c;
        return strURL;}
    public String getNameEnquiry() {
        String strURL = "";
        for(int c : AppConstant.NameEnquiry)
            strURL +=(char)c;
        return strURL;}
    public String getSendToBank() {
        String strURL = "";
        for(int c : AppConstant.SendToBank)
            strURL +=(char)c;
        return strURL;}
    public String getRegCustomer() {
        String strURL = "";
        for(int c : AppConstant.RegisterCustomer)
            strURL +=(char)c;
        return strURL;}
    public String getSendMoney() {
        String strURL = "";
        for(int c : AppConstant.SendMoney)
            strURL +=(char)c;
        return strURL;}
    public String getSavedCardDetails() {
        String strURL = "";
        for(int c : AppConstant.SaveCardDetails)
            strURL +=(char)c;
        return strURL;}
    public String getChangePin() {
        String strURL = "";
        for(int c : AppConstant.ChangePin)
            strURL +=(char)c;
        return strURL;}
    public String getProfileNoCheck() {
        String strURL = "";
        for(int c : AppConstant.ProfileNoCheck)
            strURL +=(char)c;
        return strURL;}
    public String getHttp() {
        String strURL = "";
        for(int c : AppConstant.http)
            strURL +=(char)c;
        return strURL;}
   public String getHttps() {
        String strURL = "";
        for(int c : AppConstant.https)
            strURL +=(char)c;
        return strURL;}
    public String getTinggPay() {
        String strURL = "";
        for(int c : AppConstant.tinggPay)
            strURL +=(char)c;
        return strURL;}
    public String getGateWay() {
        String strURL = "";
        for(int c : AppConstant.gateway2)
            strURL +=(char)c;
        return strURL;}
    public String getLordaragorn() {
        String strURL = "";
        for(int c : AppConstant.lordaragorn)
            strURL +=(char)c;
        return strURL;}
    public String getSmartWalet() {
        String strURL = "";
        for(int c : AppConstant.smartwallet)
            strURL +=(char)c;
        return strURL;}
    public String getIpSmartWallet() {
        String strURL = "";
        for(int c : AppConstant.ipSmartWallet)
            strURL +=(char)c;
        return strURL;}
    public String getSeedValue() {
        String strURL = "";
        for(int c : AppConstant.SEEDVALUE)
            strURL +=(char)c;
        return strURL;}
    public String getIpDomain(){
        String strURL = "";
        for(int c : AppConstant.ipDomain)
            strURL +=(char)c;
        return strURL;}

    public String getMigsUrl(){
        String strURL = "";
        for(int c : AppConstant.migsUrl)
            strURL +=(char)c;
        return strURL;
    }

}
