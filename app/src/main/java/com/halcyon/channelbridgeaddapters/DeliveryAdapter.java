package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halcyon.Entity.DeliveryEntity;
import com.halcyon.Entity.TargetEntity;
import com.halcyon.channelbridge.R;

import java.util.ArrayList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements View.OnClickListener {

    Context mContext;
    ArrayList<DeliveryEntity> item;


    public DeliveryAdapter(Context mContext, ArrayList<DeliveryEntity> albumList) {
        this.mContext = mContext;
        this.item = albumList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_delivery, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        DeliveryEntity deliveryEntity = item.get(position);


        holder.textInvoicDaue.setText(deliveryEntity.getInvoiceDate().substring(0,10));
        holder.textCustomer.setText(deliveryEntity.getCustomer());
        holder.textInvNumber.setText(deliveryEntity.getInvoiceNumber());
        holder.textInvValue.setText(deliveryEntity.getInvoiceValue());

        String status="";

        if(deliveryEntity.getDeliveryStatus()==1){
            status= "Part Delivered";
            holder.imageViewSatus.setImageResource(R.drawable.ic_delivery_status_part_deliverd);
        }else if(deliveryEntity.getDeliveryStatus()==2){
            status= "Delivered";
            holder.imageViewSatus.setImageResource(R.drawable.ic_delivery_status_deliverd);
        }else {
            status= "Not Delivered";
            holder.imageViewSatus.setImageResource(R.drawable.ic_delivery_status_not_deliverd);
        }

        holder.textStatus.setText(status);
        holder.textReason.setText(deliveryEntity.getInvoiceReason());

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public void onClick(View v) {
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView textInvoicDaue, textCustomer, textInvNumber, textInvValue, textStatus,textReason;
        ImageView imageViewSatus;



        public MyViewHolder(View itemView) {
            super(itemView);
            textInvoicDaue = (TextView) itemView.findViewById(R.id.textview_invoice_date);
            textCustomer = (TextView) itemView.findViewById(R.id.textview_customer);
            textInvNumber = (TextView) itemView.findViewById(R.id.textview_invoice_number);
            textInvValue = (TextView) itemView.findViewById(R.id.textview_invoice_value);
            textStatus = (TextView) itemView.findViewById(R.id.textview_delivery_status);
            imageViewSatus = (ImageView) itemView.findViewById(R.id.imageView_status);
            textReason = (TextView) itemView.findViewById(R.id.textview_reason);

        }


        @Override
        public void onClick(View v) {

        }

    }

}
