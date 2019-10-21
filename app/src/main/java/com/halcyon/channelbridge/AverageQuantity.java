package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.halcyon.Entity.Product;
import com.halcyon.channelbridgeaddapters.AvarageAdapter;
import com.halcyon.channelbridgeaddapters.AvarageItem;
import com.halcyon.channelbridgedb.CustomerProductAvg;
import com.halcyon.channelbridgedb.Products;

import java.util.ArrayList;

/**
 * Created by Himanshu on 8/8/2016.
 */
public class AverageQuantity extends Activity {
    RecyclerView eventList;

    ArrayList<AvarageItem> item = new ArrayList<AvarageItem>();
    private String pharmacyId = "";
    AvarageAdapter adapter;
    ArrayList<Product> prductList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.averagequantity);

        if (getIntent().getExtras() != null) {
            pharmacyId = getIntent().getExtras().getString("PharmacyId");
        }

        eventList = (RecyclerView) findViewById(R.id.list_news);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        eventList.setLayoutManager(mLayoutManager);

        eventList.setItemAnimator(new DefaultItemAnimator());

        adapter = new AvarageAdapter(this,item);

        Products pro = new Products(AverageQuantity.this);
        pro.openReadableDatabase();
        prductList =pro.getProductsToAVGQTY("",pharmacyId);
        pro.closeDatabase();
        System.out.println("size :"+prductList.size());
        for (Product ps : prductList) {

            item.add(new AvarageItem(ps.getCode(),ps.getProDes(),String.valueOf(ps.getAvg()),pharmacyId,0));
        }


        eventList.setAdapter(adapter);
    }

    public void getDetails(final String code , String id){


//


        Dialog dialogBoxedit;
        dialogBoxedit = new Dialog(AverageQuantity.this);
        dialogBoxedit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBoxedit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxedit.setContentView(R.layout.dialog_averagequantity);
        dialogBoxedit.setCancelable(true);
        dialogBoxedit.show();

        TextView proDode= (TextView) dialogBoxedit.findViewById(R.id.textView_dialog_avg_code);
        EditText proQty = (EditText) dialogBoxedit.findViewById(R.id.dialog_avgQty);

        proDode.setText(code);
        final CustomerProductAvg dbAvg = new CustomerProductAvg(AverageQuantity.this);
        final Products pro = new Products(AverageQuantity.this);

        proQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                dbAvg.openWritableDatabase();
                dbAvg.insertCustomerProductAvg(pharmacyId,code,Integer.parseInt(editable.toString()),1);
                dbAvg.closeDatabase();

                 prductList.clear();
                 eventList.removeAllViews();
                 item.clear();

                pro.openReadableDatabase();
                prductList =pro.getProductsToAVGQTY("",pharmacyId);
                pro.closeDatabase();

                for (Product ps : prductList) {
                    item.add(new AvarageItem(ps.getCode(),ps.getProDes(),String.valueOf(ps.getAvg()),pharmacyId,0));
                }

                eventList.setAdapter(adapter);

            }
        });


/*

        pro.openReadableDatabase();
        prductList =pro.getProductsToAVGQTY("",pharmacyId);
        pro.closeDatabase();
        for (Product ps : prductList) {
            int type;
            if(ps.getCode().equals(code)){
                type=1;
            }else {
                type=0;
            }
            item.add(new AvarageItem(ps.getCode(),ps.getProDes(),String.valueOf(ps.getAvg()),pharmacyId,type));
        }


        eventList.setAdapter(adapter);*/


    }
}
