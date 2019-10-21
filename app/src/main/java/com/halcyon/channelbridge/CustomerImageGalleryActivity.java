package com.halcyon.channelbridge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.ImageGallery;

public class CustomerImageGalleryActivity extends Activity {

    static int cameraData = 0;
    TextView tViewCustomerName;
    Gallery gViewCustomerImages;
    ImageButton iBtnTakePicture;
    Button btnDone;
    CustomerImageGalleryAdapter customerImageGalleryAdapter;
    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    String customerId, previousActivity;
    Intent cameraIntent;
    Bitmap customerImage;
    ImageView iViewCustomerImage;
    String displayImage = "";
    private Toast ExtStorage;
    private Intent customerDetailsTabWidget = new Intent("com.halcyon.channelbridge.CUSTOMERDETAILSCOMMENTSTABWIDGET");
    private String rowId;
    boolean imageSelected;

    String picturePath;
    Uri selectedImage;
    private static int RESULT_LOAD_IMAGE = 1;

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_image_gallery);

        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        gViewCustomerImages = (Gallery) findViewById(R.id.gCustomerImages);
        iBtnTakePicture = (ImageButton) findViewById(R.id.ibTakePicture);
        btnDone = (Button) findViewById(R.id.bDone);
        iViewCustomerImage = (ImageView) findViewById(R.id.ivCustomerImage);


        updateExternalStorageState();
        getDataFromPreviousActivity();

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        }

        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            if (checkGalleryDirectory()) {
                ArrayList<String> requiredImageIds = new ArrayList<String>();
                requiredImageIds = getRequiredFileNames(customerId);

                Log.w("RequireD IDzzz", requiredImageIds.size() + "");
                if (!requiredImageIds.isEmpty()) {
                    setAdapter(this, requiredImageIds);
                }
            }
        } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
            ExtStorage.makeText(CustomerImageGalleryActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
            ExtStorage.setGravity(Gravity.TOP, 100, 100);
            ExtStorage.show();
        } else if (!mExternalStorageAvailable) {
            ExtStorage.makeText(CustomerImageGalleryActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
            ExtStorage.setGravity(Gravity.TOP, 100, 100);
            ExtStorage.show();
        }

        iBtnTakePicture.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, cameraData);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    //////////////
                    ArrayList<String> requiredImageIdsForDp = new ArrayList<String>();
                    requiredImageIdsForDp = getRequiredFileNames(customerId);
                    int dp = gViewCustomerImages.getSelectedItemPosition();
                    String imgName = requiredImageIdsForDp.get(dp);
                    displayImage = imgName;
                    ///////////////
                    // iViewCustomerImage.setOnClickListener(this);
                    if (previousActivity.contentEquals("AddCustomerActivity")) {
                        Intent addCustomerIntent = new Intent(getApplication(), AddCustomerActivity.class);

                        if (!displayImage.isEmpty()) {

                            ImageGallery imageGallery = new ImageGallery(CustomerImageGalleryActivity.this);
                            imageGallery.openReadableDatabase();
                            String primaryImage = imageGallery.getPrimaryImageforCustomerId(customerId);
                            imageGallery.closeDatabase();

                            if (primaryImage.contentEquals("null")) {
                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageByImageId(displayImage);
                                imageGallery.closeDatabase();
                            } else {
                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageFalse(primaryImage);
                                imageGallery.closeDatabase();

                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageByImageId(displayImage);
                                imageGallery.closeDatabase();
                            }
                            imageSelected = true;
                        }
                        finish();
                        addCustomerIntent.putExtra("imageSet", imageSelected);
                        // setResult(Activity.RESULT_OK,addCustomerIntent);
                        startActivity(addCustomerIntent);

                    } else if (previousActivity.contentEquals("NOTAddCustomerActivity")) {
//						Intent viewCustomerIntent = new Intent(getApplication(), CustomerDetailsComments_TabWidget.class);

                        if (!displayImage.isEmpty()) {
                            ImageGallery imageGallery = new ImageGallery(CustomerImageGalleryActivity.this);
                            imageGallery.openReadableDatabase();
                            String primaryImage = imageGallery.getPrimaryImageforCustomerId(customerId);
                            imageGallery.closeDatabase();

                            if (primaryImage.contentEquals("null")) {
                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageByImageId(displayImage);
                                imageGallery.closeDatabase();
                            } else {
                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageFalse(primaryImage);
                                imageGallery.closeDatabase();

                                imageGallery.openWritableDatabase();
                                imageGallery.setPrimaryImageByImageId(displayImage);
                                imageGallery.closeDatabase();
                            }


                            // Bitmap bitmap = ((BitmapDrawable)iBtnTakePicture.getDrawable()).getBitmap();
                            //   byte[]image= getBytes(bitmap);

                            Customers customers = new Customers(CustomerImageGalleryActivity.this);
                            customers.openWritableDatabase();
                            customers.setImageIdByCustomerPharmacyCode(customerId, displayImage);
                            //  customers.setBinaryImageIdByCustomerPharmacyCode(customerId, image);
                            customers.closeDatabase();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("Id", rowId);
                        bundle.putString("PharmacyId", customerId);
                        customerDetailsTabWidget.putExtras(bundle);
                        finish();
                        startActivity(customerDetailsTabWidget);

                    }
                } catch (Exception e) {
                    Log.w("error in back button", e.toString());
                }
            }
        });

        gViewCustomerImages.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Log.w("arg2", arg2 + "");
                ArrayList<String> requiredImageIds = new ArrayList<String>();
                requiredImageIds = getRequiredFileNames(customerId);
                String imageName = requiredImageIds.get(arg2);

                setImageToPreview(imageName);


            }
        });

	/*	iViewCustomerImage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

					Vibrator longPressVibe = (Vibrator) getApplication().getSystemService(getApplication().VIBRATOR_SERVICE) ;
					longPressVibe.vibrate(50);
				    AlertDialog.Builder imageClick = new AlertDialog.Builder(CustomerImageGalleryActivity.this);
				    imageClick.setTitle("Choose what you want to do:")
			           .setItems(new String[] {"Set as display picture", "Delete"}, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int which) {
					            switch (which) {
					            case 0:
					            	try {
						            	ArrayList<String> requiredImageIdsForDp = new ArrayList<String>();
						            	requiredImageIdsForDp = getRequiredFileNames(customerId);
						            	int dp = gViewCustomerImages.getSelectedItemPosition();
						            	String imgName = requiredImageIdsForDp.get(dp);
						            	displayImage = imgName;
					            	} catch (Exception e) {
					 	         	   Log.w("Error on image onclick switch case 0:", e.toString());
					 	            }
					            	break;
					            case 1:
					            	try {
						            	int img = gViewCustomerImages.getSelectedItemPosition();
						            	Log.w("POSITION", img + "");
						            	ArrayList<String> requiredImageIds = new ArrayList<String>();
				         				requiredImageIds = getRequiredFileNames(customerId);
				         				String imageName = requiredImageIds.get(img);

			         					File customerImageFile = new File(
			         							Environment.getExternalStorageDirectory() + File.separator
			         									+ "DCIM" + File.separator + "Channel_Bridge_Images"
			         									+ File.separator + imageName);
			         					if (customerImageFile.exists()) {
			         						boolean status = customerImageFile.delete();
			         						try {
	    		         						ImageGallery imageGallery = new ImageGallery(CustomerImageGalleryActivity.this);
	    		         						imageGallery.openWritableDatabase();
	    		         						imageGallery.deleteImageByCustomeridAndImageId(customerId, imageName);
	    		         						imageGallery.closeDatabase();
	    		         						displayImage = "";
	    		         					} catch (Exception e) {
	    		         						Log.w("error deleting from db", e.toString());
	    		         					}
			         						Log.w("File Deleted: ", status +  "");

			         					}
			         				} catch (Exception e) {
			         					Log.w("PHoto delete error", e.toString());
			         				} finally {
			         					if (mExternalStorageAvailable && mExternalStorageWriteable) {
			    		         			if (checkGalleryDirectory()) {
			    		         				ArrayList<String> newRequiredImageIds = new ArrayList<String>();
			    		         				newRequiredImageIds = getRequiredFileNames(customerId);
			    		         				if (!newRequiredImageIds.isEmpty()) {
			    		         					try {
			    		         						setAdapter(CustomerImageGalleryActivity.this, newRequiredImageIds);
				    		         					gViewCustomerImages.setSelected(true);
				    		         					gViewCustomerImages.setSelection(0);
				    		         					String imageid = newRequiredImageIds.get(0);
				    		         					setImageToPreview(imageid);
			    		         					} catch (Exception e) {
			    		         						Log.w("setadapter after delete", e.toString());
			    		         					}
			    		         				} else {
			    		         					setAdapter(CustomerImageGalleryActivity.this, newRequiredImageIds);
			    		         					iViewCustomerImage.setImageResource(R.drawable.unknown_image);
			    		         				}
			    		         			}
			    		         		} else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
			    		         			ExtStorage.makeText(CustomerImageGalleryActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
			    		         			ExtStorage.setGravity(Gravity.TOP, 100, 100);
			    		         			ExtStorage.show();
			    		         		} else if (!mExternalStorageAvailable) {
			    		         			ExtStorage.makeText(CustomerImageGalleryActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
			    		         			ExtStorage.setGravity(Gravity.TOP, 100, 100);
			    		         			ExtStorage.show();
			    		         		}
			         				}

					            	break;
					            }
				           }
				    });
				    imageClick.setOnCancelListener(new OnCancelListener() {

						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							if (mExternalStorageAvailable && mExternalStorageWriteable) {
			         			if (checkGalleryDirectory()) {
			         				ArrayList<String> requiredImageIds = new ArrayList<String>();
			         				requiredImageIds = getRequiredFileNames(customerId);
			         				if (!requiredImageIds.isEmpty()) {
			         					setAdapter(CustomerImageGalleryActivity.this, requiredImageIds);
			         					gViewCustomerImages.setSelected(true);
			         					gViewCustomerImages.setSelection(0);
			         					String imageName = requiredImageIds.get(0);
			         					setImageToPreview(imageName);
			         				}
			         			}
			         		} else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
			         			ExtStorage.makeText(CustomerImageGalleryActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
			         			ExtStorage.setGravity(Gravity.TOP, 100, 100);
			         			ExtStorage.show();
			         		} else if (!mExternalStorageAvailable) {
			         			ExtStorage.makeText(CustomerImageGalleryActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
			         			ExtStorage.setGravity(Gravity.TOP, 100, 100);
			         			ExtStorage.show();
			         		}
						}
					});
					imageClick.show();

			}
		});*/

    }

    @SuppressWarnings("static-access")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String previousImage = null;

        if (resultCode == RESULT_OK) {
            ImageGallery imageGallery = new ImageGallery(this);
            int imageId = 0;
            try {
                imageGallery.openReadableDatabase();
                previousImage = imageGallery.getLastImageNameForCustomer(customerId);
                imageGallery.closeDatabase();
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
            customerImage = (Bitmap) extras.get("data");


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String deviceId = sharedPreferences.getString("DeviceId", "-1");

            String filename = deviceId + "_" + customerId + "_" + imageId + ".jpg";
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images" + File.separator + filename);

            try {
                FileOutputStream out = new FileOutputStream(path);
                customerImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                imageGallery.openWritableDatabase();
                long r = imageGallery.insertNewImage(customerId, String.valueOf(imageId), filename, "false", "false");
                Log.w("result", r + "");
                imageGallery.closeDatabase();

            } catch (Exception e) {
                Log.w("PHOTO ERROR", e.toString());
            } finally {
                if (mExternalStorageAvailable && mExternalStorageWriteable) {
                    if (checkGalleryDirectory()) {
                        ArrayList<String> requiredImageIds = new ArrayList<String>();
                        requiredImageIds = getRequiredFileNames(customerId);

                        if (!requiredImageIds.isEmpty()) {
                            setAdapter(this, requiredImageIds);
                            gViewCustomerImages.setSelected(true);
                            gViewCustomerImages.setSelection(0);
                            String imageName = requiredImageIds.get(0);
                            setImageToPreview(imageName);
                        }
                    }
                } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
                    ExtStorage.makeText(CustomerImageGalleryActivity.this, "The External Storage is not writable", Toast.LENGTH_SHORT);
                    ExtStorage.setGravity(Gravity.TOP, 100, 100);
                    ExtStorage.show();
                } else if (!mExternalStorageAvailable) {
                    ExtStorage.makeText(CustomerImageGalleryActivity.this, "External Storage not available", Toast.LENGTH_SHORT);
                    ExtStorage.setGravity(Gravity.TOP, 100, 100);
                    ExtStorage.show();
                }
            }
        }
    }

  /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      //  if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            iViewCustomerImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));



      //  }
    }*/


    private void getDataFromPreviousActivity() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        try {
            if (extras.containsKey("customerId")) {
                customerId = extras.getString("customerId");
                previousActivity = "AddCustomerActivity";
            } else if (extras.containsKey("PharmacyId")) {
                customerId = extras.getString("PharmacyId");
                if (extras.containsKey("Id")) {
                    rowId = extras.getString("Id");
                    Log.w("Row Id From Customer Details", "Id: " + rowId);
                }
                previousActivity = "NOTAddCustomerActivity";
            }

        } catch (Exception e) {
            Log.w("Error getting data from previous activity", e.toString());
        }
    }

    private boolean checkGalleryDirectory() {
        // TODO Auto-generated method stub
        try {
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images");
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

    private void setAdapter(Activity a, ArrayList<String> requredImageIds) {
        // TODO Auto-generated method stub
        customerImageGalleryAdapter = new CustomerImageGalleryAdapter(a, requredImageIds);
          gViewCustomerImages.setAdapter(customerImageGalleryAdapter);
          gViewCustomerImages.setSelected(true);
          gViewCustomerImages.setSelection(0);
        ArrayList<String> requiredImageIds = new ArrayList<String>();
        requiredImageIds = getRequiredFileNames(customerId);
        if (!requiredImageIds.isEmpty()) {
            String imageName = requiredImageIds.get(0);
            setImageToPreview(imageName);
        } else {
            iViewCustomerImage.setImageResource(R.drawable.unknown_image);
        }


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

    private ArrayList<String> getRequiredFileNames(String customerId) {
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images");
        boolean pathExists = path.exists();

        ArrayList<String> requiredImageIds = new ArrayList<String>();
        try {
            Log.w("PATH EXISTS", String.valueOf(pathExists));
            String[] files = path.list();

            if (files.length != 0) {
                Log.w("CUSTOMER ID", customerId);
                Log.w("files.length", files.length + "");
                for (int i = 0; i < files.length; i++) {

                    String temp = files[i];
                    int firstUnderScore = temp.indexOf("_");
                    int secondUnderScore = temp.indexOf("_", firstUnderScore + 1);
                    Log.w("first" + firstUnderScore, "second" + secondUnderScore);
                    String customerIdfromFile = temp.substring(firstUnderScore + 1, secondUnderScore);

                    Log.w("customerIdFromFile", customerIdfromFile);

                    if (customerId.contentEquals(customerIdfromFile)) {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            try {
                if (previousActivity.contentEquals("AddCustomerActivity")) {
                    Intent addCustomerIntent = new Intent(getApplication(), AddCustomerActivity.class);

                    if (!displayImage.isEmpty()) {
                        ImageGallery imageGallery = new ImageGallery(CustomerImageGalleryActivity.this);
                        imageGallery.openReadableDatabase();
                        String primaryImage = imageGallery.getPrimaryImageforCustomerId(customerId);
                        imageGallery.closeDatabase();

                        if (primaryImage.contentEquals("null")) {
                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageByImageId(displayImage);
                            imageGallery.closeDatabase();
                        } else {
                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageFalse(primaryImage);
                            imageGallery.closeDatabase();

                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageByImageId(displayImage);
                            imageGallery.closeDatabase();
                        }
                    }

                    finish();
                    startActivity(addCustomerIntent);

                } else if (previousActivity.contentEquals("NOTAddCustomerActivity")) {
//					Intent viewCustomerIntent = new Intent(getApplication(), CustomerDetailsComments_TabWidget.class);
                    if (!displayImage.isEmpty()) {
                        ImageGallery imageGallery = new ImageGallery(CustomerImageGalleryActivity.this);
                        imageGallery.openReadableDatabase();
                        String primaryImage = imageGallery.getPrimaryImageforCustomerId(customerId);
                        imageGallery.closeDatabase();

                        if (primaryImage.contentEquals("null")) {
                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageByImageId(displayImage);
                            imageGallery.closeDatabase();
                        } else {
                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageFalse(primaryImage);
                            imageGallery.closeDatabase();

                            imageGallery.openWritableDatabase();
                            imageGallery.setPrimaryImageByImageId(displayImage);
                            imageGallery.closeDatabase();
                        }
                        Bitmap bitmap = ((BitmapDrawable) iBtnTakePicture.getDrawable()).getBitmap();
                        byte[] image = getBytes(bitmap);
                        Customers customers = new Customers(CustomerImageGalleryActivity.this);
                        customers.openWritableDatabase();
                        customers.setImageIdByCustomerPharmacyCode(customerId, displayImage);
                        customers.setBinaryImageIdByCustomerPharmacyCode(customerId, image);
                        customers.closeDatabase();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("Id", rowId);
                    bundle.putString("PharmacyId", customerId);
                    customerDetailsTabWidget.putExtras(bundle);
                    finish();
                    startActivity(customerDetailsTabWidget);

                }
            } catch (Exception e) {
                Log.w("error in back button", e.toString());
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setImageToPreview(String imageName) {
        try {


            byte[] image = new byte[0];
            Customers data = new Customers(this);
            data.openReadableDatabase();
            image = data.getByteArrayImage(customerId);
            data.closeDatabase();

            //  Bitmap bm = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            //  iViewCustomerImage.setImageBitmap(bm);


            File customerImageFile = new File(Environment.getExternalStorageDirectory() + File.separator
                    + "DCIM" + File.separator + "Channel_Bridge_Images"
                    + File.separator + imageName);

            if (customerImageFile.exists()) {
                try {
                    iViewCustomerImage.setImageBitmap(decodeSampledBitmapFromResource(String.valueOf(customerImageFile), 450, 500));
                } catch (IllegalArgumentException e) {
                    Log.w("Illegal argument exception", e.toString());
                } catch (OutOfMemoryError e) {
                    Log.w("Out of memory error :(", e.toString());
                }

            } else {
                iViewCustomerImage.setImageResource(R.drawable.unknown_image);
            }
        } catch (Exception e) {
            Log.w("Error getting image file", e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putBoolean("mExternalStorageAvailable", mExternalStorageAvailable);
        outState.putBoolean("mExternalStorageWriteable", mExternalStorageWriteable);
        outState.putInt("cameraData", cameraData);
        outState.putString("customerId", customerId);
        outState.putString("previousActivity", previousActivity);
        outState.putString("displayImage", displayImage);

    }

    private void setBundleData(Bundle bundlData) {

        mExternalStorageAvailable = bundlData.getBoolean("mExternalStorageAvailable");
        mExternalStorageWriteable = bundlData.getBoolean("mExternalStorageWriteable");
        cameraData = bundlData.getInt("cameraData");
        customerId = bundlData.getString("customerId");
        previousActivity = bundlData.getString("previousActivity");
        displayImage = bundlData.getString("displayImage");


    }
}
