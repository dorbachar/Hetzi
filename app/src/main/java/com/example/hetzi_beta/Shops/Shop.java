package com.example.hetzi_beta.Shops;

import android.net.Uri;

public class Shop {
    private String shop_name;
    private String firebase_user_uid;
    private String logo_uri_asString;
    private String cover_photo_uri_asString;
    private String website;
    private String physical_address; // TODO : - ON USER SIDE - add location on google maps (for now, and in the future open out map)
    private String phone_number;
    private String instagram_uri;
    private String facebook_uri;

    public Shop() {

    }

    public Shop(String shop_name, String firebase_uid, String logo, String cover_photo, String website, String address, String phone_number) {
        this.shop_name                  = shop_name;
        this.firebase_user_uid          = firebase_uid;
        this.logo_uri_asString          = logo;
        this.cover_photo_uri_asString   = cover_photo;
        this.website                    = website;
        this.physical_address           = address;
        this.phone_number               = phone_number;
    }


    // ~~~~~~~~~ Sets & Gets ~~~~~~~~~ //
    public String   getShopName() {
        return this.shop_name;
    }
    public void     setShopName(String name) {
        this.shop_name = name;
    }

    public String   getShopUserUid() {
        return this.firebase_user_uid;
    }

    public String      getLogoUri() {
        return this.logo_uri_asString;
    }
    public void     setLogoUri(String new_uri) {
        this.logo_uri_asString = new_uri;
    }

    public String      getCoverPhotoUri() {
        return this.cover_photo_uri_asString;
    }
    public void     setCoverPhotoUri(String new_uri) {
        this.cover_photo_uri_asString = new_uri;
    }

    public String   getWebsite() {
        return this.website;
    }
    public void     setWebsite(String website) {
        this.website = website;
    }

    public void     setPhysicalAddress(String physical_address) {
        this.physical_address = physical_address;
    }
    public String   getPhysicalAddress() {
        return this.physical_address;
    }

    public String   getPhone() {
        return this.phone_number;
    }
    public void     setPhone(String phone) {
        this.phone_number = phone;
    }


}
