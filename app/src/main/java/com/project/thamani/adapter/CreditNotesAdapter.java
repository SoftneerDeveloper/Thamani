package com.project.thamani.adapter;

/**
 * Created by ravi on 26/09/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.pt.minilcd.MiniLcd;
import android.pt.printer.Printer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.thamani.R;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.model.All;
import com.project.thamani.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.project.thamani.activity.FinalActivity.dateBuilder;
import static com.project.thamani.activity.FinalActivity.timeBuilder;


public class CreditNotesAdapter extends RecyclerView.Adapter<CreditNotesAdapter.MyViewHolder> {
    private Context context;
    private List<All> detailList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView receipt_number,item_name,quantity,description,phone,customer,active;
        private Button print_note;
        MiniLcd miniLcd = null;

        private int ret;


        boolean open_flg = false;

        Printer printer ;

        private ProgressDialog pDialog;
        private SQLiteHandler user_db;
        private  String username,phone2,shop,staff_id;

        public MyViewHolder(View view) {
            super(view);
            receipt_number = view.findViewById(R.id.receipt_number);
            item_name = view.findViewById(R.id.item_name);
            quantity = view.findViewById(R.id.quantity);
            description = view.findViewById(R.id.description);
            phone = view.findViewById(R.id.phone);
            customer = view.findViewById(R.id.customer);
            active = view.findViewById(R.id.active);
            print_note = view.findViewById(R.id.print_note);

            miniLcd = new MiniLcd();
            printer= new Printer();
            ret =  printer.open();

            user_db = new SQLiteHandler(context);

            // Fetching user details from SQLite
            HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
            username=user.get("username");
            phone2=user.get("phone");
            shop=user.get("shop");
            staff_id=user.get("id");

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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
        assert item != null;
        if (item.getActive().equals("false")){
            holder.active.setVisibility(View.GONE);
            holder.print_note.setVisibility(View.GONE);
    }
    
    holder.print_note.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            holder.ret = holder.printer.printBlankLines(1);
            holder.printer.setBold(true);
            holder.printer.setFontSize(28);
            holder.printer.printString("THAMANI ONLINE");
            holder.printer.setBold(false);
            holder.printer.printString("RETAILER NAME");
            holder.printer.setBold(false);
            holder.printer.printString("Customer Care: 0"+ holder.phone2);
            holder.printer.printString("Date : " + dateBuilder() + "\t" + timeBuilder());
            holder.printer.printString("****  Credit Note Receipt  ****");
            holder.printer.printString("----------------------------");

                holder.printer.printString("Receipt number: "+ item.getReceiptNumber());
                holder.printer.printString("Item:           "+ item.getItem());
                holder.printer.printString("Quantity:       "+ item.getQuantity());
                holder.printer.printString("Customer:       "+ item.getCustomer());
                holder.printer.printString("Phone number:   "+ item.getPhone());
                holder.printer.printString("Description:    "+ item.getNotes());

            holder.printer.printString("----------------------------");

            holder.printer.setBold(true);
            holder.printer.printString("Served By :"+ holder.username);
            holder.printer.setBold(false);
            holder.printer.printString("***Asante, Karibu tena ***");
            holder.printer.printString("Powered by Thamani Online ");
            holder.printer.printString("  ");
            holder.printer.printString("  ");
            holder.printer.printString("  ");
            holder.printer.printString("  ");

            holder.printer.printBlankLines(15);




            holder.printer.close(); 
        }
    });

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
