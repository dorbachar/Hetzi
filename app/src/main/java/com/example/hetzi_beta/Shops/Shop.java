package com.example.hetzi_beta.Shops;

public class Shop {
    private String name;
    private String fb_uid; // Firebase uid
    private String logo_uri;
    private String cover_uri;
    private String website;
    private String address; // TODO : - ON USER SIDE - add location on google maps (for now, and in the future open out map)
    private String phone;
    private String insta;
    private String facebook;

    public Shop() {
    }

    public Shop(String shop_name, String firebase_uid, String logo, String cover_photo, String website, String address, String phone_number) {
        this.name           = shop_name;
        this.fb_uid         = firebase_uid;
        this.logo_uri       = logo;
        this.cover_uri      = cover_photo;
        this.website        = website;
        this.address        = address;
        this.phone          = phone_number;
    }


    // ~~~~~~~~~ Sets & Gets ~~~~~~~~~ //
    public String   getShopName() {
        return this.name;
    }
    public void     setShopName(String name) {
        this.name = name;
    }

    public String   getShopUserUid() {
        return this.fb_uid;
    }

    public String      getLogoUri() {
        return this.logo_uri;
    }
    public void     setLogoUri(String new_uri) {
        this.logo_uri = new_uri;
    }

    public String      getCoverPhotoUri() {
        return this.cover_uri;
    }
    public void     setCoverPhotoUri(String new_uri) {
        this.cover_uri = new_uri;
    }

    public String   getWebsite() {
        return this.website;
    }
    public void     setWebsite(String website) {
        this.website = website;
    }

    public void     setPhysicalAddress(String physical_address) {
        this.address = physical_address;
    }
    public String   getPhysicalAddress() {
        return this.address;
    }

    public String   getPhone() {
        return this.phone;
    }
    public void     setPhone(String phone) {
        this.phone = phone;
    }

    public String   getFacebookUri() {
        return this.facebook;
    }
    public void     setFacebookUri(String facebook_uri) {
        this.facebook = facebook_uri;
    }

    public String   getInstagramUri() {
        return this.insta;
    }
    public void     setInstagramUri(String instagram_uri) {
        this.insta = instagram_uri;
    }


}
