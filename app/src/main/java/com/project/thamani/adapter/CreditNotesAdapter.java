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
import com.project.thamani.model.All;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CreditNotesAdapter extends RecyclerView.Adapter<CreditNotesAdapter.MyViewHolder> {
    private Context context;
    private List<All> detailList;


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


    public CreditNotesAdapter(Context context, List<All> cartList) {
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
//            return text.substring(position, index); // Extract first word.
//        } else {
//            return text; // Text is the first word itself.
//        }
//    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final All item = detailList.get(position);
        if (item != null) {

                if (item.getReceiptNumber() != null)
                    holder.receipt_number.setText("Receipt #"  + item.getReceiptNumber());

            if (item.getItem() != null)
                holder.item_name.setText("Item #" + item.getItem());

            if (item.getQuantity() != null)
                holder.quantity.setText("Quantity " + item.getQuantity());

            if (item.getCustomer() != null)
                holder.customer.setText("Customer " + item.getCustomer());

            if (item.getPhone() != null)
                holder.phone.setText(item.getPhone());

            if (item.getNotes() != null)
                holder.description.setText(item.getNotes());
        }

    }
    public All getItem(int position) {
        return detailList.get(position);
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    // recipe
    private String listToString(List<String> stringList){
        String s= "";
        if(stringList != null)
            for (int i=0;i<stringList.size();i++){
                if(i == stringList.size()-1){
                    s+=stringList.get(0);
                }else{
                    s+=stringList.get(i)+", ";
                }
            }
        return s;
    }


}
