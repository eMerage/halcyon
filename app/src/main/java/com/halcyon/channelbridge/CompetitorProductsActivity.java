package com.halcyon.channelbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.CompetitorProducts;
import com.halcyon.channelbridgedb.CompetitorProductsImages;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.Sequence;

public class CompetitorProductsActivity extends Activity {

    static int competitorCameraData = 0;
    static int d = 0;
    EditText txtCompany, txtProduct, txtValue, txtPackSizes, txtOtherInfo, txtSendTo;
    Button btnSaveAndSend, btnCancel;
    GridView gViewCompetitorImages;
    ImageButton iBtnTakePicture;
    Builder alertCancel;
    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    boolean saveComplete = false;
    Intent cameraIntent;
    String competitorId = null;
    Bitmap competitorImage;
    CompetitorProductsImageAdapter competitorProductsImageAdapter;
    private Toast ExtStorage;

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 0;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    @SuppressWarnings({"static-access"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.competitor_products);

        txtCompany = (EditText) findViewById(R.id.etCompany);
        txtProduct = (EditText) findViewById(R.id.etProduct);
        txtValue = (EditText) findViewById(R.id.etValue);
        txtPackSizes = (EditText) findViewById(R.id.etPacketSize);
        txtOtherInfo = (EditText) findViewById(R.id.etOtherInfo);
        txtSendTo = (EditText) findViewById(R.id.etSendTo);
        gViewCompetitorImages = (GridView) findViewById(R.id.gCompetitorImages);
        iBtnTakePicture = (ImageButton) findViewById(R.id.ibTakePicture);


        btnSaveAndSend = (Button) findViewById(R.id.bSaveAndSend);
        btnCancel = (Button) findViewById(R.id.bCancel);

        getLastSavedCompetitorId();
        updateExternalStorageState();
        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            if (checkGalleryDirectory()) {
                ArrayList<String> requiredImageIds = new ArrayList<String>();
                requiredImageIds = getRequiredFileNames(competitorId);
                Log.w("RequireD IDzzz", requiredImageIds.size() + "");
                if (!requiredImageIds.isEmpty()) {
                    setImageAdapter(this, requiredImageIds);

                }
            }
        } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
            ExtStorage.makeText(CompetitorProductsActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
            ExtStorage.setGravity(Gravity.TOP, 100, 100);
            ExtStorage.show();
        } else if (!mExternalStorageAvailable) {
            ExtStorage.makeText(CompetitorProductsActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
            ExtStorage.setGravity(Gravity.TOP, 100, 100);
            ExtStorage.show();
        }


        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        }


