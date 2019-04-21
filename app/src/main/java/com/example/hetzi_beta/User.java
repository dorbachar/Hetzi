package com.example.hetzi_beta;

public class User {
    private String fbase_uid;
    private boolean got_business_permissions;

    public User(String fbase_uid, boolean got_business_permissions) {
        this.fbase_uid = fbase_uid;
        this.got_business_permissions = got_business_permissions;
    }
}
