package com.halcyon.channelbridgehelp;

/**
 * Created by Amila on 6/12/15.
 */
public enum RemarksType{

    ITINERARY("itinerary"),EXTAR_CUSTOMER("extra_customer"),RETURN_PRODUCT_WITH_HISTORY("Return_product_with_history"),RETURN_PRODUCT_WITHOUT_HISTORY("Return_product_without_history"),RETURN_INVOICE("Return_Invoice"),REINVOICE("ReInvoice");

    RemarksType(String remark) {
        this.remark = remark;
    }
    private String remark;
    @Override
    public String toString(){
        return remark;
    }
}
