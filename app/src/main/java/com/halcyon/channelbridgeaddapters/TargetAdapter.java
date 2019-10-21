package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halcyon.Entity.TargetEntity;
import com.halcyon.channelbridge.ManualSync;
import com.halcyon.channelbridge.R;
import com.halcyon.helpModel.TextViewFontAwesome;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;


/**
 * Created by Himanshu on 4/10/2015.
 */
public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.MyViewHolder> implements View.OnClickListener {

    Context mContext;
    ArrayList<TargetEntity> item;


    public TargetAdapter(Context mContext, ArrayList<TargetEntity> albumList) {
        this.mContext = mContext;
        this.item = albumList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_product_target, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        TargetEntity targetEntity = item.get(position);

        holder.textPrinciple.setText(targetEntity.getPrinciple());
        holder.textProduct.setText(targetEntity.getProductName());

        holder.textTarget.setText(String.valueOf(targetEntity.getTarget()));
        holder.textTarget.setGravity(Gravity.RIGHT);
        holder.textAchievement.setText(String.valueOf(targetEntity.getAchievement()));
        holder.textAchievement.setGravity(Gravity.RIGHT);
        holder.textDifferent.setText(targetEntity.getDifferent());
        holder.textDifferent.setGravity(Gravity.RIGHT);
        holder.textMonthlyAch.setText(String.format("%.2f", targetEntity.getAchievementPresentage())+" %");
        holder.textMonthlyAch.setGravity(Gravity.RIGHT);

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public void onClick(View v) {
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView textPrinciple, textProduct, textTarget, textAchievement, textDifferent, textMonthlyAch;



        public MyViewHolder(View itemView) {
            super(itemView);
            textPrinciple = (TextView) itemView.findViewById(R.id.textview_principle);
            textProduct = (TextView) itemView.findViewById(R.id.textview_product);
            textTarget = (TextView) itemView.findViewById(R.id.textview_targent);
            textAchievement = (TextView) itemView.findViewById(R.id.textview_achievement);
            textDifferent = (TextView) itemView.findViewById(R.id.textview_different);
            textMonthlyAch = (TextView) itemView.findViewById(R.id.textview_achievement_presentage);

        }


        @Override
        public void onClick(View v) {

        }

    }

}
