package com.example.hetzi_beta.Accounts;


/*
* A base class for both user accounts and business accounts.
* */
public class Account {
    private String mFirebaseUid;

    public Account(String mFirebaseUid) {
        this.mFirebaseUid = mFirebaseUid;
    }
}
