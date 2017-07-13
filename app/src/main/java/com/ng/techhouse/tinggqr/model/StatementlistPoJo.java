package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 20/02/2017.
 */

public class StatementlistPoJo {

    String Status,Action,Amount,Date,TransactionId;

    public StatementlistPoJo(String status, String action, String amount, String date, String transactionId) {
        Status = status;
        Action = action;
        Amount = amount;
        Date = date;
        TransactionId = transactionId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }
}
