package com.halcyon.channelbridge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.halcyon.channelbridge.R;

public class PriceListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Activity activity;
    List<String[]> productList = new ArrayList<String[]>();


    public PriceListAdapter(Activity a, List<String[]> products) {
        activity = a;
        productList = products;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return productList.size();
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
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.price_list_adapter_view, parent, false);

        TextView tViewProductName = (TextView) vi.findViewById(R.id.tvProductName);
        TextView tViewProductCode = (TextView) vi.findViewById(R.id.tvProductCode);
        TextView tViewProductPrinciple = (TextView) vi.findViewById(R.id.tvPrinciple);
        TextView tViewProductWholeSalePrice = (TextView) vi.findViewById(R.id.tvWholeSalePrice);
        TextView tViewProductRetailPrice = (TextView) vi.findViewById(R.id.tvRetailPrice);

        String[] temp = productList.get(position);

        tViewProductName.setText(temp[8]);
        tViewProductCode.setText(temp[2]);
        tViewProductPrinciple.setText(temp[11]);
        tViewProductWholeSalePrice.setText(temp[13]);
        tViewProductRetailPrice.setText(temp[14]);


        return vi;
    }

}
