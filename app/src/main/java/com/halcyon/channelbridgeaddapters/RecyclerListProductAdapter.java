package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.halcyon.Entity.TempInvoiceStock;
import com.halcyon.channelbridge.InvoiceGen1Alternate;
import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;

/**
 * Created by Himanshu on 6/8/2016.
 */
public class RecyclerListProductAdapter extends RecyclerView.Adapter<RecyclerListProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TempInvoiceStock> albumList;
    TemporaryInvoice tempInvoiceStockController;
    private Boolean iswebApprovalActive = true;

    public ArrayList myItems = new ArrayList();

    public RecyclerListProductAdapter(Context mContext, ArrayList<TempInvoiceStock> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        tempInvoiceStockController = new TemporaryInvoice(mContext);
        SharedPreferences shared = mContext.getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval", true));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView code, discripton, batch, productStock, price, shelf, request, order, free, discount;
        RelativeLayout layout, layout2, layoutBack;


        public MyViewHolder(View view) {
            super(view);

            code = (TextView) view.findViewById(R.id.textViewCode);
            discripton = (TextView) view.findViewById(R.id.textViewdescription);
            batch = (TextView) view.findViewById(R.id.textViewBatch);
            productStock = (TextView) view.findViewById(R.id.txtStock);
            price = (TextView) view.findViewById(R.id.textViewPrice);

            shelf = (TextView) view.findViewById(R.id.TextShelf);
            request = (TextView) view.findViewById(R.id.TextRequest);
            order = (TextView) view.findViewById(R.id.TextOrder);
            free = (TextView) view.findViewById(R.id.TextFree);
            discount = (TextView) view.findViewById(R.id.TextDiscount);

            layoutBack = (RelativeLayout) view.findViewById(R.id.listProduct);

        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product, parent, false);
        MyViewHolder pvh = new MyViewHolder(itemView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {

        holder.code.setText(albumList.get(i).getProductCode());
        holder.discripton.setText(albumList.get(i).getProductDes());
        holder.productStock.setText(String.valueOf(albumList.get(i).getStock()));

        if (albumList.get(i).getBatchCount() == 1) {
            holder.batch.setText(albumList.get(i).getBatchCode());
            holder.price.setText(String.valueOf(albumList.get(i).getPrice()));

            if(albumList.get(i).getColor()==0){
                holder.layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }else {
                holder.layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.highlight_PUMPKIN));
            }
       } else {
            holder.layoutBack.setBackgroundColor(mContext.getResources().getColor(R.color.highlight_yellow));
            holder.batch.setText("Multi");
            holder.price.setText("Multi");
            holder.discount.setText("0.0");
      }



        holder.layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InvoiceGen1Alternate) mContext).lodeSelectedProducutCode(albumList.get(i).getProductCode(), albumList.get(i).getProductDes(),
                        albumList.get(i).getBatchCode(), String.valueOf(albumList.get(i).getStock()), String.valueOf(albumList.get(i).getPrice()),
                        String.valueOf(albumList.get(i).getShelfQuantity()), String.valueOf(albumList.get(i).getRequestQuantity()),
                        String.valueOf(albumList.get(i).getNormalQuantity()), String.valueOf(albumList.get(i).getFreeQuantity()),
                        String.valueOf(albumList.get(i).getPercentage()), String.valueOf(albumList.get(i).getRow_ID()),"",
                        String.valueOf(albumList.get(i).getIsFreeAllowed()), String.valueOf(albumList.get(i).getIsDiscountAllowed()), albumList.get(i).getBatchCount(),i);


            }






        });


        if (String.valueOf(albumList.get(i).getShelfQuantity()).equals("")) {
            holder.shelf.setText("0");
        } else {
            holder.shelf.setText(String.valueOf(albumList.get(i).getShelfQuantity()));

        }

        if (String.valueOf(albumList.get(i).getRequestQuantity()).equals("")) {
            holder.request.setText("0");
        } else {
            holder.request.setText(String.valueOf(albumList.get(i).getRequestQuantity()));

        }

        if (String.valueOf(albumList.get(i).getNormalQuantity()).equals("")) {
            holder.order.setText("0");
        } else {
            holder.order.setText(String.valueOf(albumList.get(i).getNormalQuantity()));


        }

        if (String.valueOf(albumList.get(i).getFreeQuantity()).equals("")) {
            holder.free.setText("0");
        } else {
            holder.free.setText(String.valueOf(albumList.get(i).getFreeQuantity()));

        }

        if (String.valueOf(albumList.get(i).getPercentage()).equals("")) {
            holder.discount.setText("0.0");
        } else {
            holder.discount.setText(String.valueOf(albumList.get(i).getPercentage()));

        }





    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
