package com.halcyon.Entity;

/**
 * Created by Amila on 11/15/15.
 */
public class TempInvoiceStock {

    private String productCode;
    private String productId;
    private String batchCode;
    private String shelfQuantity;
    private String requestQuantity;
    private String freeQuantity;
    private String freeQuantitySystem;
    private String normalQuantity;
    private String row_ID;
    private int Stock;
    private double percentage;
    private String expiryDate;
    private String timestamp;
    private String productDes;
    private String price;
    private String isFreeAllowed;
    private String isDiscountAllowed;

    private int StockFull;//for multiple
    private int BatchCount;//for multiple

    private String retailPrice;
    private String perchesPrice;

    private int Color;

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }


    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getPerchesPrice() {
        return perchesPrice;
    }

    public void setPerchesPrice(String perchesPrice) {
        this.perchesPrice = perchesPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(String shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public String getRequestQuantity() {
        return requestQuantity;
    }

    public void setRequestQuantity(String requestQuantity) {
        this.requestQuantity = requestQuantity;
    }

    public String getFreeQuantitySystem() {
        return freeQuantitySystem;
    }

    public void setFreeQuantitySystem(String freeQuantity) {
        this.freeQuantitySystem = freeQuantity;
    }

    public String getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(String freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public String getNormalQuantity() {
        return normalQuantity;
    }

    public void setNormalQuantity(String normalQuantity) {
        this.normalQuantity = normalQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRow_ID() {
        return row_ID;
    }

    public void setRow_ID(String row_ID) {
        this.row_ID = row_ID;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductDes() {
        return productDes;
    }

    public void setProductDes(String productDes) {
        this.productDes = productDes;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getIsFreeAllowed() {
        return isFreeAllowed;
    }

    public void setIsFreeAllowed(String isFreeAllowed) {
        this.isFreeAllowed = isFreeAllowed;
    }

    public String getIsDiscountAllowed() {
        return isDiscountAllowed;
    }

    public void setIsDiscountAllowed(String isDiscountAllowed) {
        this.isDiscountAllowed = isDiscountAllowed;
    }

    public int getStockFull() {
        return StockFull;
    }

    public void setStockFull(int stockf) {
        StockFull = stockf;
    }

    public int getBatchCount() {
        return BatchCount;
    }

    public void setBatchCount(int bcount) {
        BatchCount = bcount;
    }
}