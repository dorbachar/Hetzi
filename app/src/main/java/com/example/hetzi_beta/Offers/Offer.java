package com.example.hetzi_beta.Offers;

/*
* Offer-
* A simple, pretty straight-forward class built to contain an offer's details as specified in the
* form. Details may vary later along the way.
*
* */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Offer implements Parcelable {
    private String      title;
    private String      photoUrl;
    private Integer     quantity;
    private Float       origPrice;
    private Integer     discount;
    private Integer     timeInSecs; // TODO : TIME OVERHAUL

    public Integer s_day;
    public Integer s_month;
    public Integer s_year;
    public Integer s_hour;
    public Integer s_minute;

    private boolean     is_active;
    private String      fbKey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(title);
        dest.writeString(photoUrl);
        dest.writeInt(quantity);
        dest.writeFloat(origPrice);
        dest.writeInt(discount);
        dest.writeInt(timeInSecs);
        dest.writeInt(s_day);
        dest.writeInt(s_month);
        dest.writeInt(s_year);
        dest.writeInt(s_hour);
        dest.writeInt(s_minute);
        dest.writeByte((byte) (is_active ? 1 : 0));
        dest.writeString(fbKey);
    }

    public Offer(Parcel in) {
        title           = in.readString();
        photoUrl = in.readString();
        quantity        = in.readInt();
        origPrice = in.readFloat();
        discount        = in.readInt();
        timeInSecs = in.readInt();
        s_day           = in.readInt();
        s_month         = in.readInt();
        s_year          = in.readInt();
        s_hour          = in.readInt();
        s_minute        = in.readInt();
        is_active       = in.readByte() != 0;
        fbKey           = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    public Offer() {}

    public Offer(String title, String photo_url, Integer quantity, Float orig_price, Integer discount,
                 Integer time_in_secs, Integer start_day, Integer start_month, Integer start_year,
                    Integer start_hour, Integer start_minute) {
        this.title          = title;
        this.photoUrl = photo_url;
        this.quantity       = quantity;
        this.origPrice = orig_price;
        this.discount       = discount;
        this.timeInSecs = time_in_secs;
        this.s_day          = start_day;
        this.s_month        = start_month;
        this.s_year         = start_year;
        this.s_hour         = start_hour;
        this.s_minute       = start_minute;
        this.is_active      = shouldOfferBeActive(start_day, start_month, start_year, start_hour, start_minute);
        this.fbKey          = "none";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photo_url) {
        this.photoUrl = photo_url;
    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Integer getQuantity() { return this.quantity; }

    public void setOrigPrice(Float orig_price) { this.origPrice = orig_price; }

    public Float getOrigPrice() { return this.origPrice; }

    public void setDiscount(int discount) { this.discount = discount; }

    public Integer getDiscount() { return this.discount; }

    public Integer getTimeInSecs() {
        return this.timeInSecs;
    }

    public void setTimeInSecs(Integer time_in_secs) {
        this.timeInSecs = time_in_secs;
    }


    public boolean isActive() {
        return is_active;
    }
    public void setActive(boolean is_active) {
        this.is_active = is_active;
    }

    private boolean shouldOfferBeActive(Integer start_day, Integer start_month, Integer start_year,
                                        Integer start_hour, Integer start_minute) {
        final Calendar c = Calendar.getInstance();



        return false;
    }

    public String getFbKey() {
        return this.fbKey;
    }
    public void setFbKey(String new_key) {
        this.fbKey = new_key;
    }

    public String getFieldFromString(String field_name) {
        switch(field_name) {
            case "title":
                return title;
            case "photoUrl":
                return photoUrl;
            case "quantity":
                return quantity.toString();
            case "origPrice":
                return origPrice.toString();
            case "discount":
                return discount.toString();
            case "timeInSecs":
                return timeInSecs.toString();
            case "s_day":
                return s_day.toString();
            case "s_month":
                return s_month.toString();
            case "s_year":
                return s_year.toString();
            case "s_hour":
                return s_hour.toString();
            case "s_minute":
                return s_minute.toString();
        }
        return "none";
    }
}
