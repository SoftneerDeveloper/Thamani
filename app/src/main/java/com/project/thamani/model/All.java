package com.project.thamani.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class All {

@SerializedName("uuid")
@Expose
private Integer uuid;
@SerializedName("receipt_number")
@Expose
private String receiptNumber;
@SerializedName("quantity")
@Expose
private Integer quantity;
@SerializedName("phone")
@Expose
private String phone;
@SerializedName("notes")
@Expose
private String notes;
@SerializedName("item")
@Expose
private Integer item;
@SerializedName("customer")
@Expose
private String customer;
@SerializedName("active")
@Expose
private String active;

public Integer getUuid() {
return uuid;
}

public void setUuid(Integer uuid) {
this.uuid = uuid;
}

public String getReceiptNumber() {
return receiptNumber;
}

public void setReceiptNumber(String receiptNumber) {
this.receiptNumber = receiptNumber;
}

public Integer getQuantity() {
return quantity;
}

public void setQuantity(Integer quantity) {
this.quantity = quantity;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public String getNotes() {
return notes;
}

public void setNotes(String notes) {
this.notes = notes;
}

public Integer getItem() {
return item;
}

public void setItem(Integer item) {
this.item = item;
}

public String getCustomer() {
return customer;
}

public void setCustomer(String customer) {
this.customer = customer;
}

public String getActive() {
return active;
}

public void setActive(String active) {
this.active = active;
}

}