        alertCancel = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want Cancel?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                clearImages();
                                finish();
                                Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                startActivity(startItinerary);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });


        btnSaveAndSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkDataForSave()) {
                    saveCompetitorProduct();
                    btnSaveAndSend.setEnabled(false);
                    saveComplete = true;
                    btnCancel.setText("Close");
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!saveComplete) {
                    if (checkDataForCancel()) {
                        clearImages();
                        finish();
                        Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                        startActivity(startItinerary);

                    } else {
                        alertCancel.show();
                    }
                } else {
                    finish();
                    Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                    startActivity(startItinerary);
                }
            }
        });


        iBtnTakePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, competitorCameraData);

            }
        });

        gViewCompetitorImages.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Log.w("arg2", arg2 + "");
                final Dialog imageViewDialog = new Dialog(CompetitorProductsActivity.this);
                imageViewDialog.setContentView(R.layout.competitor_products_image_popup);
                ImageView iViewProduct = (ImageView) imageViewDialog.findViewById(R.id.ivCompetitorImage);
                Button btnDone = (Button) imageViewDialog.findViewById(R.id.bDone);
                Button btnDelete = (Button) imageViewDialog.findViewById(R.id.bDelete);
                imageViewDialog.setTitle("Competitor Product Image");

                ArrayList<String> requiredImageIds = new ArrayList<String>();
                requiredImageIds = getRequiredFileNames(competitorId);

                try {
                    String imageName = requiredImageIds.get(arg2);
                    File customerImageFile = new File(
                            Environment.getExternalStorageDirectory() + File.separator
                                    + "DCIM" + File.separator + "Channel_Bridge_Competitors"
                                    + File.separator + imageName);

                    if (customerImageFile.exists()) {

                        try {
                            iViewProduct.setImageBitmap(decodeSampledBitmapFromResource(
                                    String.valueOf(customerImageFile), 450, 500));
                        } catch (IllegalArgumentException e) {
                            Log.w("Illegal argument exception", e.toString());
                        } catch (OutOfMemoryError e) {
                            Log.w("Out of memory error :(", e.toString());
                        }

                    } else {
                        iViewProduct.setImageResource(R.drawable.unknown_image);
                    }
                } catch (Exception e) {
                    Log.w("Error getting image file", e.toString());
                }

                btnDone.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        imageViewDialog.dismiss();

                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            ArrayList<String> requiredImageIds = new ArrayList<String>();
                            requiredImageIds = getRequiredFileNames(competitorId);
                            String imageName = requiredImageIds.get(arg2);

                            File competitorImageFile = new File(
                                    Environment.getExternalStorageDirectory() + File.separator
                                            + "DCIM" + File.separator + "Channel_Bridge_Competitors"
                                            + File.separator + imageName);
                            if (competitorImageFile.exists()) {
                                boolean status = false;
                                try {
                                    imageViewDialog.dismiss();
                                    status = competitorImageFile.delete();
                                    CompetitorProductsImages competitorProductsImages = new CompetitorProductsImages(CompetitorProductsActivity.this);
                                    competitorProductsImages.openWritableDatabase();
                                    competitorProductsImages.deleteImageByCompetitorIdAndImageId(competitorId, imageName);
                                    competitorProductsImages.closeDatabase();
                                } catch (Exception e) {
                                    Log.w("error deleting from db", e.toString());
                                }
                                Log.w("File Deleted: ", status + "");

                            }
                        } catch (Exception e) {
                            Log.w("PHoto delete error", e.toString());
                        } finally {
                            if (mExternalStorageAvailable && mExternalStorageWriteable) {
                                if (checkGalleryDirectory()) {
                                    ArrayList<String> newRequiredImageIds = new ArrayList<String>();
                                    newRequiredImageIds = getRequiredFileNames(competitorId);
                                    if (!newRequiredImageIds.isEmpty()) {
                                        try {
                                            setImageAdapter(CompetitorProductsActivity.this, newRequiredImageIds);
                                        } catch (Exception e) {
                                            Log.w("setadapter after delete", e.toString());
                                        }
                                    } else {
                                        setImageAdapter(CompetitorProductsActivity.this, newRequiredImageIds);
                                    }
                                }
                            } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
                                ExtStorage.makeText(CompetitorProductsActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
                                ExtStorage.setGravity(Gravity.TOP, 100, 100);
                                ExtStorage.show();
                            } else if (!mExternalStorageAvailable) {
                                ExtStorage.makeText(CompetitorProductsActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
                                ExtStorage.setGravity(Gravity.TOP, 100, 100);
                                ExtStorage.show();
                            }
                        }
                    }
                });

                imageViewDialog.show();
            }
        });
    }

    private boolean checkDataForCancel() {
        boolean flag = false;
        if ((txtCompany.getText().toString().isEmpty()) && (txtProduct.getText().toString().isEmpty())
                && (txtValue.getText().toString().isEmpty()) && (txtPackSizes.getText().toString().isEmpty())
                && (txtOtherInfo.getText().toString().isEmpty()) && txtSendTo.getText().toString().isEmpty()) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    private boolean checkDataForSave() {
        boolean flag = false;

        if (!txtCompany.getText().toString().isEmpty()) {
            if (!txtProduct.getText().toString().isEmpty()) {
                if (!txtValue.getText().toString().isEmpty()) {
                    if (!txtPackSizes.getText().toString().isEmpty()) {
                        if (!txtOtherInfo.getText().toString().isEmpty()) {
                            if (!txtSendTo.getText().toString().isEmpty()) {
                                flag = true;
                            } else {
                                flag = false;
                                txtSendTo.setFocusable(true);
                                txtSendTo.requestFocus();
                                Toast sendToEmpty = Toast.makeText(this, "Send to field cannot be empty", Toast.LENGTH_SHORT);
                                sendToEmpty.setGravity(Gravity.TOP, 50, 100);
                                sendToEmpty.show();
                            }
                        }//Haven't Put a validation to otherInfo!!
                    } else {
                        flag = false;
                        txtPackSizes.setFocusable(true);
                        txtPackSizes.requestFocus();
                        Toast packetSizeEmpty = Toast.makeText(this, "Packet Size field cannot be empty", Toast.LENGTH_SHORT);
                        packetSizeEmpty.setGravity(Gravity.TOP, 50, 100);
                        packetSizeEmpty.show();
                    }
                } else {
                    flag = false;
                    txtValue.setFocusable(true);
                    txtValue.requestFocus();
                    Toast valueEmpty = Toast.makeText(this, "Value field cannot be empty", Toast.LENGTH_SHORT);
                    valueEmpty.setGravity(Gravity.TOP, 50, 100);
                    valueEmpty.show();
                }
            } else {
                flag = false;
                txtProduct.setFocusable(true);
                txtProduct.requestFocus();
                Toast productEmpty = Toast.makeText(this, "Product field cannot be empty", Toast.LENGTH_SHORT);
                productEmpty.setGravity(Gravity.TOP, 50, 100);
                productEmpty.show();
            }
        } else {
            flag = false;
            txtCompany.setFocusable(true);
            txtCompany.requestFocus();
            Toast companyEmpty = Toast.makeText(this, "Company field cannot be empty", Toast.LENGTH_SHORT);
            companyEmpty.setGravity(Gravity.TOP, 50, 100);
            companyEmpty.show();
        }

        return flag;
    }

    private void saveCompetitorProduct() {
        String company = txtCompany.getText().toString();
        String product = txtProduct.getText().toString();
        String value = txtValue.getText().toString();
        String packSizes = txtPackSizes.getText().toString();
        String otherInfo = txtOtherInfo.getText().toString();
        String sendTo = txtSendTo.getText().toString();
        String timeStamp = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Log.w("Email address", sendTo);
        CompetitorProducts competitorProducts = new CompetitorProducts(this);
        competitorProducts.openWritableDatabase();
        competitorProducts.insertCompetitorProduct(company, product, value, packSizes, otherInfo, sendTo, timeStamp);
        competitorProducts.closeDatabase();
        competitorProducts.openReadableDatabase();
        List<String[]> competitorProductsArray = competitorProducts.getAllCompetitorProducts();
        competitorProducts.closeDatabase();


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        String repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();

        for (String[] s : competitorProductsArray) {

            Log.w("Row ID: ", s[0]);
            Log.w("Company: ", s[1]);
            Log.w("product: ", s[2]);
            Log.w("value: ", s[3]);
            Log.w("packSizes: ", s[4]);
            Log.w("otherInfo: ", s[5]);
            Log.w("sendTo: ", s[6]);
            Log.w("timeStamp: ", s[7]);
        }

        String emailText =
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">" +
                        "<tr>" +
                        "<td style=\"padding:20px 0 20px 0\" align=\"center\" valign=\"top\">" +
                        "<table style=\"border:1px solid #e0e0e0\" bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"650\">" +
                        "<tr>" +
                        "<td valign=\"top\">" +
                        "<h1 style=\"font-size:22px;font-weight:normal;line-height:22px;margin:0 0 11px 0\">Competitor Products</h1>" +
                        "<br/>" +
                        "<p style=\"font-size:12px;line-height:16px;margin:0\">" +
                        "The details are as follows:" +
                        "</p>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>" +
                        "<h2 style=\"font-size:18px;font-weight:normal;margin:0\">Sales Representative name:" + repName + "</h2>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "<br/>" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"500\" align=\"center\">" +
                        "<tr>" +
                        "<th style=\"font-size:13px;padding:5px 9px 6px 9px;line-height:1em\" align=\"left\" bgcolor=\"#EAEAEA\" width=\"200\">Company:</th>" +
                        "<th width=\"10\"></th>" +
                        "<td>&nbsp;</td>" +
                        "<td style=\"font-size:12px;padding:7px 9px 9px 9px;border-left:1px solid #eaeaea;border-bottom:1px solid #eaeaea;border-right:1px solid #eaeaea\" align=\"center\" bgcolor=\"#EAEAEA\">" +
                        company + "<br>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th style=\"font-size:13px;padding:5px 9px 6px 9px;line-height:1em\" align=\"left\" bgcolor=\"#EAEAEA\" width=\"200\">Product:</th>" +
                        "<th width=\"10\"></th>" +
                        "<td>&nbsp;</td>" +
                        "<td style=\"font-size:12px;padding:7px 9px 9px 9px;border-left:1px solid #eaeaea;border-bottom:1px solid #eaeaea;border-right:1px solid #eaeaea\" align=\"center\" bgcolor=\"#EAEAEA\">" +
                        product + "<br>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th style=\"font-size:13px;padding:5px 9px 6px 9px;line-height:1em\" align=\"left\" bgcolor=\"#EAEAEA\" width=\"200\">Value:</th>" +
                        "<th width=\"10\"></th>" +
                        "<td>&nbsp;</td>" +
                        "<td style=\"font-size:12px;padding:7px 9px 9px 9px;border-left:1px solid #eaeaea;border-bottom:1px solid #eaeaea;border-right:1px solid #eaeaea\" align=\"center\" bgcolor=\"#EAEAEA\">" +
                        value + "<br>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th style=\"font-size:13px;padding:5px 9px 6px 9px;line-height:1em\" align=\"left\" bgcolor=\"#EAEAEA\" width=\"200\">Pack Sizes:</th>" +
                        "<th width=\"10\"></th>" +
                        "<td>&nbsp;</td>" +
                        "<td style=\"font-size:12px;padding:7px 9px 9px 9px;border-left:1px solid #eaeaea;border-bottom:1px solid #eaeaea;border-right:1px solid #eaeaea\" align=\"center\" bgcolor=\"#EAEAEA\">" +
                        packSizes + "<br>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th style=\"font-size:13px;padding:5px 9px 6px 9px;line-height:1em\" align=\"left\" bgcolor=\"#EAEAEA\" width=\"200\">Other Info.</th>" +
                        "<th width=\"10\"></th>" +
                        "<td>&nbsp;</td>" +
                        "<td style=\"font-size:12px;padding:7px 9px 9px 9px;border-left:1px solid #eaeaea;border-bottom:1px solid #eaeaea;border-right:1px solid #eaeaea\" align=\"center\" bgcolor=\"#EAEAEA\">" +
                        otherInfo + "<br>" +
                        "</td>" +
                        "</tr>" +
                        "</table>";

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/html");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{sendTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Competitor Product");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(emailText));
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ArrayList<Uri> uriList = null;
        try {
            uriList = getUriListForImages();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        Log.w("URI SIZE", uriList.size() + "");


        try {
            finish();
            CompetitorProductsActivity.this.startActivity(Intent.createChooser(emailIntent, "Email:"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(CompetitorProductsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public String encodeHTML(String s) {
        s = s.replaceAll("#", "%23");
        s = s.replaceAll("%", "%25");
        return s;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!saveComplete) {
                if (checkDataForCancel()) {
                    clearImages();
                    finish();
                    Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                    startActivity(startItinerary);

                } else {
                    alertCancel.show();
                }
            } else {
                Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                startActivity(startItinerary);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("static-access")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            CompetitorProductsImages competitorProductsImages = new CompetitorProductsImages(this);
            int imageId = 0;
            try {

                competitorProductsImages.openReadableDatabase();

                String previousImage = competitorProductsImages.getLastImageNameForCompetitor(competitorId);
                competitorProductsImages.closeDatabase();
                if (previousImage.contentEquals("-1")) {
                    imageId = 0;
                } else {
                    imageId = Integer.parseInt(previousImage) + 1;
                    Log.w("imageId", imageId + "");
                }
            } catch (Exception e) {
                Log.w("getlast image ", e.toString());
            }
            Bundle extras = data.getExtras();
            competitorImage = (Bitmap) extras.get("data");

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            String deviceId = sharedPreferences.getString("DeviceId", "-1");


            String filename = deviceId + "_" + competitorId + "_" + imageId + ".jpg";
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Competitors" + File.separator + filename);

            try {
                FileOutputStream out = new FileOutputStream(path);
                competitorImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                competitorProductsImages.openWritableDatabase();
                long r = competitorProductsImages.insertCompetitorProductImage(competitorId, String.valueOf(imageId), filename, "false");

                Log.w("result", r + "");
                competitorProductsImages.closeDatabase();

            } catch (Exception e) {
                Log.w("PHOTO ERROR", e.toString());
            } finally {
                if (mExternalStorageAvailable && mExternalStorageWriteable) {
                    if (checkGalleryDirectory()) {
                        ArrayList<String> requiredImageIds = new ArrayList<String>();
                        requiredImageIds = getRequiredFileNames(competitorId);
                        if (!requiredImageIds.isEmpty()) {
                            setImageAdapter(this, requiredImageIds);
                        }
                    }
                } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
                    ExtStorage.makeText(CompetitorProductsActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
                    ExtStorage.setGravity(Gravity.TOP, 100, 100);
                    ExtStorage.show();
                } else if (!mExternalStorageAvailable) {
                    ExtStorage.makeText(CompetitorProductsActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
                    ExtStorage.setGravity(Gravity.TOP, 100, 100);
                    ExtStorage.show();
                }
            }
        }
    }

    private void getLastSavedCompetitorId() {
        // TODO Auto-generated method stub
        Sequence sequence = new Sequence(this);
        sequence.openReadableDatabase();
        String lastRowId = sequence.getLastRowId("competitor_products_images");
        sequence.closeDatabase();
        if (lastRowId.isEmpty()) {
            competitorId = "0";
        } else {
            competitorId = String.valueOf(Integer.parseInt(lastRowId) + 1);
        }


        Log.w("CUstomerIds SIze", competitorId);

    }

    private void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    @SuppressWarnings("unused")
    private void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("test", "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    @SuppressWarnings("unused")
    private void stopWatchingExternalStorage() {
        unregisterReceiver(mExternalStorageReceiver);
    }

    private boolean checkGalleryDirectory() {
        // TODO Auto-generated method stub
        try {
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Competitors");
            if (path.exists()) {
                Log.w("CustomerImageGallery", "Path Already Exist");
                return true;
            } else {
                Log.w("CustomerImageGallery", "Path Does not Exist.. Creating Path");
                path.mkdirs();
                return true;
            }

        } catch (Exception e) {
            Log.w("CustomerImageGallery: Unable to make path...", e.toString());
            return false;
        }
    }

    private void setImageAdapter(Activity a, ArrayList<String> requredImageIds) {
        // TODO Auto-generated method stub
        competitorProductsImageAdapter = new CompetitorProductsImageAdapter(this, requredImageIds);
        gViewCompetitorImages.setAdapter(competitorProductsImageAdapter);
    }

    private ArrayList<String> getRequiredFileNames(String competitorId) {

        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Competitors");
        boolean pathExists = path.exists();

        ArrayList<String> requiredImageIds = new ArrayList<String>();

        try {
            Log.w("PATH EXISTS", String.valueOf(pathExists));
            String[] files = path.list();

            if (files.length != 0) {
                Log.w("CUSTOMER ID", competitorId);
                Log.w("files.length", files.length + "");
                for (int i = 0; i < files.length; i++) {

                    String temp = files[i];
                    int firstUnderScore = temp.indexOf("_");
                    int secondUnderScore = temp.indexOf("_", firstUnderScore + 1);
                    Log.w("first" + firstUnderScore, "second" + secondUnderScore);
                    String customerIdfromFile = temp.substring(firstUnderScore + 1, secondUnderScore);

                    Log.w("customerIdFromFile", customerIdfromFile);

                    if (competitorId.contentEquals(customerIdfromFile)) {
                        requiredImageIds.add(temp);
                    }
                }
            } else {
                Toast.makeText(this, "This file location is empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.w("CustomerImageGallery: getRequiredFileNames", ex.toString());
        }

        if (!requiredImageIds.isEmpty()) {
            for (String imageId : requiredImageIds) {
                Log.w("REQUIRED IMAGE Ids", imageId.toString());
            }
        }
        return requiredImageIds;
    }

    private ArrayList<Uri> getUriListForImages() throws Exception {
        ArrayList<Uri> myList = new ArrayList<Uri>();
        String imageDirectoryPath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Competitors";
        String copy = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "tempChannel_Bridge_Competitors";
        ArrayList<String> requiredImageIds = new ArrayList<String>();
        requiredImageIds = getRequiredFileNames(competitorId);
        if (!requiredImageIds.isEmpty()) {
            for (String imageName : requiredImageIds) {
                try {
                    String tempFilename = copyPhoto(imageDirectoryPath, imageName); //get a temp filename from the copy method
                    File myFile = new File(copy, tempFilename);
                    Uri fileUri = Uri.fromFile(myFile);
                    myList.add(fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        File copyDir = new File(copy);
                        copyDir.delete();
                    } catch (Exception e) {
                        Log.w("Error Deleting temp file", e.toString());
                    }
                }
            }
        }
        return myList;
    }

    private String copyPhoto(String inputPath, String inputFile) {
        // TODO Auto-generated method stub
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            String outputPath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "tempChannel_Bridge_Competitors";

            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + File.separator + inputFile);
            out = new FileOutputStream(outputPath + File.separator + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
//	        new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return inputFile;
    }

    private void clearImages() {
        // TODO Auto-generated method stub
        ArrayList<String> requiredImageIdsForDp = new ArrayList<String>();
        requiredImageIdsForDp = getRequiredFileNames(competitorId);
        try {
            for (String fName : requiredImageIdsForDp) {
                File customerImageFile = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "DCIM" + File.separator
                                + "Channel_Bridge_Competitors" + File.separator
                                + fName);
                if (customerImageFile.exists()) {
                    customerImageFile.delete();
                }
            }
        } catch (Exception e) {
            Log.w("delete on cancel error", e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putBoolean("mExternalStorageAvailable", mExternalStorageAvailable);
        outState.putBoolean("mExternalStorageWriteable", mExternalStorageWriteable);
        outState.putBoolean("saveComplete", saveComplete);
        outState.putInt("competitorCameraData", competitorCameraData);
        outState.putInt("d", d);
        outState.putString("competitorId", competitorId);

    }

    private void setBundleData(Bundle bundlData) {

        mExternalStorageAvailable = bundlData.getBoolean("mExternalStorageAvailable");
        mExternalStorageWriteable = bundlData.getBoolean("mExternalStorageWriteable");
        saveComplete = bundlData.getBoolean("saveComplete");
        competitorCameraData = bundlData.getInt("competitorCameraData");
        d = bundlData.getInt("d");
        competitorId = bundlData.getString("competitorId");


    }

}
