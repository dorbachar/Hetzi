package com.example.hetzi_beta.Transactions;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils;

/*
 * This class represents an Offer that items were bought from.
 *
 * So for example if a customer buys 2 donuts and 1 bread from the same shop there will be
 * 2 BusinessTransaction objects created, one titled 'donuts' and quantity=2, and one titled 'bread'
 * with quantity=1.
 *
 * */
public class Transaction {
    protected String  title;
    protected Float   sum;
    protected Integer quantity;
    protected String  payment_id;

    public Transaction(Offer offer) {
        this.title          = offer.getTitle();
        this.sum            = Utils.priceAfterDiscount(offer.getOrigPrice(), offer.getDiscount());
        this.quantity       = 1;
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

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public void plusOneItem() {
        sum += (sum / quantity);
        quantity++;
    }

}
