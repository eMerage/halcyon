package com.halcyon.channelbridge;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.halcyon.channelbridge.R;

public class AboutApplication extends Activity {

    TextView tViewApplicationName, tViewVersion, tViewWebSite;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_application);

        tViewApplicationName = (TextView) findViewById(R.id.tvApplicationName);
        tViewVersion = (TextView) findViewById(R.id.tvVersion);
        tViewWebSite = (TextView) findViewById(R.id.tvWebSite);
        btnDone = (Button) findViewById(R.id.bDone);

        String version = getApplicationVersion();
        String appName = "Halcyon v1.1.1(3)";

        tViewVersion.setText(version);
        tViewApplicationName.setText(appName);

        System.out.println(appName);

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent startItinerary = new Intent(AboutApplication.this,
                        ItineraryList.class);
                finish();
                startActivity(startItinerary);

            }
        });
    }

    public String getApplicationVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        return version;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent itineraryListIntent = new Intent(
                    "com.halcyon.channelbridge.ITINERARYLIST");
            startActivity(itineraryListIntent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
