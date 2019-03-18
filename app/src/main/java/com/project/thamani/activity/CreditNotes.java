package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.thamani.R;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.S;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreditNotes extends AppCompatActivity {

    private String userid,barcode,shop,serial_no;

    private static final String TAG = CreditNotes.class.getSimpleName();

    private DatabaseHelper db;
    private SQLiteHandler user_db;
    private SessionManager session;
    private BroadcastReceiver mSdcardReceiver;
    private LinearLayout credit_notes;
    private TextInputEditText ireceipt_number,iitem_name,iquantity,idescription,iname,iphone;
    private String receipt_number,item_name,quantity,description,name,phone,staff_id;
    private Button save,clear;

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_notes);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SqLite database handler
        user_db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
        userid=user.get("u_id");
        shop=user.get("shop");
        staff_id=user.get("id_no");


        credit_notes=(LinearLayout)findViewById(R.id.credit_notes);
        ireceipt_number=findViewById(R.id.receipt_number);
        iitem_name=findViewById(R.id.item_name);
        iquantity=findViewById(R.id.quantity);
        idescription=findViewById(R.id.description);
        iname=findViewById(R.id.customer);
        iphone=findViewById(R.id.phone);
        save=findViewById(R.id.save);
        clear=findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ireceipt_number.setText("");
                iitem_name.setText("");
                iquantity.setText("");
                idescription.setText("");
                iname.setText("");
                iphone.setText("");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receipt_number = ireceipt_number.getText().toString().trim();
                item_name = iitem_name.getText().toString().trim();
                quantity = iquantity.getText().toString().trim();
                description = idescription.getText().toString().trim();
                name = iname.getText().toString().trim();
                phone = iphone.getText().toString().trim();

                // Check for empty data in the form
                if (!receipt_number.isEmpty() && !item_name.isEmpty() && !quantity.isEmpty() && !description.isEmpty() && !name.isEmpty() && !phone.isEmpty()) {
                    // login user
                    creditNotes();
                } else {
                    S.T(credit_notes,"All the fields are required!");
                }
            }
        });

    }

    private void creditNotes() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Saving ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.CREDIT_NOTES_P, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Credit Response: " + response.toString());
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (error) {
                        //   Launch login activity
                        S.T(credit_notes,"The Credit Note was saved");
                        startActivity(new Intent(CreditNotes.this,ViewCreditNotesActivity.class));


                    } else {

                        // Error occurred in registration. Get the error
                        // message

                        S.T(credit_notes,"The Credit Note was not saved");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Credit Note: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    hideDialog();
                    S.T(credit_notes,"Check your Data Connection");
                } else if (error instanceof TimeoutError) {
                    hideDialog();

                    S.T(credit_notes,"Connection Time Out");

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    S.T(credit_notes,"Check your Data Connection");
                } else if (error instanceof ServerError) {
                    hideDialog();

                    S.T(credit_notes,"Could not reach the Server");
                } else if (error instanceof NetworkError) {
                    hideDialog();

                    S.T(credit_notes,"No Internet Connection");
                } else if (error instanceof ParseError) {
                    hideDialog();

                }

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                // params.put("name", name);
                params.put("user_id", userid);
                params.put("receipt_number", receipt_number);
                params.put("item_name", item_name);
                params.put("quantity", quantity);
                params.put("customer", name);
                params.put("description", description);
                params.put("phone",phone );
                params.put("staff",staff_id );


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
