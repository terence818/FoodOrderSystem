package com.example.foodorderingsystem;

public class Card {

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    private String ccv;

    public Card(String ccv, String card_num, String year) {
        this.ccv = ccv;
        this.card_num = card_num;
        this.year = year;
    }

    private String card_num;
    private String year;

    public Card() {
        this.ccv = ccv;
        this.card_num = card_num;
        this.year = year;
    }
}
