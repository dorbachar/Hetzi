package com.example.hetzi_beta.Transactions;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
* This class represents an Offer that items were bought from.
*
* So for example if a customer buys 2 donuts and 1 bread from the same shop there will be
* 2 BusinessTransaction objects created, one titled 'donuts' and quantity=2, and one titled 'bread'
* with quantity=1.
*
* */
public class BusinessTransaction extends Transaction {
    private String  cust_uid;
    private String  offer_id;

    // Intended For Insights
    private Integer duration;
    private String  s_time;
    private Integer discount;

    public BusinessTransaction(Offer offer) {
        super(offer);

        FirebaseUser user   = FirebaseAuth.getInstance().getCurrentUser();

        this.cust_uid       = user.getUid(); // When this is created, the current user is the paying one
        this.offer_id       = offer.getFbKey();

        this.duration       = offer.durationMinutes();
        this.s_time         = offer.getS_time();
        this.discount       = offer.getDiscount();
    }

    public String getCust_uid() {
        return cust_uid;
    }

    public void setCust_uid(String cust_uid) {
        this.cust_uid = cust_uid;
    }

    public Float    getSum() {
        return sum;
    }

    public void     setSum(Float sum) {
        this.sum = sum;
    }

    public String   getS_time() {
        return s_time;
    }

    public void     setS_time(String s_time) {
        this.s_time = s_time;
    }

    public int      getDiscount() {
        return discount;
    }

    public void     setDiscount(int discount) {
        this.discount = discount;
    }

    public Integer  getDuration() {
        return duration;
    }

    public void     setDuration(Integer duration) {
        this.duration = duration;
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

    public String   getOffer_id() {
        return offer_id;
    }

    public void     setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public void plusOneItem() {
        sum += (sum / quantity);
        quantity++;
    }

}
