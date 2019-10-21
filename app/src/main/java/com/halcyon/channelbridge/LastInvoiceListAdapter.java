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

public class LastInvoiceListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<String[]> invoiceList = new ArrayList<String[]>();

    public LastInvoiceListAdapter(Activity a, ArrayList<String[]> iList) {
        activity = a;
        invoiceList = iList;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return invoiceList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.last_invoice_list, parent, false);

        TextView tViewInvoiceNumber = (TextView) vi.findViewById(R.id.tvInvoiceNumber);
        TextView tViewInvoiceDate = (TextView) vi.findViewById(R.id.tvInvoiceDate);
        TextView tViewInvoiceValue = (TextView) vi.findViewById(R.id.tvInvoiceValue);

        String[] invoice = invoiceList.get(position);
        tViewInvoiceNumber.setText(invoice[0]);
        tViewInvoiceDate.setText(invoice[11].substring(0, 10));
        tViewInvoiceValue.setText(invoice[3]);

        return vi;
    }

}
