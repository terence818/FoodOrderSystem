package com.example.foodorderingsystem;

public class Item {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Item(String name, Boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public Item() {
        this.name = "";
        this.isActive = false;
    }

    private String name;
    private Boolean isActive;
}
