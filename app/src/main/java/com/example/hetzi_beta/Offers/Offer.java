package com.example.hetzi_beta.Offers;

/*
* Offer-
* A simple, pretty straight-forward class built to contain an offer's details as specified in the
* form. Details may vary later along the way.
*
* */

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Offer implements Parcelable {
    private String      title;
    private String      photoUrl;
    private Integer     quantity;
    private Float       origPrice;
    private Integer     discount;
    private Integer     timeInSecs; // TODO : TIME OVERHAUL
    private String      date;      // TODO : TIME OVERHAUL  replace these two members with start_instant and end_instant
    private boolean     active;
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
        dest.writeString(date);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(fbKey);
    }

    public Offer(Parcel in) {
        title           = in.readString();
        photoUrl        = in.readString();
        quantity        = in.readInt();
        origPrice       = in.readFloat();
        discount        = in.readInt();
        timeInSecs      = in.readInt();
        date = in.readString();
        active = in.readByte() != 0;
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
        this.photoUrl       = photo_url;
        this.quantity       = quantity;
        this.origPrice      = orig_price;
        this.discount       = discount;
        this.timeInSecs     = time_in_secs;
        this.date           = formatDate(start_day, start_month, start_year, start_hour, start_minute);
        this.active         = shouldOfferBeActive(start_day, start_month, start_year, start_hour, start_minute);
        this.fbKey          = "none";
    }

    private String formatDate(Integer start_day, Integer start_month, Integer start_year, Integer start_hour, Integer start_minute) {
        String day = start_day >= 10 ? start_day.toString() : "0" + start_day.toString();
        String month = start_month >= 10 ? start_month.toString() : "0" + start_month.toString();
        String hour = start_hour >= 10 ? start_hour.toString() : "0" + start_hour.toString();
        String minute = start_minute >= 10 ? start_minute.toString() : "0" + start_minute.toString();

        String isoDate = start_year.toString() + "-" + month + "-" + day + "T" + hour + ":" + minute + ":00.00Z"; //  ISO-8601 DateTime format
        Instant instant_from_date = Instant.parse(isoDate);

        String instant_as_string = instant_from_date.toString();

        return isoDate;
    }

    public Integer getFromDate(String target) {
        List<Integer> date_as_list = getDateAsList();

        switch (target) {
            case "day":
                return date_as_list.get(0);
            case "month":
                return date_as_list.get(1);
            case "year":
                return date_as_list.get(2);
            case "hour":
                return date_as_list.get(3);
            case "minute":
                return date_as_list.get(4);
        }

        return -1;
    }

    private List<Integer> getDateAsList() {
        List<Integer> date_as_list = new ArrayList<>();

        char[] day = {date.charAt(0), date.charAt(1)};
        char[] month = {date.charAt(3), date.charAt(4)};
        char[] year = {date.charAt(6), date.charAt(7), date.charAt(8), date.charAt(9)};
        char[] hour = {date.charAt(12), date.charAt(13)};
        char[] minute = {date.charAt(15), date.charAt(16)};

        date_as_list.add(Integer.parseInt(String.valueOf(day)));
        date_as_list.add(Integer.parseInt(String.valueOf(month)));
        date_as_list.add(Integer.parseInt(String.valueOf(year)));
        date_as_list.add(Integer.parseInt(String.valueOf(hour)));
        date_as_list.add(Integer.parseInt(String.valueOf(minute)));

        return date_as_list;
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
        return active;
    }

    public void setActive(boolean is_active) {
        this.active = is_active;
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

    public String getDate() {
        return this.date;
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
            case "date":
                return date;
        }
        return "none";
    }
}
