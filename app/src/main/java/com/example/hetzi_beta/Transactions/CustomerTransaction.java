package com.example.hetzi_beta.Transactions;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;

public class CustomerTransaction extends Transaction {
    private String shop_name;

    public CustomerTransaction(Offer offer, Shop shop) {
        super(offer);
        this.shop_name = shop.getShopName();
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
}
