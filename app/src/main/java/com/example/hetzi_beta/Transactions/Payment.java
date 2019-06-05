package com.example.hetzi_beta.Transactions;

/*
* This class represents a payment from a customer whose uid is payer_uid to a business whose uid
* is receiver_uid.
*
* In the business side: In 'Past Deals' tab there will be a list of Payment objects displayed,
* and
*
* */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Payment implements Parcelable {
    private String title;
    private Float  sum;
    private String date;
    private ArrayList<Transaction> transactions;

    public Payment() {
        sum = 0f;
        transactions = new ArrayList<>(); // comment this line out for an easy bug (for debugging BugReportActivity)
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    public void addToTransactions(Transaction t) {
        transactions.add(t);
    }

    public void updateDetailsAfterPopulation(int side) {
        for (Transaction t : transactions) {
            sum += t.getSum();
        }

        title   = transactions.get(0).getName();
        date    = transactions.get(0).getTrans_time();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // ~~~~~~~~~~~~~~ Implementing Parcelable ~~~~~~~~~~~~~~ //

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (sum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(sum);
        }
        dest.writeString(date);
        dest.writeTypedList(transactions);
    }

    protected Payment(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0) {
            sum = null;
        } else {
            sum = in.readFloat();
        }
        date = in.readString();
        transactions = in.createTypedArrayList(Transaction.CREATOR);
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
}
