package com.halcyon.channelbridge;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Remarks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class RemarksActivity extends Activity {

    //
    TextView tViewDate;
    Button btnDone;
    TableLayout tblLayoutRemarksTable;
    String itineraryId, pharmacyId;
    Intent itineraryListIntent = new Intent("com.halcyon.channelbridge.ITINERARYLIST");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remarks_comments);

        btnDone = (Button) findViewById(R.id.bDone);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        tblLayoutRemarksTable = (TableLayout) findViewById(R.id.tblRemarks);

        setInitialData();

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDatafromPreviousActivy();
        }

        populateRemarksTable(itineraryId);

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(itineraryListIntent);
                finish();
            }
        });

    }

    private void getDatafromPreviousActivy() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        itineraryId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(itineraryListIntent);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setInitialData() {
        String currentDate = DateFormat.getDateInstance().format(new Date());

        tViewDate.setText(currentDate);
    }


    public void populateRemarksTable(String itnId) {
        ArrayList<String[]> remarkDetails = new ArrayList<String[]>();

        TableRow tr;
        tblLayoutRemarksTable.setShrinkAllColumns(true);

        Remarks remarksObject = new Remarks(this);
        remarksObject.openReadableDatabase();
        remarkDetails = remarksObject.getRemarkDetailsByItineraryId(itnId);
        remarksObject.closeDatabase();

        try {

            int count = 1;

            for (final String[] remarkData : remarkDetails) {
                Log.w("called", "inside populate for");

                tr = new TableRow(this);
                tr.setId(1000 + count);
                tr.setPadding(0, 2, 0, 2);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));
                tr.setClickable(true);

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);
                }

                TextView tvTimeStamp = new TextView(this);
                tvTimeStamp.setId(200 + count);
                tvTimeStamp.setText(remarkData[3]);
                tvTimeStamp.setGravity(Gravity.LEFT);
                tvTimeStamp.setTextColor(Color.WHITE);
                tvTimeStamp.setPadding(10, 2, 10, 2);
                tr.addView(tvTimeStamp);
                Log.w("Prop: ", remarkData[3] + "");


                TextView tvRemark = new TextView(this);
                tvRemark.setId(200 + count);
                tvRemark.setText(remarkData[2]);
                tvRemark.setPadding(10, 2, 10, 2);
                tvRemark.setGravity(Gravity.LEFT);
                tvRemark.setTextColor(Color.WHITE);
                tr.addView(tvRemark);

                count++;

                tr.setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });

                tblLayoutRemarksTable.addView(tr,
                        new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
            }
        } catch (Exception e) {

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putString("itineraryId", itineraryId);
        outState.putString("pharmacyId", pharmacyId);

    }

    private void setBundleData(Bundle bundlData) {

        itineraryId = bundlData.getString("itineraryId");
        pharmacyId = bundlData.getString("pharmacyId");


    }

}



