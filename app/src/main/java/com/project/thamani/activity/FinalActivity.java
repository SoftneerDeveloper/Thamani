package com.project.thamani.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.pt.minilcd.MiniLcd;
import android.pt.printer.Printer;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.thamani.R;
import com.project.thamani.adapter.NotesAdapter;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.MyDividerItemDecoration;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.model.Note;


import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FinalActivity extends AppCompatActivity {
    private static final String TAG = FinalActivity.class.getSimpleName();

    private Button btn_print;
    private TextView cashh,duee,totall;
    private  List<String> receipt;

    Context context;


    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private DatabaseHelper db;


    private ProgressDialog pDialog;
    private SQLiteHandler user_db;
    private  String username,phone,shop,staff_id;
    private Double total,cash,due;
    private int ret;
    MiniLcd miniLcd = null;



    boolean open_flg = false;

    Printer printer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        context=FinalActivity.this;

//        onBackPressed();

        btn_print=(Button) findViewById(R.id.btn_print);
        cashh=(TextView) findViewById(R.id.cashh);
        duee=(TextView) findViewById(R.id.duee);
        totall=(TextView) findViewById(R.id.totall);
        user_db = new SQLiteHandler(getApplicationContext());
        miniLcd = new MiniLcd();


        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
        username=user.get("username");
        phone=user.get("phone");
        shop=user.get("shop");
        staff_id=user.get("id");
        printer= new Printer();
        ret =  printer.open();



        Intent intent = getIntent();

         total = intent.getExtras().getDouble("total");
         cash = intent.getExtras().getDouble("cash");

         due=cash-total;

        duee.setText("Ksh."+ String.format("KES. %.2f",due));
        totall.setText("Ksh."+ String.format("KES. %.2f",total));
        cashh.setText("Ksh."+ String.format("KES. %.2f",cash));


        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());

        final ArrayList<String> listdata = new ArrayList<String>();
        JSONArray jArray = db.getAllItems();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                try {
                    listdata.add(jArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
//        receipt=db.getAllItems().toString();

        recyclerView = findViewById(R.id.recyler_view2);

        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);
        ShowCustomer();

//        toggleEmptyNotes();
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                printString(this, );
//Note
Print();
//                printer.printString(listToString(listdata));


                db.deleteItems();

                Intent i =new Intent(FinalActivity.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

    private void Print(){
//                printer.setLeftMargin(4);
//                printer.setRightMargin(4);
        ret = printer.printBlankLines(1);
        printer.setBold(true);
        printer.setFontSize(28);
        printer.printString("THAMANI ONLINE");
        printer.setBold(false);
                printer.printString("RETAILER NAME");
                printer.setBold(false);
                printer.printString("Customer Care: "+ phone);
        printer.printString("Date : " + dateBuilder() + "\t" + timeBuilder());
        printer.printString("******  Sales Receipt  *****");
        printer.printString("----------------------------");
        printer.setBold(true);
            printer.printString("ITEM         QTY   PRICE(KES)  ");
                printer.setBold(false);
                printer.printString("----------------------------");
        Log.d(TAG, "Print Response: " + String.valueOf(db.printAllItems()));

        ArrayList<Note>  notes = new ArrayList<>();
        notes.addAll(db.printAllItems());

        for(int i=0;i<notes.size();i++) {
            printer.printString(notes.get(i).getGTIN());
            printer.printString(notes.get(i).getItem()+"    1           "+ notes.get(i).getPrice());
        }

        printer.printString("-----------------------------");
                printer.setBold(true);
                                printer.setFontSize(20);
        printer.printString("Total         :  KES " + total);
               printer.setFontSize(4);
               printer.setBold(false);
        printer.printString("-----------------------------");

        printer.printString("Cash          :  KES " + cash);
        printer.printString("Change        :  KES " + String.format("KES. %.2f",due));
        printer.printString("----------------------------");
        double vat= total*0.4;
        double sub=total-vat;

        printer.printString("KES " + String.format("%.2f",vat)+" VAT @4% Inclusive");
        printer.printString("KES " + String.valueOf(sub)+" Total Excl");
        printer.printString("-----------------------------");
                printer.setBold(true);
                printer.printString("Served By :"+ username);
                printer.setBold(false);
                printer.printString("***Asante, Karibu tena ***");
        printer.printString("Powered by Thamani Online ");
        printer.printString("  ");
        printer.printString("  ");
        printer.printString("  ");
        printer.printString("  ");

        printer.printBlankLines(20);




        printer.close();

    }
    private void  ShowCustomer(){
        miniLcd.open();
//        miniLcd.displayBootPicture(225,255,255);
//        miniLcd.eraseUserPicture(206195208);
        miniLcd.fullRectangle(2,2,476,316,206195208);

//        miniLcd.displayPicture(32,0, BitmapFactory.decodeResource(getResources(), R.drawable.thamani_logo_black));


        miniLcd.displayString(55,20,000000,206195208,"RETAILER NAME: "+ shop,12);

        miniLcd.displayString(32,50,000000,206195208,"---------------------------------",10);
        miniLcd.displayString(32,80,000000,206195208,"TOTAL :  KES "+ String.format("KES. %.2f",total),20);
        miniLcd.displayString(32,120,000000,206195208,"CASH  :  KES "+ String.format("KES. %.2f",cash),20);
        miniLcd.displayString(32,150,000000,206195208,"---------------------------------",10);
        miniLcd.displayString(32,180,000000,206195208,"CHANGE DUE:  KES "+ String.format("KES. %.2f",due),20);

        miniLcd.displayString(32,220,000000,206195208,"Served By :"+ username,12);
        miniLcd.displayString(32,260,000000,206195208,"****Asante sana, Karibu tena**** ",12);

        miniLcd.close();

    }
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
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    /**
     * Toggling list and empty notes view
     */
//    private void toggleEmptyNotes() {
//        // you can check notesList.size() > 0
//
//        if (db.getNotesCount() > 0) {
//            noNotesView.setVisibility(View.GONE);
//        } else {
//            noNotesView.setVisibility(View.VISIBLE);
//        }
//    }
    //Timer

    public static String timeBuilder() {

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static String dateBuilder() {
        /*DateFormat format = new SimpleDateFormat("dd/MM/yyyy ");
        Date date = new Date();
        String dot = format.format(date);
        return dot;*/

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String dot = format.format(date);
        return dot;


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        IsFinish("You must first print inorder to go back !");
    }




    public void IsFinish(String alertmessage) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
//                        android.os.Process.killProcess(android.os.Process.myPid());
                        // This above line close correctly
                        //finish();
                        Print();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Notice");
        builder.setMessage(alertmessage)
                .setPositiveButton("Print", dialogClickListener)
                .setNegativeButton("Later", dialogClickListener).show();

    }
}
