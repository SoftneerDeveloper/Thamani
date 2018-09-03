package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.pt.printer.Printer;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.thamani.R;
import com.project.thamani.adapter.NotesAdapter;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.MyDividerItemDecoration;
import com.project.thamani.model.Note;
import android.app.AlertDialog.Builder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FinalActivity extends AppCompatActivity {

    private Button btn_print;
    private TextView cashh,duee,totall;
    private  List<String> receipt;


    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private DatabaseHelper db;


    private ProgressDialog pDialog;

    boolean open_flg = false;

    Printer printer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        btn_print=(Button) findViewById(R.id.btn_print);
        cashh=(TextView) findViewById(R.id.cashh);
        duee=(TextView) findViewById(R.id.duee);
        totall=(TextView) findViewById(R.id.totall);
        printer= new Printer();
        int ret =  printer.open();

        if (ret == 0) {


            Messagebox(this, "open success!!") ;
            open_flg = true;

        }
        else {


            Messagebox(this, "open fail!!") ;
            open_flg = false;

        }

        Intent intent = getIntent();

        final Double total = intent.getExtras().getDouble("total");
        final Double cash = intent.getExtras().getDouble("cash");

        Double due=cash-total;

        duee.setText("Ksh."+due.toString());
        totall.setText("Ksh."+total.toString());
        cashh.setText("Ksh."+cash.toString());


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

//        toggleEmptyNotes();
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                printString(this, );
                int ret = printer.printString(listToString(listdata));


                if (ret== 0) {

                    Messagebox(FinalActivity.this, "success") ;
                }
                else
                {
                    Messagebox(FinalActivity.this, "fail") ;
                }

                printer.close();
                db.deleteItems();

                Intent i =new Intent(FinalActivity.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });
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

    public void open(View view)
    {


        if (open_flg)
        {
            Messagebox(this, "is opend") ;
            return;
        }

        printer = new Printer();


        int ret =  printer.open();

        if (ret == 0) {


            Messagebox(this, "open success!!") ;
            open_flg = true;

        }
        else {


            Messagebox(this, "open fail!!") ;
            open_flg = false;

        }



    }


    public void printString(View view)
    {

        if (!open_flg)
        {
            Messagebox(this, "please open first") ;
            return;
        }

        int no_paper_flg = printer.queState();

        if (no_paper_flg == 1)
        {
            Messagebox(this, "no_paper") ;
            return;
        }



        int ret = printer.printString("Hell,wolrd!!!");


        if (ret== 0) {

            Messagebox(this, "success") ;
        }
        else
        {
            Messagebox(this, "fail") ;
        }


    }

    public void printCode128(View view)
    {

        if (!open_flg)
        {
            Messagebox(this, "please open first") ;
            return;
        }

        int no_paper_flg = printer.queState();

        if (no_paper_flg == 1)
        {
            Messagebox(this, "no_paper") ;
            return;
        }



        int ret = printer.printCODE128("20160601");


        if (ret== 0) {

            Messagebox(this, "success") ;
        }
        else
        {
            Messagebox(this, "fail") ;
        }


    }

    public void printQR(View view)
    {

        if (!open_flg)
        {
            Messagebox(this, "please open first") ;
            return;
        }

        int no_paper_flg = printer.queState();

        if (no_paper_flg == 1)
        {
            Messagebox(this, "no_paper") ;
            return;
        }



        int ret = printer.printQR("Hello,world", 5);


        if (ret== 0) {

            Messagebox(this, "success") ;
        }
        else
        {
            Messagebox(this, "fail") ;
        }


    }

    public void printDataMatrix(View view)
    {

        if (!open_flg)
        {
            Messagebox(this, "please open first") ;
            return;
        }


        int no_paper_flg = printer.queState();

        if (no_paper_flg == 1)
        {
            Messagebox(this, "no_paper") ;
            return;
        }



        int ret = printer.printDataMatrix("Hello,world", 5);


        if (ret== 0) {

            Messagebox(this, "success") ;
        }
        else
        {
            Messagebox(this, "fail") ;
        }


    }


    public void printPictrue(View view)
    {

        if (!open_flg)
        {
            Messagebox(this, "please open first") ;
            return;
        }

        int no_paper_flg = printer.queState();

        if (no_paper_flg == 1)
        {
            Messagebox(this, "no_paper") ;
            return;
        }



        int ret = printer.printPictureByRelativePath("/res/drawable/ic_launcher.png", 200, 200);


        if (ret== 0) {

            Messagebox(this, "success") ;
        }
        else
        {
            Messagebox(this, "fail") ;
        }



    }



    public void close(View view)
    {


        if (!open_flg)
        {
            Messagebox(this, "is closed") ;
            return;
        }



        int ret =  printer.close();

        if (ret == 0) {


            Messagebox(this, "close success!!") ;
            open_flg = false;

        }
        else {


            Messagebox(this, "close fail!!") ;
            open_flg = true;

        }



    }

    public static void Messagebox(Context context, String info)
    {
        Builder builder = new Builder(context);
        builder.setTitle("title");
        builder.setMessage(info);
        builder.setPositiveButton("yes", null);
        builder.show();
    }

}
