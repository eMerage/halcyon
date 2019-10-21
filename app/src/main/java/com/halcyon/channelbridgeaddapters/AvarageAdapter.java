package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halcyon.channelbridge.AverageQuantity;
import com.halcyon.channelbridge.R;

import java.util.ArrayList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class AvarageAdapter extends RecyclerView.Adapter<AvarageAdapter.MyViewHolder> implements View.OnClickListener {

    Context mContext;
    ArrayList<AvarageItem> item;


    public AvarageAdapter(Context mContext, ArrayList<AvarageItem> albumList) {
        this.mContext = mContext;
        this.item = albumList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.averagequantitylist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.proCode.setText(item.get(position).pCode);
        holder.proDis.setText(item.get(position).pDescription);
        holder.proAVG.setText(item.get(position).pAVG);



      holder.layout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              ((AverageQuantity) mContext).getDetails(item.get(position).pCode,item.get(position).cusID);
          }
      });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public void onClick(View v) {
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView proCode, proDis, proAVG;
        RelativeLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            proCode = (TextView) itemView.findViewById(R.id.textView10);
            proDis = (TextView) itemView.findViewById(R.id.textView12);
            proAVG = (TextView) itemView.findViewById(R.id.textView13);
            layout =(RelativeLayout)itemView.findViewById(R.id.layoutavg);


        }


        @Override
        public void onClick(View v) {

        }

    }

}
