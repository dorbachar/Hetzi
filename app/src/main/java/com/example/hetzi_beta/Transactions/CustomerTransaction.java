package com.example.hetzi_beta.Transactions;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;

public class CustomerTransaction extends Transaction {
    public CustomerTransaction(){}

    public CustomerTransaction(Offer offer, Shop shop) {
        super(offer);
        this.name  = shop.getShopName();
    }
}
