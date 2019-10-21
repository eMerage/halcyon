package com.halcyon.channelbridge;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.halcyon.channelbridge.R;


//
@SuppressWarnings("deprecation")
public class CustomerDetailsComments_TabWidget extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_comments_tab_widget);


        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost(); // The activity TabHost
        TabHost.TabSpec spec; // Resusable TabSpec for each tab
        Intent viewCustomerDetailsIntent, CommentsIntent;

        Bundle extras = getIntent().getExtras();


        viewCustomerDetailsIntent = new Intent("com.halcyon.channelbridge.CUSTOMERDETAILSACTIVITY");

        viewCustomerDetailsIntent.putExtras(extras);

        spec = tabHost
                .newTabSpec("customer_details")
                .setIndicator("Customer Details",
                        res.getDrawable(R.drawable.customer_view_tab))
                .setContent(viewCustomerDetailsIntent);
        tabHost.addTab(spec);


        CommentsIntent = new Intent("com.halcyon.channelbridge.REMARKSACTIVITY");

        CommentsIntent.putExtras(extras);

        spec = tabHost
                .newTabSpec("remarks_comments")
                .setIndicator("Remarks/Comments",
                        res.getDrawable(R.drawable.remarks_tab))
                .setContent(CommentsIntent);
        tabHost.addTab(spec);


        tabHost.setCurrentTab(0);
    }

}


