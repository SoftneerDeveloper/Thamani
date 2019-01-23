package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.pt.minilcd.MiniLcd;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import com.project.thamani.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.project.thamani.model.Keys.keys.MY_DEFAULT_MAX_RETRIES;
import static com.project.thamani.model.Keys.keys.MY_SOCKET_TIMEOUT_MS;

public class MpesaActivity extends AppCompatActivity {

    private Button btn_sale,btn_confirm;
    private String userid,phone_no,shop,username,staff_id,final_total,serial;
    private static final String TAG = MpesaActivity.class.getSimpleName();
    private String cash="mpesa";

    private DatabaseHelper db;
    private SQLiteHandler user_db;
    private SessionManager session;
    Context context;


    private JSONObject sales;

    private ProgressDialog pDialog;

    private EditText pay,phone;
    MiniLcd miniLcd = null;
    private Double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = MpesaActivity.this;

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
        phone=(EditText) findViewById(R.id.phone);
        pay=(EditText) findViewById(R.id.paytt);
        db = new DatabaseHelper(this);
        
        sales= new JSONObject();
        
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
            sales.put("mode","mpesa");
            sales.put("serial",serial);
            sales.put("user_id",userid);
            sales.put("staff_id",staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Add Response: " + String.valueOf(sales));
//        DecimalFormat format = new DecimalFormat();
//        format.setDecimalSeparatorAlwaysShown(false);
//        Toast.makeText(this, String.valueOf(sales), Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();

        total = intent.getExtras().getDouble("total");
        final_total=Double.toString(Math.round(total));

        pay.setText(String.format("KES. %.2f",total));

        btn_sale=(Button) findViewById(R.id.btn_sale);
        btn_confirm=(Button) findViewById(R.id.btn_confirm);
        btn_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_no = phone.getText().toString().trim();
                // Check for empty data in the form
                if (phone_no.isEmpty() ) {
                    // Prompt user to enter credentials
                    phone.setError("Invalid phone number");
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid phone number!", Toast.LENGTH_LONG)
                            .show();
                } else {

                    ThamaniSTK(phone_no,final_total);

                }

            }
        });
        ShowCustomer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static String generateSerial(int length){
        String alphabet =
                new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"); //9
        int n = alphabet.length(); //10

        String result = new String();
        Random r = new Random(); //11

        for (int i=0; i<length; i++) //12
            result = result + alphabet.charAt(r.nextInt(n)); //13

        return result;
    }
    /**
     * method make volley network call and parses json
     */
    private void postSale(final Double totall, final JSONObject salee) {

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
                        Toast.makeText(getApplicationContext(), "Verification successfully.", Toast.LENGTH_LONG).show();

                        hideDialog();
                        // Launch Final activity
                        Intent i =new Intent(MpesaActivity.this,FinalActivity.class);
                        i.putExtra("total", totall);
                        i.putExtra("cash", cash);
                        startActivity(i);
                        finish();


                    } else {

                        // Error occurred in registration. Get the error
                        // message
//                        String errorMsg = response.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                "Verification Failed, Wait for Customer to pay", Toast.LENGTH_LONG).show();
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

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Connection Time Out",    Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Could not reach the Server",    Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"No Internet Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
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

    /**
     * function to verify login details in mysql db
     * */
    private void ThamaniSTK(final String phone, final String amount) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Sending STK to Customer...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.STK_PUSH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "STK Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                final String params = jObj.getString("params");

                // Check for error node in json
                if (params!=null) {

                    btn_confirm.setVisibility(View.VISIBLE);
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfirmPayment(params);
                        }
                    });

                }else {
                    Toast.makeText(MpesaActivity.this, "STK Payment Failed", Toast.LENGTH_SHORT).show();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Connection Time Out",    Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Could not reach the Server",    Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"No Internet Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    hideDialog();

                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("amount", amount);
                return params;
            }
        };

        // Adding request to request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                MY_DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance().getRequestQueue().add(strReq);
    }

    /**
     * function to verify login details in mysql db
     * */
    private void VerifyPayment(final String param) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Verifying Payment ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.VERIFY_PAY+param, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "STK Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                    // Check for error node in json
                        postSale(total, sales);
                    }else {
                        Toast.makeText(MpesaActivity.this, "STK Payment Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Connection Time Out",    Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"Could not reach the Server",    Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();

                    Toast.makeText(MpesaActivity.this,"No Internet Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    hideDialog();

                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }

                hideDialog();
            }
        }) {


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void ConfirmPayment(final String params) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Notice");
        builder.setMessage("Wait for the Customer to finish the payment to confirm?");

        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                VerifyPayment(params);

                dialog.cancel();

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //buyCover();
            }
        });

        builder.show();
    }
}
