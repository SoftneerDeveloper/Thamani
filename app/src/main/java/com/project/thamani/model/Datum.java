package com.project.thamani.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("uuid")
    @Expose
    private Integer uuid;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("Retailer")
    @Expose
    private String retailer;
    @SerializedName("GTIN")
    @Expose
    private String gTIN;

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public String getGTIN() {
        return gTIN;
    }

    public void setGTIN(String gTIN) {
        this.gTIN = gTIN;
    }

}
