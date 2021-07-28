package com.example.foodorderingsystem;

import android.graphics.Bitmap;

public class Food {





    public Food(String food, String allergy, String price, String url_image, Bitmap bitmap) {
        this.food = food;
        this.allergy = allergy;
        this.price = price;
        this.url_image = url_image;
        this.bitmap = bitmap;
    }




    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    private String food;
    private String allergy;
    private String price;
    private String url_image;
    private Bitmap bitmap;


    public Food() {

        this.url_image = "";
        this.food = "";
        this.price = "";
        this.allergy = "";

    }
}
