package com.example.hetzi_beta.CustomerApp.LiveSales;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;

public class Deal {
    private Offer offer;
    private Shop shop;

    public Deal(Offer offer, Shop shop) {
        this.offer = offer;
        this.shop = shop;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
