package com.halcyon.channelbridge;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.halcyon.channelbridge.R;

public class ProductRepStoreProductListAdapter extends BaseAdapter {

    ArrayList<String[]> productsList = new ArrayList<String[]>();
    private LayoutInflater inflater = null;
    private Activity activity;

    public ProductRepStoreProductListAdapter(Activity a, ArrayList<String[]> productData) {

        activity = a;
        productsList = productData;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return productsList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.product_rep_store_product_list, null);

        TextView tViewProductName = (TextView) vi.findViewById(R.id.tvProductName);
        TextView tViewProductCode = (TextView) vi.findViewById(R.id.tvProductCode);
        TextView tViewProductBatch = (TextView) vi.findViewById(R.id.tvBatch);
        TextView tViewProductCurrentStock = (TextView) vi.findViewById(R.id.tvCurrentStock);
        TextView tViewProductPrice = (TextView) vi.findViewById(R.id.tvPrice);
        TextView tViewProductExpiry = (TextView) vi.findViewById(R.id.tvExpiry);

        String[] tempData = productsList.get(position);

        tViewProductName.setText(tempData[5]);
        tViewProductCode.setText(tempData[0]);
        tViewProductBatch.setText(tempData[13]);
        tViewProductCurrentStock.setText(tempData[14]);
        tViewProductPrice.setText(tempData[10]);
        tViewProductExpiry.setText(tempData[15].substring(0, 10));

        return vi;
    }
}
