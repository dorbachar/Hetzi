package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.location.Location;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils;

import static com.example.hetzi_beta.Utils.HTZ_INVALID_DISTANCE;
import static com.example.hetzi_beta.Utils.HTZ_LOCATION_NOT_FOUND;

public class Deal implements Comparable<Deal> {
    private Offer offer;
    private Shop shop;
    private String sort_filter;
    private Float distance_from_user;

    public Deal(Offer offer, Shop shop) {
        this.offer = offer;
        this.shop = shop;
        this.sort_filter = "none";
        this.distance_from_user = getUpdatedDistanceFromUser();
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

    public Float getUpdatedDistanceFromUser() {
        setDistanceFromUser(calcDistanceFromUser());
        return distance_from_user;
    }

    private Float calcDistanceFromUser() {
        float[] res = new float[1];

        if (Utils.user_lat == HTZ_LOCATION_NOT_FOUND || Utils.user_lon == HTZ_LOCATION_NOT_FOUND) {
            return HTZ_INVALID_DISTANCE;
        }

        Location.distanceBetween(Utils.user_lat, Utils.user_lon, shop.getLat(), shop.getLon(), res);

        return Utils.round(res[0] / 1000,1);
    }

    public String getSortFilter() {
        return sort_filter;
    }

    public void setSortFilter(String sort_filter) {
        this.sort_filter = sort_filter;
    }

    public Float getDistanceFromUser() {
        return distance_from_user;
    }

    public void setDistanceFromUser(Float distance_from_user) {
        this.distance_from_user = distance_from_user;
    }

    /*
        * From: https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html
        *
        * Returns:
        * A negative integer, zero, or a positive integer as this object is
        * less than, equal to, or greater than the specified object.
        *
        * @return (x < 0) <=> this < o
        * @return (x > 0) <=> this > o
        * @return (x = 0) <=> this == o
        *
        * The natural ordering for a class C is said to be *consistent with equals* if and only if
        * e1.compareTo(e2) == 0 has the same boolean value as e1.equals(e2)
        * for every e1 and e2 of class C.
        *
        * Note that null is not an instance of any class, and e.compareTo(null) should throw a
        * NullPointerException even though e.equals(null) returns false.
        *
    * */
    @Override
    public int compareTo(Deal o) {
        switch(sort_filter) {
            case "none":
                return 0;
            case "distance":
                return this.getDistanceFromUser().compareTo(o.getDistanceFromUser());
            case "price":
                return this.getOffer().priceAfterDiscount().compareTo(o.getOffer().priceAfterDiscount());
            case "time":
                return this.getOffer().getE_time().compareTo(o.getOffer().getE_time());
        }

        return 0;
    }
}
