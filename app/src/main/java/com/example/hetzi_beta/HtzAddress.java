package com.example.hetzi_beta;

public class HtzAddress {
    Double latitude;
    Double longtitude;
    String  address;

    public HtzAddress(Double latitude, Double longtitude, String address) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
