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
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Offer implements Parcelable {
    private String      title;
    private String      photoUrl;
    private Integer     quantity;
    private Float       origPrice;
    private Integer     discount;

    private String      s_time;
    private String      e_time;
//    private boolean     active;
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
        dest.writeString(s_time);
        dest.writeString(e_time);
//        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(fbKey);
    }

    public Offer(Parcel in) {
        title           = in.readString();
        photoUrl        = in.readString();
        quantity        = in.readInt();
        origPrice       = in.readFloat();
        discount        = in.readInt();
        s_time          = in.readString();
        e_time          = in.readString();
//        active          = in.readByte() != 0;
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
                 String duration, Integer start_day, Integer start_month, Integer start_year,
                    Integer start_hour, Integer start_minute) {
        this.title          = title;
        this.photoUrl       = photo_url;
        this.quantity       = quantity;
        this.origPrice      = orig_price;
        this.discount       = discount;
        this.s_time         = getStartInstant(start_day, start_month, start_year, start_hour, start_minute);
        this.e_time         = getEndInstant(duration);
//        this.active         = isActive();
        this.fbKey          = "none";
    }

    private String getEndInstant(String duration) {
        char[] hours = {duration.charAt(0), duration.charAt(1)};
        char[] minutes = {duration.charAt(3), duration.charAt(4)};
        Integer i_hours = Integer.parseInt(String.valueOf(hours));
        Integer i_minutes = Integer.parseInt(String.valueOf(minutes));

        Instant s_time = Instant.parse(this.s_time);
        Instant e_time = s_time.plus(i_hours, ChronoUnit.HOURS).plus(i_minutes,
                ChronoUnit.MINUTES);

        return e_time.toString();
    }

    private String getStartInstant(Integer start_day, Integer start_month, Integer start_year, Integer start_hour, Integer start_minute) {
        LocalDateTime ldt = LocalDateTime.of(start_year, start_month, start_day, start_hour, start_minute, 0);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("Israel"));
        Instant time_inst = zdt.toInstant();

        return time_inst.toString();
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

        // 2013-05-30T23:38:23.085Z

        Instant inst = Instant.parse(s_time);
        ZonedDateTime zdt = inst.atZone(ZoneId.of("Israel"));

        String curr_date = zdt.toString();

        char[] year = {curr_date.charAt(0), curr_date.charAt(1), curr_date.charAt(2), curr_date.charAt(3)};
        char[] month = {curr_date.charAt(5), curr_date.charAt(6)};
        char[] day = {curr_date.charAt(8), curr_date.charAt(9)};

        char[] hour = {curr_date.charAt(11), curr_date.charAt(12)};
        char[] minute = {curr_date.charAt(14), curr_date.charAt(15)};

        date_as_list.add(parseInt(String.valueOf(day)));
        date_as_list.add(parseInt(String.valueOf(month)));
        date_as_list.add(parseInt(String.valueOf(year)));
        date_as_list.add(parseInt(String.valueOf(hour)));
        date_as_list.add(parseInt(String.valueOf(minute)));

        return date_as_list;
    }

    public boolean isActive() {
        Instant start = Instant.parse(s_time);
        Instant end   = Instant.parse(e_time);

        return Instant.now().isBefore(end) && Instant.now().isAfter(start);
    }

    public boolean hasEnded() {
        Instant end   = Instant.parse(e_time);

        return Instant.now().isAfter(end);
    }

    public boolean hasStarted() {
        Instant start   = Instant.parse(s_time);

        return Instant.now().isAfter(start);
    }

    public int totalDurationInSecs() {
        Instant start = Instant.parse(s_time);
        Instant end   = Instant.parse(e_time);

        return (int)ChronoUnit.SECONDS.between(start, end);
    }

    public int minutesTillEnd() {
        Instant end   = Instant.parse(e_time);

        return (int)ChronoUnit.MINUTES.between(Instant.now(), end);
    }

    public int secondsTillEnd() {
        Instant end   = Instant.parse(e_time);

        return (int)ChronoUnit.SECONDS.between(Instant.now(), end);
    }

    public int durationMinutes() {
        Instant start = Instant.parse(s_time);
        Instant end   = Instant.parse(e_time);

        return (int)ChronoUnit.MINUTES.between(start, end);
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
            case "s_time":
                return s_time;
        }
        return "none";
    }

    // ------------- Getters & Setters ------------ //
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getOrigPrice() {
        return origPrice;
    }

    public void setOrigPrice(Float origPrice) {
        this.origPrice = origPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

//    public void setActive(boolean active) {
//        this.active = active;
//    }
//
//    public boolean isActive() {
//        return active;
//    }

    public String getFbKey() {
        return fbKey;
    }

    public void setFbKey(String fbKey) {
        this.fbKey = fbKey;
    }
}
