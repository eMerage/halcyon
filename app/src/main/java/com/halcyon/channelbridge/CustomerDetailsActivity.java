package com.halcyon.channelbridge;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.ImageGallery;
import com.halcyon.channelbridgedb.Itinerary;

//
public class CustomerDetailsActivity extends Activity {

    String name, address, tele, nic, rowId;
    TextView tViewDate, tViewName, tViewAddress,
            tViewTelephone, tViewFax, tViewCustomerStatus, tViewEmail,
            tViewWeb, tViewBrNo, tViewOwnerContact, tViewOwnerWifeBday,
            tViewPharmacistName, tViewPurchasingOfficer, tViewNoStaff;
    ImageView iViewCustomerImage;
    Intent itineraryListIntent = new Intent(
            "com.halcyon.channelbridge.ITINERARYLIST");
    private Button btnDone, btnTakePictures;
    private String pharmacyId;

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


//	public void setDate() {
//		String currentDate = DateFormat.getDateInstance().format(new Date());
//
//		tViewDate.setText(currentDate);
//	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_details);

//		tViewTitleCustomer = (TextView) findViewById(R.id.tvTitleCustomerName);
        //tViewDate = (TextView) findViewById(R.id.tvDate);
        iViewCustomerImage = (ImageView) findViewById(R.id.ivCustomerImage);
        tViewName = (TextView) findViewById(R.id.tvCustomerName);
        tViewAddress = (TextView) findViewById(R.id.tvAddress);
        tViewTelephone = (TextView) findViewById(R.id.tvTelephone);
        tViewFax = (TextView) findViewById(R.id.tvFax);
        tViewCustomerStatus = (TextView) findViewById(R.id.tvCustomerStatus);
        tViewEmail = (TextView) findViewById(R.id.tvEmail);
        tViewWeb = (TextView) findViewById(R.id.tvWeb);
        tViewBrNo = (TextView) findViewById(R.id.tvBrNo);
        tViewOwnerContact = (TextView) findViewById(R.id.tvOwnerContact);
        tViewOwnerWifeBday = (TextView) findViewById(R.id.tvOwnerWifeBday);
        tViewPharmacistName = (TextView) findViewById(R.id.tvPharmacistName);
        tViewPurchasingOfficer = (TextView) findViewById(R.id.tvPurchasingOfficer);
        tViewNoStaff = (TextView) findViewById(R.id.tvNoStaff);
        btnTakePictures = (Button) findViewById(R.id.bTakeImages);

