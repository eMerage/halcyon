package com.halcyon.channelbridge;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.halcyon.channelbridge.R;

public class RepStoreUnloadListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String[]> unloadHistoryList;


    public RepStoreUnloadListAdapter(Context c, ArrayList<String[]> history) {
        context = c;
        unloadHistoryList = history;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return unloadHistoryList.size();
    }

    public Object getItem(int arg0) {
        return unloadHistoryList.get(arg0);
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.unload_popup_list, null);
        }

        ImageView iViewUnloadStatus = (ImageView) vi.findViewById(R.id.ivUnloadStatus);
        TextView tViewUnloadDate = (TextView) vi.findViewById(R.id.tvDate);
        TextView tViewUnloadQty = (TextView) vi.findViewById(R.id.tvQuantity);


        String[] unloadDetails = unloadHistoryList.get(position);

        //TODO - set the unload details indexes right

        tViewUnloadDate.setText(unloadDetails[6].substring(0, 10));
        tViewUnloadQty.setText(unloadDetails[4]);


//		TODO - set the images right 
        switch (Integer.parseInt(unloadDetails[5])) {
            case 0:
                iViewUnloadStatus.setImageResource(R.drawable.pending);
                break;
            case 1:
                iViewUnloadStatus.setImageResource(R.drawable.sync_refresh);
                break;
            case 2:
                iViewUnloadStatus.setImageResource(R.drawable.accept);
                break;
            case 3:
                iViewUnloadStatus.setImageResource(R.drawable.content_remove);
                break;
        }

        return vi;
    }

}
