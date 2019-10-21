package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halcyon.channelbridge.ManualSync;
import com.halcyon.channelbridge.R;
import com.halcyon.helpModel.TextViewFontAwesome;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class ManualSyncRecyAdapter extends RecyclerView.Adapter<ManualSyncRecyAdapter.MyViewHolder> implements View.OnClickListener {

    Context mContext;
    ArrayList<ManualSyncList> item;


    public ManualSyncRecyAdapter(Context mContext, ArrayList<ManualSyncList> albumList) {
        this.mContext = mContext;
        this.item = albumList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manual_syncing, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ManualSyncList mItems=item.get(position);

     if(mItems.getSynctype()==0){
            holder.textSyncType.setText(R.string.fw_cloud_download);
           holder.textSyncType.setTextColor(mContext.getResources().getColor(R.color.myBlue));
            holder.layoutToUploade.setVisibility(View.INVISIBLE);
            holder.textViewToDone.setVisibility(View.INVISIBLE);
            holder.textViewDone.setText("Download");
        }else {
            holder.textSyncType.setText(R.string.fw_cloud_upload);
            holder.textSyncType.setTextColor(mContext.getResources().getColor(R.color.myGreen));
         holder.layoutToUploade.setVisibility(View.VISIBLE);
         holder.textViewToDone.setVisibility(View.VISIBLE);
         holder.textViewDone.setText("Uploaded");
        }
        if(mItems.getProgressStatus()==1){
            holder.progressBar.setVisibility(View.VISIBLE);
        }else {
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        if(position==0 || position ==1 || position ==3){
            holder.textViewDone.setVisibility(View.VISIBLE);
            holder.layoutDownloaded.setVisibility(View.VISIBLE);
        }else {
            holder.textViewDone.setVisibility(View.GONE);
            holder.layoutDownloaded.setVisibility(View.GONE);
        }

        holder.textTitel.setText(mItems.getTitel());
        holder.textSubTitel.setText(mItems.getSubtitel());
        holder.textDone.setText(String.valueOf(mItems.getNumberOfDone()));
        holder.textTodone.setText(String.valueOf(mItems.getNumbaerOfToDone()));

        if(mItems.getSyncResult() ==null || mItems.getSyncResult().equals("null")){
            holder.textResult.setText("");
        }else {
            holder.textResult.setText(mItems.getSyncResult());
        }
        if(mItems.getProgressColor()==0){
            holder.progressBar.setIndicatorColor(mContext.getResources().getColor(R.color.myBlue));
        }else {
            holder.progressBar.setIndicatorColor(mContext.getResources().getColor(R.color.myGreen));
        }



        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ManualSync) mContext).RecyclerViewOnItemClick(position);
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

        AVLoadingIndicatorView progressBar;
        TextViewFontAwesome textSyncType;
        TextView textTitel, textSubTitel, textDone, textTodone, textViewDone, textViewToDone,textResult;
        RelativeLayout layoutToUploade,layoutMain,layoutDownloaded;


        public MyViewHolder(View itemView) {
            super(itemView);

            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar);
            textTitel = (TextView) itemView.findViewById(R.id.textView20);
            textSubTitel = (TextView) itemView.findViewById(R.id.textView22);
            textDone = (TextView) itemView.findViewById(R.id.textView_done);
            textTodone = (TextView) itemView.findViewById(R.id.textView_todone);
            textSyncType = (TextViewFontAwesome) itemView.findViewById(R.id.textView26);
            textViewDone = (TextView) itemView.findViewById(R.id.textView44);
            textViewToDone = (TextView) itemView.findViewById(R.id.textView27);
            textResult = (TextView) itemView.findViewById(R.id.textView23);
            layoutToUploade = (RelativeLayout) itemView.findViewById(R.id.relativeLayout25);
            layoutMain = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutMain);
            layoutDownloaded = (RelativeLayout) itemView.findViewById(R.id.relativeLayout24);




        }


        @Override
        public void onClick(View v) {

        }

    }

}