        btnDone = (Button) findViewById(R.id.bDone);

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        }

        //setDate();
        setData();

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                startActivity(itineraryListIntent);
                finish();

            }
        });

        btnTakePictures.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle extras = new Bundle();
                extras.putString("PharmacyId", pharmacyId);
                Intent startGallery = new Intent(getApplication(), CustomerImageGalleryActivity.class);
                startGallery.putExtras(extras);
                startActivity(startGallery);
                finish();

            }
        });
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

    public void setData() {

        try {
            Bundle extras = getIntent().getExtras();
            String rowId = extras.getString("Id");
            pharmacyId = extras.getString("PharmacyId");

            setCustomerImage();


            Itinerary itinerary = new Itinerary(this);
            itinerary.openReadableDatabase();
            String status = itinerary.getItineraryStatus(rowId);
            itinerary.closeDatabase();

            if (status.contentEquals("true")) {
                itinerary.openReadableDatabase();
                String[] itnDetails = itinerary.getItineraryDetailsForTemporaryCustomer(rowId);
                itinerary.closeDatabase();
                String address = itnDetails[2] + ", " + itnDetails[3] + ", " + itnDetails[4] + ", " + itnDetails[5];
                tViewName.setText(itnDetails[0]);
                tViewAddress.setText(itnDetails[1]);
                tViewAddress.setText(address);
                tViewTelephone.setText(itnDetails[6]);
                pharmacyId = itnDetails[8];
                String[] imgWord = pharmacyId.split("_");
                byte[] image = new byte[0];
                CustomersPendingApproval customersPendingApproval = new CustomersPendingApproval(this);
                customersPendingApproval.openReadableDatabase();
                String[] customerDetails = customersPendingApproval.getCustomerDetailsByPharmacyId(pharmacyId);
                image = customersPendingApproval.getByteArrayImage(imgWord[1]);
                customersPendingApproval.closeDatabase();
                String primaryImage = null;

                tViewWeb.setText(customerDetails[9]);
                tViewBrNo.setText(customerDetails[11]);
                tViewFax.setText(customerDetails[7]);
                tViewEmail.setText(customerDetails[8]);
                tViewCustomerStatus.setText(customerDetails[10]);
                tViewOwnerContact.setText(customerDetails[13]);
                tViewOwnerWifeBday.setText(customerDetails[14]);
                tViewPharmacistName.setText(customerDetails[16]);
                tViewPurchasingOfficer.setText(customerDetails[17]);
                tViewNoStaff.setText(customerDetails[18]);

                try {
                    /*ImageGallery imageGallery = new ImageGallery(this);
					imageGallery.openReadableDatabase();
					primaryImage = imageGallery.getPrimaryImageforCustomerId(itnDetails[7]);
					imageGallery.closeDatabase();*/
                    Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                    iViewCustomerImage.setImageBitmap(bm);
                } catch (Exception e) {
                    Log.w("Unable to get display pic", e.toString());
                }

                try {
                    Log.w("Primary Image", primaryImage + "");
                    File customerImageFile = new File(
                            Environment.getExternalStorageDirectory() + File.separator
                                    + "DCIM" + File.separator + "Channel_Bridge_Images"
                                    + File.separator + primaryImage);

				/*	if (customerImageFile.exists()) {

						try {
							iViewCustomerImage.setImageBitmap(decodeSampledBitmapFromResource(
											String.valueOf(customerImageFile), 400, 550));
						} catch (IllegalArgumentException e) {
							Log.w("Illegal argument exception", e.toString());
						} catch (OutOfMemoryError e) {
							Log.w("Out of memory error :(", e.toString());
						}

					} else {
						iViewCustomerImage.setImageResource(R.drawable.unknown_image);
					}*/
                } catch (Exception e) {
                    Log.w("Error setting image file", e.toString());
                    iViewCustomerImage.setImageResource(R.drawable.unknown_image);
                }

            } else if (status.contentEquals("false")) {
                itinerary.openReadableDatabase();
                String[] itnDetails = itinerary.getItineraryDetailsById(rowId);
                itinerary.closeDatabase();
                String primaryImage = itnDetails[7];
                pharmacyId = itnDetails[4];
                byte[] image = new byte[0];
                Customers customersObject = new Customers(this);
                customersObject.openReadableDatabase();
                String[] customerDetails = customersObject.getCustomerDetailsByPharmacyId(pharmacyId);
                image = customersObject.getByteArrayImage(pharmacyId);
                customersObject.closeDatabase();

                //0 - rowid
                //1 - pharmacyId
                //2 - pharmacyCode
                //3 - dealerId
                //4 - companyCode
                //5 - customerName
                //6 - address
                //7 - area
                //8 - town
                //9 - district
                //10 - telephone
                //11 - fax
                //12 - email
                //13 - customerStatus
                //14 - creditLimit
                //16 - currentCredit
                //16 - creditExpiryDate
                //17 - creditDuration
                //18 - vatNo
                //19 - status


                tViewName.setText(customerDetails[5]);
                tViewAddress.setText(customerDetails[6]);
                tViewTelephone.setText(customerDetails[10]);
                tViewFax.setText(customerDetails[11]);
                tViewCustomerStatus.setText(customerDetails[13]);
                tViewEmail.setText(customerDetails[12]);
                tViewWeb.setText("");
                tViewBrNo.setText(customerDetails[2]);
                tViewOwnerContact.setText("");
                tViewOwnerWifeBday.setText("");
                tViewPharmacistName.setText("");
                tViewPurchasingOfficer.setText("");
                tViewNoStaff.setText("");
                Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                iViewCustomerImage.setImageBitmap(bm);
                try {
                    Log.w("Primary Image", primaryImage + "");
                    File customerImageFile = new File(
                            Environment.getExternalStorageDirectory() + File.separator
                                    + "DCIM" + File.separator + "Channel_Bridge_Images"
                                    + File.separator + primaryImage);

				/*	if (customerImageFile.exists()) {

						try {
							iViewCustomerImage.setImageBitmap(decodeSampledBitmapFromResource(
											String.valueOf(customerImageFile), 400, 550));
						} catch (IllegalArgumentException e) {
							Log.w("Illegal argument exception", e.toString());
						} catch (OutOfMemoryError e) {
							Log.w("Out of memory error :(", e.toString());
						}

					} else {
						iViewCustomerImage.setImageResource(R.drawable.unknown_image);
					}*/
                } catch (Exception e) {
                    Log.w("Error setting image file", e.toString());
                    iViewCustomerImage.setImageResource(R.drawable.unknown_image);
                }
            }
        } catch (Exception e) {
            Log.w("Unable to set data", e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putString("name", name);
        outState.putString("address", address);
        outState.putString("tele", tele);
        outState.putString("nic", nic);
        outState.putString("rowId", rowId);


    }

    private void setBundleData(Bundle bundlData) {

        name = bundlData.getString("name");
        address = bundlData.getString("address");
        tele = bundlData.getString("tele");
        nic = bundlData.getString("nic");
        rowId = bundlData.getString("rowId");

    }


    private void setCustomerImage() {
        String primaryImage = null;
        try {
            ImageGallery imageGallery = new ImageGallery(this);
            imageGallery.openReadableDatabase();
            primaryImage = imageGallery.getPrimaryImageforCustomerId(pharmacyId);
            imageGallery.closeDatabase();
        } catch (Exception e) {
            Log.w("Unable to get display pic", e.toString());
        }

        try {
            Log.w("Primary Image", primaryImage + "");
            if (primaryImage != null) {
                File customerImageFile = new File(
                        Environment.getExternalStorageDirectory() + File.separator
                                + "DCIM" + File.separator + "Channel_Bridge_Images"
                                + File.separator + primaryImage);

                if (customerImageFile.exists()) {

                    try {
                        iViewCustomerImage.setImageBitmap(decodeSampledBitmapFromResource(
                                String.valueOf(customerImageFile), 400, 550));
                    } catch (IllegalArgumentException e) {
                        Log.w("Illegal argument exception", e.toString());
                    } catch (OutOfMemoryError e) {
                        Log.w("Out of memory error :(", e.toString());
                    }

                } else {
                    iViewCustomerImage.setImageResource(R.drawable.unknown_image);
                }
            }
        } catch (Exception e) {
            Log.w("Error setting image file", e.toString());
        }
    }

}
