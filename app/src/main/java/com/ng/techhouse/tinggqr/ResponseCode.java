package com.ng.techhouse.tinggqr;

/**
 * Created by rabiu on 16/01/2017.
 */

public class ResponseCode {


    public String getResponseMessage(String code) {
        String message;
        switch (code) {
            case "37":
                message = "Biller Item Not Found ";
                break;
            case "30":
                message = "Processing";
                break;
            case "00":
                message = "Successful";
                break;
            case "05":
                message = "Your PIN is incorrect";
                break;
            case "07":
                message = "Account Invalid";
                break;
            case "21":
                message = "SmartCard Incorrect";
                break;
            case "22":
                message = "Customer ID is Null";
                break;
            case "28":
                message = "Biller Not Found";
                break;
            case "29":
                message = "Payment Item Not Found";
                break;
            case "23":
                message = "Bouquet Invalid";
                break;
            case "24":
                message = "Insufficient Fund";
                break;
            case "25":
                message = "Unknown Error";
                break;
            case "26":
                message = "Account Suspended";
                break;
            case "27":
                message = "Transfer Limit Exceeded";
                break;
            case "32":
                message = "Unauthorized";
                break;
            case "33":
                message = "Authentication Required";
                break;
            case "34":
                message = "Accepted";
                break;
            case "31":
                message = "Service Unavailable";
                break;
            case "35":
                message = "Invalid Amount";
                break;
            case "36":
                message = "Source Account Not Registered";
                break;
            case "38":
                message = "Incomplete Param";
                break;
            case "39":
                message = "Invalid Token";
                break;
            case "40":
                message = "Phone No Already Registered";
                break;
            case "41":
                message = "Duplicate Ref";
                break;
            case "42":
                message = "Activation Required";
                break;
            case "43":
                message = "Invalid Security Answer";
                break;
            case "44":
                message = "Hierachy Limit Exceeded";
                break;
            case "45":
                message = "Merchant Account Not Registered";
                break;
            case "46":
                message = "Receiver Account Not Registered";
                break;
            case "47":
                message = "Invalid Debit Account";
                break;
            case "48":
                message = "Invalid Quantity";
                break;
            case "49":
                message = "Technical Error";
                break;
            case "50":
                message = "Card link Successful";
                break;
            case "51":
                message = "Invalid Card";
                break;
            case "53":
                message = "Card Already Used";
                break;
            default:
                throw new IllegalArgumentException("Invalid Message");
        }
        return message;
    }
}
