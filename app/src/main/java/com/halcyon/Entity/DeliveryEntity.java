package com.halcyon.Entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.halcyon.channelbridgedb.DatabaseHelper;

import java.sql.SQLException;

public class DeliveryEntity {

    String invoiceNumber;
    String invoiceDate;
    String customerNumber;
    String customer;
    String invoiceValue;
    int deliveryStatus;
    String invoiceReason;


    public DeliveryEntity() {
    }

    public DeliveryEntity(String invoiceNumber, String invoiceDate, String customerNumber, String customer, String invoiceValue, int deliveryStatus, String invoiceReason) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerNumber = customerNumber;
        this.customer = customer;
        this.invoiceValue = invoiceValue;
        this.deliveryStatus = deliveryStatus;
        this.invoiceReason = invoiceReason;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(String invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getInvoiceReason() {
        return invoiceReason;
    }

    public void setInvoiceReason(String invoiceReason) {
        this.invoiceReason = invoiceReason;
    }
}

