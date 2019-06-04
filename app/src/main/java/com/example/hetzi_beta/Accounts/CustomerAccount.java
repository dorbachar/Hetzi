package com.example.hetzi_beta.Accounts;

public class CustomerAccount {
    private String uid;
    private String display_name;

    public CustomerAccount(String uid, String display_name) {
        this.uid            = uid;
        this.display_name   = display_name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
