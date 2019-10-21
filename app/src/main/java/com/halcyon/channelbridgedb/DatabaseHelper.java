package com.halcyon.channelbridgedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "channel_bridge_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static Context getContext() {
        return DatabaseHelper.getContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        Town.onCreate(db);
        District.onCreate(db);
        Delivery.onCreate(db);
        Target.onCreate(db);
        Approval_Persons.onCreate(db);
        Approval_Details.onCreate(db);
        DiscountStructures.onCreate(db);
        UserLogin.onCreate(db);
        Reps.onCreate(db);
        Itinerary.onCreate(db);
        Customers.onCreate(db);
        Products.onCreate(db);
        Remarks.onCreate(db);
        ProductRepStore.onCreate(db);
        Invoice.onCreate(db);
        InvoicedProducts.onCreate(db);
        InvoicedCheque.onCreate(db);
        CompetitorProducts.onCreate(db);
        ProductReturns.onCreate(db);
        CustomersPendingApproval.onCreate(db);
        ImageGallery.onCreate(db);
        ShelfQuantity.onCreate(db);
        CompetitorProductsImages.onCreate(db);
        ProductUnload.onCreate(db);
        CustomerProduct.onCreate(db);
        CustomerProductAvg.onCreate(db);
        AutoSyncOnOffFlag.onCreate(db);
        Rep_GPS.onCreate(db);
        Attendence.onCreate(db);
        Branch.onCreate(db);
        CollectionNoteSendToApprovel.onCreate(db);
        DEL_Outstandiing.onCreate(db);
        Master_Banks.onCreate(db);
        InvoicePaymentType.onCreate(db);
        ExpireWarning.onCreate(db);
        TemporaryInvoice.onCreate(db);
        CreditPeriod.onCreate(db);
        ReturnHeader.onCreate(db);
        DealerSales.onCreate(db);
        // VideoList.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        UserLogin.onUpgrade(db, oldVersion, newVersion);
        Reps.onUpgrade(db, oldVersion, newVersion);
        Itinerary.onUpgrade(db, oldVersion, newVersion);
        Customers.onUpgrade(db, oldVersion, newVersion);
        Products.onUpgrade(db, oldVersion, newVersion);
        Remarks.onUpgrade(db, oldVersion, newVersion);
        ProductRepStore.onUpgrade(db, oldVersion, newVersion);
        Invoice.onUpgrade(db, oldVersion, newVersion);
        InvoicedProducts.onUpgrade(db, oldVersion, newVersion);
        InvoicedCheque.onUpgrade(db, oldVersion, newVersion);
        CompetitorProducts.onUpgrade(db, oldVersion, newVersion);
        ProductReturns.onUpgrade(db, oldVersion, newVersion);
        CustomersPendingApproval.onUpgrade(db, oldVersion, newVersion);
        ImageGallery.onUpgrade(db, oldVersion, newVersion);
        ShelfQuantity.onUpgrade(db, oldVersion, newVersion);
        CompetitorProductsImages.onUpgrade(db, oldVersion, newVersion);
        ProductUnload.onUpgrade(db, oldVersion, newVersion);
        CustomerProduct.onUpgrade(db, oldVersion, newVersion);
        CustomerProductAvg.onUpgrade(db, oldVersion, newVersion);
        AutoSyncOnOffFlag.onUpgrade(db, oldVersion, newVersion);
        Rep_GPS.onUpgrade(db, oldVersion, newVersion);
        Attendence.onUpgrade(db, oldVersion, newVersion);
        Branch.onUpgrade(db, oldVersion, newVersion);
        CollectionNoteSendToApprovel.onUpgrade(db, oldVersion, newVersion);
        DEL_Outstandiing.onUpgrade(db, oldVersion, newVersion);
        Master_Banks.onUpgrade(db, oldVersion, newVersion);
        ExpireWarning.onUpgrade(db, oldVersion, newVersion);

        InvoicePaymentType.onUpgrade(db, oldVersion, newVersion);
        TemporaryInvoice.onUpgrade(db,oldVersion,newVersion);
        CreditPeriod.onUpgrade(db,oldVersion,newVersion);
        ReturnHeader.onUpgrade(db, oldVersion, newVersion);
        DealerSales.onUpgrade(db,oldVersion,newVersion);
        Target.onUpgrade(db,oldVersion,newVersion);
        Delivery.onUpgrade(db,oldVersion,newVersion);
        District.onUpgrade(db,oldVersion,newVersion);
        Town.onUpgrade(db,oldVersion,newVersion);
        // VideoList.onUpgrade(db,oldVersion,newVersion);
    }

}
