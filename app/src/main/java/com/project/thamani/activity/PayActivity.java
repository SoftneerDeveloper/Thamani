package com.project.thamani.activity;

import android.content.Intent;
import android.pt.minilcd.MiniLcd;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.project.thamani.R;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.helper.SessionManager;
import com.project.thamani.model.Note;

import java.util.ArrayList;
import java.util.HashMap;

public class PayActivity extends AppCompatActivity {

    private CardView cash,mpesa;
    private TextView txt_total;
    MiniLcd miniLcd = null;
    private SQLiteHandler user_db;
    private String userid,barcode,shop;
    private Double total,total2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_total=(TextView) findViewById(R.id.total);
        Intent intent = getIntent();
        miniLcd = new MiniLcd();


         total = intent.getExtras().getDouble("total");
        total2 = Math.round(total * 100.0) / 100.0;

        txt_total.setText("KES."+total2.toString());
        // SqLite database handler
        user_db = new SQLiteHandler(getApplicationContext());


        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

        userid=user.get("u_id");
        shop=user.get("shop");

        mpesa=(CardView) findViewById(R.id.mpesa);
        cash=(CardView) findViewById(R.id.cash);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(PayActivity.this, FinishActivity.class);
                intent.putExtra("total", total);
                startActivity(intent);
                finish();
            }
        });
        mpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(PayActivity.this, MpesaActivity.class);
                intent.putExtra("total", total);
                startActivity(intent);
                finish();
            }
        });
        ShowCustomer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void  ShowCustomer(){
        miniLcd.open();
//        miniLcd.displayBootPicture(225,255,255);
//        miniLcd.eraseUserPicture(206195208);
        miniLcd.fullRectangle(2,2,476,316,206195208);

//        miniLcd.displayPicture(32,0, BitmapFactory.decodeResource(getResources(), R.drawable.thamani_logo_black));


        miniLcd.displayString(55,30,000000,206195208,"RETAILER NAME:"+ shop,12);

        miniLcd.displayString(32,70,000000,206195208,"---------------------------------",10);
        miniLcd.displayString(32,120,000000,206195208,"TOTAL AMOUNT:  KES"+ total,20);

        miniLcd.displayString(90,260,000000,206195208,"Powered by Thamani Online ",12);

        miniLcd.close();

    }
}
