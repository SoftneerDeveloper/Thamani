package com.project.thamani.adapter;

/**
 * Created by ravi on 26/09/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.thamani.R;
import com.project.thamani.model.Credit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CreditNotesAdapter extends RecyclerView.Adapter<CreditNotesAdapter.MyViewHolder> {
    private Context context;
    private List<Credit> detailList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView receipt_number,item_name,quantity,description,phone,customer;


        public MyViewHolder(View view) {
            super(view);
            receipt_number = view.findViewById(R.id.receipt_number);
            item_name = view.findViewById(R.id.item_name);
            quantity = view.findViewById(R.id.quantity);
            description = view.findViewById(R.id.description);
            phone = view.findViewById(R.id.phone);
            customer = view.findViewById(R.id.customer);


        }
    }


    public CreditNotesAdapter(Context context, List<Credit> cartList) {
        this.context = context;
        this.detailList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credit_note, parent, false);

        return new MyViewHolder(itemView);
    }
//    private String getFirstWord(String text) {
//        int index = text.indexOf(' ');
//        if (index > -1) { // Check if there is more than one word.
//            return text.substring(0, index); // Extract first word.
//        } else {
//            return text; // Text is the first word itself.
//        }
//    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Credit item = detailList.get(position);
//        holder.short_form.setText(item.getNotes().toUpperCase().substring(0,1));
        holder.receipt_number.setText("Receipt #"+item.getAll().get(0).getReceiptNumber());
        holder.item_name.setText("Item #"+item.getAll().get(0).getReceiptNumber());
        holder.quantity.setText("Quantity "+item.getAll().get(0).getQuantity());
        holder.customer.setText("Customer "+item.getAll().get(0).getCustomer());
        holder.phone.setText(item.getAll().get(0).getPhone());
        holder.description.setText(item.getAll().get(0).getNotes());


    }
    public Credit getItem(int position) {
        return detailList.get(position);
    }
    @Override
    public int getItemCount() {
        return detailList.size();
    }



}
