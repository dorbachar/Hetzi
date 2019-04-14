package com.example.hetzi_beta;

/*
* Offer-
* A simple, pretty straight-forward class built to contain an offer's details as specified in the
* form. Details may vary later along the way.
*
* */

public class Offer {
    private String  title;          // p_... stands for product
    private String  photo_url;
    private int     quantity;
    private int     orig_price;
    private int     discount;
    private int     time_in_secs; // TODO : decide on time format

    public Offer() {
    }

    public Offer(String title, String photo_url, int quantity, int orig_price, int discount, int time_in_secs) {
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

    public int getQuantity() { return this.quantity; }

    public void setOrigPrice(int orig_price) { this.orig_price = orig_price; }

    public int getOrigPrice() { return this.orig_price; }

    public void setDiscount(int discount) { this.discount = discount; }

    public int getDiscount() { return this.discount; }

    public int getTimeInSecs() {
        return this.time_in_secs;
    }

    public void setTimeInSecs(int time_in_secs) {
        this.time_in_secs = time_in_secs;
    }
    }
