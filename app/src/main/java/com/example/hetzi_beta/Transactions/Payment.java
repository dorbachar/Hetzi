package com.example.hetzi_beta.Transactions;

/*
* This class represents a payment from a customer whose uid is payer_uid to a business whose uid
* is receiver_uid.
*
* In the business side: In 'Past Deals' tab there will be a list of Payment objects displayed,
* and
*
* */

public class Payment {
    private String title;
    private String payer_uid;
    private String payer_name;
    private String receiver_uid;
    private String receiver_name;
    private Float  sum;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPayer_uid() {
        return payer_uid;
    }

    public void setPayer_uid(String payer_uid) {
        this.payer_uid = payer_uid;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    public String getPayer_name() {
        return payer_name;
    }

    public void setPayer_name(String payer_name) {
        this.payer_name = payer_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }
}
