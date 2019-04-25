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
    private Integer     start_day;
    private Integer     start_month;
    private Integer     start_year;
    private Integer     start_hour;
    private Integer     start_minute;

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
        dest.writeInt(start_day);
        dest.writeInt(start_month);
        dest.writeInt(start_year);
        dest.writeInt(start_hour);
        dest.writeInt(start_minute);
    }

    public Offer(Parcel in) {
        title           = in.readString();
        photo_url       = in.readString();
        quantity        = in.readInt();
        orig_price      = in.readFloat();
        discount        = in.readInt();
        time_in_secs    = in.readInt();
        start_day       = in.readInt();
        start_month     = in.readInt();
        start_year      = in.readInt();
        start_hour      = in.readInt();
        start_minute    = in.readInt();
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

    public Offer(String title, String photo_url, Integer quantity, Float orig_price, Integer discount,
                 Integer time_in_secs, Integer start_day, Integer start_month, Integer start_year,
                    Integer start_hour, Integer start_minute) {
        this.title = title;
        this.photo_url = photo_url;
        this.quantity = quantity;
        this.orig_price = orig_price;
        this.discount = discount;
        this.time_in_secs = time_in_secs;
        this.start_day = start_day;
        this.start_month = start_month;
        this.start_year = start_year;
        this.start_hour = start_hour;
        this.start_minute = start_minute;
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
