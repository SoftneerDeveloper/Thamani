package com.project.thamani.model;

/**
 * Created by ravi on 20/02/18.
 */

public class Note {
    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_UUID = "item";
    public static final String COLUMN_ITEM = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_GTIN = "gtin";
    public static final String COLUMN_WARE= "warehouse";
    public static final String COLUMN_WID = "wid";
    public static final String COLUMN_MAN= "manufacturer";
    public static final String COLUMN_MID = "mid";
    public static final String COLUMN_GS1 = "gs1";
    public static final String COLUMN_RETAILER = "retailer";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SERIAL = "serial";
    public static final String COLUMN_OFFLINE = "offline";

    private int id;
    private String uuid;
    private String item;
    private String name;
    private String price;
    private String gtin;
    private String warehouse;
    private String wid;
    private String manufacturer;
    private String mid;
    private String gs1;
    private String retailer;
    private String timestamp;
    private String serial;
    private int offline;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_UUID + " TEXT,"
                    + COLUMN_ITEM + " TEXT,"
                    + COLUMN_PRICE + " TEXT,"
                    + COLUMN_GTIN + " TEXT,"
                    + COLUMN_WARE + " TEXT,"
                    + COLUMN_WID + " TEXT,"
                    + COLUMN_MAN + " TEXT,"
                    + COLUMN_MID + " TEXT,"
                    + COLUMN_GS1+ " TEXT,"
                    + COLUMN_RETAILER+ " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_SERIAL + " TEXT,"
                    + COLUMN_OFFLINE + " INTEGER "
                    + ")";

    public Note() {
    }

    public Note(int id, String uuid, String item,String name, String price, String gtin,String manufacturer,String mid,String warehouse,String wid,String gs1,String retailer, String timestamp,String serial, int offline) {
        this.id = id;
        this.uuid = uuid;
        this.item = item;
        this.name = name;
        this.price = price;
        this.gtin = gtin;
        this.warehouse = warehouse;
        this.wid = wid;
        this.manufacturer = manufacturer;
        this.mid = mid;
        this.gs1 = gs1;
        this.retailer = retailer;
        this.timestamp = timestamp;
        this.serial = serial;
        this.offline = offline;
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

//    public String getItems() {
//        return items;
//    }
//
//    public void setItems(String items) {
//        this.items = items;
//    }
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGTIN() {
        return gtin;
    }

    public void setGTIN(String gtin) {
        this.gtin = gtin;
    }
    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getGs1() {
        return gs1;
    }

    public void setGs1(String gs1) {
        this.gs1 = gs1;
    }
    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }
    public int getOffline() {
        return offline;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
    public String getSerial() {
        return serial;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
