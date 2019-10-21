package com.halcyon.channelbridge;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.halcyon.channelbridge.R;

public class CustomerImageGalleryAdapter extends BaseAdapter {

    int imageBackground;
    private Context mContext;
    private ArrayList<String> requiredImageIds = new ArrayList<String>();

    public CustomerImageGalleryAdapter(Activity c, ArrayList<String> fileNames) {
        mContext = c;


        requiredImageIds = fileNames;
        TypedArray ta = c.obtainStyledAttributes(R.styleable.Gallery1);
        imageBackground = ta.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 1);
        ta.recycle();

    }

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

    public int getCount() {
        // TODO Auto-generated method stub
        return requiredImageIds.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(new Gallery.LayoutParams(150, 120));

        String imageId = requiredImageIds.get(position);
        try {
            File customerImageFile = new File(
                    Environment.getExternalStorageDirectory() + File.separator
                            + "DCIM" + File.separator + "Channel_Bridge_Images"
                            + File.separator + imageId);
            if (customerImageFile.exists()) {

                try {
                    iv.setImageBitmap(decodeSampledBitmapFromResource(
                            String.valueOf(customerImageFile), 200, 200));

                    iv.setBackgroundResource(imageBackground);
                } catch (IllegalArgumentException e) {
                    Log.w("Illegal argument exception", e.toString());
                } catch (OutOfMemoryError e) {
                    Log.w("Out of memory error :(", e.toString());
                }

            } else {
                iv.setImageResource(R.drawable.unknown_image);
            }
        } catch (Exception e) {
            Log.w("Error getting image file", e.toString());
        }

        return iv;
    }

}
