package com.example.hetzi_beta.Accounts;


/*
* A base class for both user accounts and business accounts.
* Maybe do 2 inheriting HtzAccount type if the member fields will differ much.
* Assuming a user's preferences will be saved here (allgeries, etc).
*
* */
public class HtzAccount {
    private String mFirebaseUid;

    public HtzAccount(String mFirebaseUid) {
        this.mFirebaseUid = mFirebaseUid;
    }
}
