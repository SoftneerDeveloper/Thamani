package com.project.thamani.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.project.thamani.activity.FinalActivity;
import com.project.thamani.activity.FinishActivity;
import com.project.thamani.activity.MainActivity;
import com.project.thamani.activity.OfflineActivity;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.project.thamani.activity.MpesaActivity.generateSerial;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private String userid,payy,shop,username,staff_id,serial;
    private static final String TAG = NetworkStateChecker.class.getSimpleName();
    private String cash="cash";

    private DatabaseHelper db;
    private SQLiteHandler user_db;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);
        // SqLite database handler
        user_db = new SQLiteHandler(context);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        serial="OFFLINE"+ts;

        final JSONObject sales= new JSONObject();



        // Fetching user details from SQLite
        HashMap<String, String> user = user_db.getUserDetails();

//        String name = user.get("name");
//        String email = user.get("email");
        userid=user.get("u_id");
        shop=user.get("shop");
        username=user.get("username");
        staff_id=user.get("id_no");
        try {
            sales.put("sale",db.getAllOfflineItems());
            sales.put("mode","cash");
            sales.put("status",serial);
            sales.put("user_id",userid);
            sales.put("staff_id",staff_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                postSale(sales);
//                onReceive();
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);




            }
        }
    }

    //Check if internet is back push the sales

    //After pushing update to 1 and cleqr from table
    /**
     * method make volley network call and parses json
     */
    private void postSale(final JSONObject salee) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";

        Log.d(TAG, "Sale Response: " + salee.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                AppConfig.URL_SALE,salee, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Add Response: " + response.toString());
                try {
//                    JSONObject jObj = new JSONObject("result");
                    boolean error = response.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                        Toast.makeText(context, "Offline Sale was pushed successfully.", Toast.LENGTH_LONG).show();
                        db.updateOfflineBack();
                        db.deleteItems();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
//                        String errorMsg = response.getString("error_msg");
                        Toast.makeText(context,
                                "Sale Failed", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sale Error: " + error.getMessage());
                }
        }) {

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_string_req);
    }



}
