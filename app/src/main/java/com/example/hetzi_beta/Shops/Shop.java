package com.example.hetzi_beta.Shops;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.hetzi_beta.Utils.HtzAddress;
import com.example.hetzi_beta.Utils.Utils;

import static com.example.hetzi_beta.Utils.Utils.HTZ_INVALID_DISTANCE;
import static com.example.hetzi_beta.Utils.Utils.HTZ_LOCATION_NOT_FOUND;

public class Shop implements Parcelable {
    private String name;
    private String uid;     // Firebase connected user uid
    private String fbKey;   // Firebase unique random key
    private String logo_uri;
    private String cover_uri;
    private String website;
    private Double lon;
    private Double lat;
    private String phone;
    private String insta;
    private String facebook;

    public Shop() {

    }

    public Shop(String shop_name, String firebase_uid, String logo, String cover_photo, String website, String phone_number) {
        this.name           = shop_name;
        this.uid            = firebase_uid;
        this.logo_uri       = logo;
        this.cover_uri      = cover_photo;
        this.website        = website;
        this.phone          = phone_number;

        HtzAddress shop_full_address = Utils.SHOP_ADDRESS.get(shop_name);
        this.lat            = shop_full_address.getLatitude();
        this.lon            = shop_full_address.getLongtitude();
    }


    // ~~~~~~~~~ Sets & Gets ~~~~~~~~~ //
    public String   getShopName() {
        return this.name;
    }
    public void     setShopName(String name) {
        this.name = name;
    }

    public String   getLogoUri() {
        return this.logo_uri;
    }
    public void     setLogoUri(String new_uri) {
        this.logo_uri = new_uri;
    }

    public String   getCoverPhotoUri() {
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

    public Double   getLon() {
        return lon;
    }
    public void     setLon(Double lon) {
        this.lon = lon;
    }

    public Double   getLat() {
        return lat;
    }
    public void     setLat(Double lat) {
        this.lat = lat;
    }


    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFbKey() {
        return fbKey;
    }

    public void setFbKey(String fbKey) {
        this.fbKey = fbKey;
    }

    public Float calcDistanceFromUser() {
        float[] res = new float[1];

        if (Utils.user_lat == HTZ_LOCATION_NOT_FOUND || Utils.user_lon == HTZ_LOCATION_NOT_FOUND) {
            return HTZ_INVALID_DISTANCE;
        }

        Location.distanceBetween(Utils.user_lat, Utils.user_lon, lat, lon, res);

        return Utils.round(res[0] / 1000,1);
    }

    // ------------------------- Parcelable ------------------------- //

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uid);
        dest.writeString(fbKey);
        dest.writeString(logo_uri);
        dest.writeString(cover_uri);
        dest.writeString(website);
        dest.writeDouble(lon);
        dest.writeDouble(lat);
        dest.writeString(phone);
        dest.writeString(insta);
        dest.writeString(facebook);
    }

    public Shop(Parcel in) {
        name        = in.readString();
        uid = in.readString();
        fbKey       = in.readString();
        logo_uri    = in.readString();
        cover_uri   = in.readString();
        website     = in.readString();
        lon         = in.readDouble();
        lat         = in.readDouble();
        phone       = in.readString();
        insta       = in.readString();
        facebook    = in.readString();
    }

}
