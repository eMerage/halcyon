package com.halcyon.Entity;

/**
 * Created by Amila on 12/11/15.
 */
public class ReturnHeaderEntity {



    private  String invoiceNumber;
    private  String returnDate;
    private  String totalAmount;
    private  String  discountAmount;
    private  int  totalQuantity;
    private  String  startTime;
    private  String  endTime;
    private  String  longitude;
    private  String  latitude;
    private  String  cutomerNo;
    private  String   returnInvoiceNumber;
    private  Boolean   isUpload;
    private String id;
    private int detailsCount;


    public Boolean getUpload() {
        return isUpload;
    }

    public void setUpload(Boolean upload) {
        isUpload = upload;
    }

    public int getDetailsCount() {
        return detailsCount;
    }

    public void setDetailsCount(int detailsCount) {
        this.detailsCount = detailsCount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCutomerNo() {
        return cutomerNo;
    }

    public void setCutomerNo(String cutomerNo) {
        this.cutomerNo = cutomerNo;
    }

    public String getReturnInvoiceNumber() {
        return returnInvoiceNumber;
    }

    public void setReturnInvoiceNumber(String returnInvoiceNumber) {
        this.returnInvoiceNumber = returnInvoiceNumber;
    }

    public Boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Boolean isUpload) {
        this.isUpload = isUpload;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
