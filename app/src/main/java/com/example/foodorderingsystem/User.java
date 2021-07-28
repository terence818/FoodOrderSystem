package com.example.foodorderingsystem;

public class User {





    public User(String username, String email, String phone, String type, String address, String latitude, String longitude, String timestamp) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.address = address;
        Latitude = latitude;
        Longitude = longitude;
        this.timestamp=timestamp;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    private String email;
    private String phone;
    private String type;
    private String address;
    private String Latitude;
    private String Longitude;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    public User() {
        this.username = "";
        this.email = "";
        this.phone = "";
        this.type = type;
        this.address = "";
        this.timestamp="";

    }
}
