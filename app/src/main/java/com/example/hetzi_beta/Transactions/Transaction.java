package com.example.hetzi_beta.Transactions;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Transaction {

    // TODO : fields of אמצעי תשלום etc. after payment is integrated into the system
    /*
    * The member 'title' will contain info about the name & quantity. For example if the Transaction
    * was buying 2 Salads the title will be: "2 x Salad"
    * */
    private String  title;
    private String  paying_uid;
    private String  receive_uid;
    private String  offer_id;

    private Float   sum;
    private Integer quantity;

    // For Insights mainly
    private Integer duration;
    private String  s_time;
    private Integer discount;

    public Transaction(Offer offer, Shop shop) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        this.title          = offer.getTitle();
        this.paying_uid     = user.getUid();
        this.receive_uid    = shop.getUid();
        this.offer_id       = offer.getFbKey();
        this.sum            = Utils.priceAfterDiscount(offer.getOrigPrice(), offer.getDiscount());
        this.quantity       = 1;
        this.duration       = offer.durationMinutes();
        this.s_time         = offer.getS_time();
        this.discount       = offer.getDiscount();
    }

    public String   getPaying_uid() {
        return paying_uid;
    }

    public void     setPaying_uid(String paying_uid) {
        this.paying_uid = paying_uid;
    }

    public String   getReceive_uid() {
        return receive_uid;
    }

    public void     setReceive_uid(String receive_uid) {
        this.receive_uid = receive_uid;
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

}
