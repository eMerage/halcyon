package com.halcyon.helpModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Puritha Dev on 2/6/2015.
 */
public class showMessage extends Activity {

    private Context context;

    public showMessage(Context context) {
        this.context = context;
    }

    public showMessage(){
    }

    public void showAlert(String localName) {

    }

    /**
     * common success ok message
     */
   public void SuccessMessage() {
       AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setTitle("Data loaded successfully");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        builder.show();
    }

    /**
     * exit from the system
     */
    public void exitByBackKeyPress() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setTitle("Do you want to exit?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        builder.show();

    }

    /**
     *
     * @param txtMessage the message need to display
     */
    public void customiseAlertMessage(String txtMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setTitle(txtMessage);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        builder.show();
    }



}
