/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.project.thamani.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.project.thamani.R;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.helper.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputphone_no;
    private EditText inputpasscode;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputphone_no = (EditText) findViewById(R.id.phone_no);
        inputpasscode = (EditText) findViewById(R.id.passcode);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String phone_no = inputphone_no.getText().toString().trim();
                String passcode = inputpasscode.getText().toString().trim();

                // Check for empty data in the form
                if (!phone_no.isEmpty() && !passcode.isEmpty()) {
                    // login user
                    checkLogin(phone_no, passcode);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

//        // Link to Register Screen
//        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),
//                        MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String phone, final String passcode) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.GET,
                AppConfig.URL_LOGIN+phone+"/"+passcode, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject user = jObj.getJSONObject("data");
                    boolean active = user.getBoolean("active");

                    // Check for error node in json
                    if (user != null) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
//                        String uid = jObj.getString("uid");
//                        String id = jObj.getString("id");


                        
                        String user_id = user.getString("user_id");
                        String fullname = user.getString("fullname");
                        String phone = user.getString("phone");
                        String id = user.getString("id");

                        // Inserting row in users table
                        db.addUser(fullname, phone, user_id,id);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "User account does not exist";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Login Error: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    hideDialog();

                    Toast.makeText(LoginActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    hideDialog();

                    Toast.makeText(LoginActivity.this,"Connection Time Out",    Toast.LENGTH_LONG).show();

                } else if (error instanceof AuthFailureError) {
                    hideDialog();

                    Toast.makeText(LoginActivity.this,"Check your Data Connection",    Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    hideDialog();

                    Toast.makeText(LoginActivity.this,"Could not reach the Server",    Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    hideDialog();

                    Toast.makeText(LoginActivity.this,"No Internet Connection",    Toast.LENGTH_LONG).show();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
