package com.example.foodorderingsystem;

public class Restaurant {





    public Restaurant(String restaurantname,String address, String latitude, String longitude, String placeid , String cuisine , String budget , String delivery_fee) {
        this.placeid = placeid;
        this.restaurantname = restaurantname;
        Latitude = latitude;
        Longitude = longitude;
        this.address = address;
        this.cuisine = cuisine;
        this.budget = budget;
        this.delivery_fee = delivery_fee;

    }


    private String restaurantname;

    public String getRestaurantname() {
        return restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        this.restaurantname = restaurantname;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    private String Latitude;
    private String Longitude;
    private String address;

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Restaurant(String cuisine) {
        this.cuisine = cuisine;
    }

    private String cuisine;
    private String placeid;

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    private String budget;

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    private String delivery_fee;

    public Restaurant(){
        this.placeid = "";
        this.restaurantname = "";
        this.Latitude = "";
        this.Longitude = "";
        this.address = "";
        this.cuisine = "";
        this.budget = "0";
        this.delivery_fee = "";


    }

}
