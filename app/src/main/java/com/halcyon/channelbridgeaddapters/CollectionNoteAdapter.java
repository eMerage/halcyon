package com.halcyon.channelbridgeaddapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.halcyon.channelbridge.CollectionNote;
import com.halcyon.channelbridge.R;

import java.util.ArrayList;

/**
 * Created by Himanshu on 5/14/2016.
 */
public class CollectionNoteAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<CollectionNoteList> collectionNoteDetailList;


    public CollectionNoteAdapter(Context context, ArrayList<CollectionNoteList> collectionnotedetailList) {
        mContext = context;
        collectionNoteDetailList = collectionnotedetailList;

    }

    @Override
    public int getCount() {
        return collectionNoteDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return collectionNoteDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewHolder = new ViewHolderItem();
        view = inflater.inflate(R.layout.list_collectionnote, null);

        viewHolder.textInvoices = (TextView) view.findViewById(R.id.textViewlistinvoiceno);
        viewHolder.textCash = (TextView) view.findViewById(R.id.textViewlistcash);
        viewHolder.textCheq = (TextView) view.findViewById(R.id.textViewlistcheque);
        viewHolder.textBalance = (TextView) view.findViewById(R.id.textViewlistbalanece);
        viewHolder.dtnDelete = (Button) view.findViewById(R.id.buttonlistDeete);

        viewHolder.textInvoices.setText(String.valueOf(collectionNoteDetailList.get(i).invoiceNumber));
        viewHolder.textCash.setText(String.valueOf(collectionNoteDetailList.get(i).cash));
        viewHolder.textCheq.setText(String.valueOf(collectionNoteDetailList.get(i).cheuqe));
        viewHolder.textBalance.setText(String.valueOf(collectionNoteDetailList.get(i).balance));


        viewHolder.dtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CollectionNote) mContext).getDeleteIdFromList(collectionNoteDetailList.get(i).invoiceNumber);

            }
        });


        return view;
    }
    static class ViewHolderItem {
        TextView textInvoices, textCash, textCheq, textBalance;
        Button dtnDelete;

    }

}
