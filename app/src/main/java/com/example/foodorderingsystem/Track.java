package com.example.foodorderingsystem;

public class Track {


    public Track(String status, String total, String buyerId, String buyer_location_long, String buyer_location_lat,
                 String buyer_address, String restaurant_long, String restaurant_lat, String restaurant_address,
                 String restaurant_name,String distance,String duration,String courier_long,String courier_lat) {
        this.status = status;
        this.total = total;
        this.buyerId = buyerId;
        this.buyer_location_long = buyer_location_long;
        this.buyer_location_lat = buyer_location_lat;
        this.buyer_address = buyer_address;
        this.restaurant_name=restaurant_name;
        this.restaurant_long = restaurant_long;
        this.restaurant_lat = restaurant_lat;
        this.restaurant_address = restaurant_address;
        this.distance = distance;
        this.duration = duration;
        this.courier_long = courier_long;
        this.courier_lat = courier_lat;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private String total;

    private String buyerId;
    private String buyer_location_long;
    private String buyer_location_lat;

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyer_location_long() {
        return buyer_location_long;
    }

    public void setBuyer_location_long(String buyer_location_long) {
        this.buyer_location_long = buyer_location_long;
    }

    public String getBuyer_location_lat() {
        return buyer_location_lat;
    }

    public void setBuyer_location_lat(String buyer_location_lat) {
        this.buyer_location_lat = buyer_location_lat;
    }

    public String getBuyer_address() {
        return buyer_address;
    }

    public void setBuyer_address(String buyer_address) {
        this.buyer_address = buyer_address;
    }

    public String getRestaurant_long() {
        return restaurant_long;
    }

    public void setRestaurant_long(String restaurant_long) {
        this.restaurant_long = restaurant_long;
    }

    public String getRestaurant_lat() {
        return restaurant_lat;
    }

    public void setRestaurant_lat(String restaurant_lat) {
        this.restaurant_lat = restaurant_lat;
    }

    public String getRestaurant_address() {
        return restaurant_address;
    }

    public void setRestaurant_address(String restaurant_address) {
        this.restaurant_address = restaurant_address;
    }

    private String buyer_address;
    private String restaurant_long;
    private String restaurant_lat;
    private String restaurant_address;

    public String getCourier_email() {
        return courier_email;
    }

    public void setCourier_email(String courier_email) {
        this.courier_email = courier_email;
    }

    public String getCourier_name() {
        return courier_name;
    }

    public void setCourier_name(String courier_name) {
        this.courier_name = courier_name;
    }

    private String courier_email;
    private String courier_name;

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    private String restaurant_name;


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCourier_long() {
        return courier_long;
    }

    public void setCourier_long(String courier_long) {
        this.courier_long = courier_long;
    }

    public String getCourier_lat() {
        return courier_lat;
    }

    public void setCourier_lat(String courier_lat) {
        this.courier_lat = courier_lat;
    }

    private String distance;
    private String duration;
    private String courier_long;
    private String courier_lat;

    public Track() {
        this.status = "";
        this.total = "";
        this.buyerId = "";
        this.buyer_location_long = "";
        this.buyer_location_lat = "";
        this.buyer_address = "";
        this.restaurant_long = "";
        this.restaurant_lat = "";
        this.restaurant_address = "";
        this.restaurant_name="";
        this.courier_email = "";
        this.courier_name = "";
        this.distance = "";
        this.duration = "";
        this.courier_long = "";
        this.courier_lat = "";
    }

}
