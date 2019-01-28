package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.pt.minilcd.MiniLcd;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.project.thamani.R;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.project.thamani.activity.MpesaActivity.generateSerial;

public class FinishActivity extends AppCompatActivity {

    private Button btn_sale;
    private String userid,payy,shop,username,staff_id,serial;
    private static final String TAG = FinishActivity.class.getSimpleName();
    private String cash="cash";

    private DatabaseHelper db;
    private SQLiteHandler user_db;
    private SessionManager session;

    private ProgressDialog pDialog;

    private EditText pay,pay_amount;
    MiniLcd miniLcd = null;
    private Double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        miniLcd = new MiniLcd();
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        serial="ONLINE"+generateSerial(5)+ts;

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        pay_amount=(EditText) findViewById(R.id.pay_amount);
        pay=(EditText) findViewById(R.id.paytt);
        db = new DatabaseHelper(this);


        final JSONObject sales= new JSONObject();


        // SqLite database handler
        user_db = new SQLiteHandler(getApplicationContext());


        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
        userid=user.get("u_id");
        shop=user.get("shop");
        username=user.get("username");
        staff_id=user.get("id_no");
        try {
            sales.put("sale",db.getAllItems());
            sales.put("mode","cash");
            sales.put("serial",serial);
            sales.put("user_id",userid);
            sales.put("staff_id",staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Add Response: " + String.valueOf(sales));

//        Toast.makeText(this, String.valueOf(sales), Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();

        total = intent.getExtras().getDouble("total");
        pay.setText(total.toString());
        btn_sale=(Button) findViewById(R.id.btn_sale);
        btn_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 payy = pay_amount.getText().toString().trim();

                // Check for empty data in the form
                if (!payy.isEmpty() ) {
                    double bill=Double.parseDouble(payy);
                    if (bill<total){
                        // Prompt user to enter credentials
                        pay_amount.setError("Cash amount is less than the Total amount!");
                        Toast.makeText(getApplicationContext(),
                                "Cash amount is less than the Total amount!", Toast.LENGTH_LONG)
                                .show();
                    }else {
                        postSale(payy,total,sales);
                    }

                } else {
                    // Prompt user to enter credentials
                    pay_amount.setError("Enter the Cash amount");
                    Toast.makeText(getApplicationContext(),
                            "Please enter the Cash amount!", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        ShowCustomer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * method make volley network call and parses json
     */
    private void postSale(final String payyy, final Double totall, final JSONObject salee) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Completing sale...");
        showDialog();
        Log.d(TAG, "Sale Response: " + salee.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                AppConfig.URL_SALE,salee, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Add Response: " + response.toString());
                hideDialog();
                try {
//                    JSONObject jObj = new JSONObject("result");
                    boolean error = response.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                Toast.makeText(getApplicationContext(), "Sale is completed successfully.", Toast.LENGTH_LONG).show();

                hideDialog();
                // Launch Final activity
                double cash=Double.parseDouble(payyy);

                Intent i =new Intent(FinishActivity.this,FinalActivity.class);
                i.putExtra("total", totall);
                i.putExtra("cash", cash);
                startActivity(i);
                salee.remove("sale");
//                salee.remove("sale");
//                salee.remove("sale");
//                salee.remove("sale");
                finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
//                        String errorMsg = response.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "Sale Failed", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sale Error: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    hideDialog();

                    OfflineSale(payyy,totall,salee);

                    Toast.makeText(FinishActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError ) {
                    hideDialog();

                    Toast.makeText(FinishActivity.this,"Connection Time Out",    Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    Toast.makeText(FinishActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    OfflineSale(payyy,totall,salee);
                    hideDialog();

                    Toast.makeText(FinishActivity.this,"Could not reach the Server",    Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    OfflineSale(payyy,totall,salee);
                    hideDialog();

                    Toast.makeText(FinishActivity.this,"No Internet Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    OfflineSale(payyy,totall,salee);
                    hideDialog();

                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }

                hideDialog();
            }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_string_req);
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
        miniLcd.displayString(32,160,000000,206195208,"Served By :"+ username,12);
        miniLcd.displayString(90,260,000000,206195208,"Powered by Thamani Online ",12);

        miniLcd.close();

    }
    private  void OfflineSale(final String payyy, final Double totall, final JSONObject salee){

        db.updateOffline();

        Toast.makeText(getApplicationContext(), "Offline Sale is completed successfully.", Toast.LENGTH_LONG).show();
        // Launch Final activity
        double cash=Double.parseDouble(payyy);

        Intent i =new Intent(FinishActivity.this,FinalActivity.class);
        i.putExtra("total", totall);
        i.putExtra("cash", cash);
        startActivity(i);
        salee.remove("sale");
//                salee.remove("sale");
//                salee.remove("sale");
//                salee.remove("sale");
        finish();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
