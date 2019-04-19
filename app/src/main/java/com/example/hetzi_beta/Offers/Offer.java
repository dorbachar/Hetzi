package com.example.hetzi_beta.Offers;

/*
* Offer-
* A simple, pretty straight-forward class built to contain an offer's details as specified in the
* form. Details may vary later along the way.
*
* */

import android.os.Parcel;
import android.os.Parcelable;

public class Offer implements Parcelable {
    private String      title;
    private String      photo_url;
    private Integer     quantity;
    private Float       orig_price;
    private Integer     discount;
    private Integer     time_in_secs; // TODO : TIME OVERHAUL

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(title);
        dest.writeString(photo_url);
        dest.writeInt(quantity);
        dest.writeFloat(orig_price);
        dest.writeInt(discount);
        dest.writeInt(time_in_secs);
    }

    public Offer(Parcel in) {
        title = in.readString();
        photo_url = in.readString();
        quantity = in.readInt();
        orig_price = in.readFloat();
        discount = in.readInt();
        time_in_secs = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    public Offer() {

    }

    public Offer(String title, String photo_url, Integer quantity, Float orig_price, Integer discount, Integer time_in_secs) {
        this.title = title;
        this.photo_url = photo_url;
        this.quantity = quantity;
        this.orig_price = orig_price;
        this.discount = discount;
        this.time_in_secs = time_in_secs;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return this.photo_url;
    }

    public void setPhotoUrl(String photo_url) {
        this.photo_url = photo_url;
    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Integer getQuantity() { return this.quantity; }

    public void setOrigPrice(Float orig_price) { this.orig_price = orig_price; }

    public Float getOrigPrice() { return this.orig_price; }

    public void setDiscount(int discount) { this.discount = discount; }

    public Integer getDiscount() { return this.discount; }

    public Integer getTimeInSecs() {
        return this.time_in_secs;
    }

    public void setTimeInSecs(Integer time_in_secs) {
        this.time_in_secs = time_in_secs;
    }
    }
