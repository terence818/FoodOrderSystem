package com.example.foodorderingsystem;

import android.graphics.Bitmap;

public class Cart {


    public Cart(String price, String url_image, Bitmap bitmap, String food, String quantity, String restaurant) {
        this.price = price;
        this.url_image = url_image;
        this.bitmap = bitmap;
        Food = food;
        this.quantity = quantity;
        this.restaurant = restaurant;
    }

    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFood() {
        return Food;
    }

    public void setFood(String food) {
        Food = food;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    private String Food;
    private String quantity;
    private String price;
    private String url_image;
    private String restaurant;

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }


    public Cart() {

        this.url_image = "";
        this.Food = "";
        this.price = "";
        this.quantity = "";
        this.restaurant = "";

    }
}
