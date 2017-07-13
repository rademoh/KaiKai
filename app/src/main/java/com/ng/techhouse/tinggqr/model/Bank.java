package com.ng.techhouse.tinggqr.model;

/**
 * Created by rabiu on 23/01/2017.
 */

public class Bank {

    private String id;
    private String name;
    private String accountno;
    private String accountno_id;

    public Bank(String id, String name) {
        this.id = id;
        this.name = name;
    }
   /* public Bank(String accountno, String accountno_id, String name){
        this.accountno = accountno;
        this.accountno_id = accountno_id;
        this.name = name;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Bank){
            Bank b = (Bank )obj;
            if(b.getName().equals(name) && b.getId()==id ) return true;
        }

        return false;
    }
}


