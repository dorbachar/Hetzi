package com.example.hetzi_beta.Transactions;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Utils.Utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

/*
 * This class represents an Offer that items were bought from.
 *
 * So for example if a customer buys 2 donuts and 1 bread from the same shop there will be
 * 2 BusinessTransaction objects created, one titled 'donuts' and quantity=2, and one titled 'bread'
 * with quantity=1.
 *
 * */
public class Transaction implements Parcelable {
    protected String        title;
    protected Float         sum;
    protected Integer       quantity;
    protected String        trans_time;
    protected String        name;

    public Transaction(){}

    public Transaction(Offer offer) {
        this.title          = offer.getTitle();
        this.sum            = Utils.priceAfterDiscount(offer.getOrigPrice(), offer.getDiscount());
        this.quantity       = 1;
        this.trans_time     = generateTimeString();
        this.name           = "";
    }

    private String generateTimeString() {
        Instant instant = Instant.now();
        LocalTime local =  LocalTime.from(instant.atZone(ZoneId.of("GMT+3")));

        String flipped_date =   instant.toString().substring(8,10)  + "-" +
                                instant.toString().substring(5,7)   + "-" +
                                instant.toString().substring(0,4);

        return local.toString().substring(0,5) +" " + flipped_date;
    }

    public Float    getSum() {
        return sum;
    }

    public void     setSum(Float sum) {
        this.sum = sum;
    }

    public String   getTitle() {
        return title;
    }

    public void     setTitle(String title) {
        this.title = title;
    }

    public Integer  getQuantity() {
        return quantity;
    }

    public void     setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public String getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void plusOneItem() {
        sum += (sum / quantity);
        quantity++;
    }


    // ------------------------- Parcelable ------------------------- //



    protected Transaction(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0) {
            sum = null;
        } else {
            sum = in.readFloat();
        }
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        trans_time = in.readString();
        name = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (sum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(sum);
        }
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        dest.writeString(trans_time);
        dest.writeString(name);
    }


    @Override
    public int describeContents() {
        return 0;
    }
}
