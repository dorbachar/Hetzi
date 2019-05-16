package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.location.Location;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils;

import static com.example.hetzi_beta.Utils.HTZ_INVALID_DISTANCE;
import static com.example.hetzi_beta.Utils.HTZ_LOCATION_NOT_FOUND;

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

    public Float calcDistanceFromSale() {
        float[] res = new float[1];

        if (Utils.user_lat == HTZ_LOCATION_NOT_FOUND || Utils.user_lon == HTZ_LOCATION_NOT_FOUND) {
            return HTZ_INVALID_DISTANCE;
        }

        Location.distanceBetween(Utils.user_lat, Utils.user_lon, shop.getLat(), shop.getLon(), res);

        return Utils.round(res[0] / 1000,1);
    }
}
