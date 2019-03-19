package com.project.thamani.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.thamani.R;
import com.project.thamani.adapter.CreditNotesAdapter;
import com.project.thamani.app.AppConfig;
import com.project.thamani.app.AppController;
import com.project.thamani.database.DatabaseHelper;
import com.project.thamani.helper.MyDividerItemDecoration;
import com.project.thamani.helper.S;
import com.project.thamani.helper.SQLiteHandler;
import com.project.thamani.model.All;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewCreditNotesActivity extends AppCompatActivity {

    private String userid,barcode,shop,serial_no;

    private static final String TAG = ViewCreditNotesActivity.class.getSimpleName();

    private DatabaseHelper db;
    private SQLiteHandler user_db;
    private ProgressDialog pDialog;

    private RecyclerView recyclerView;
    private List<All> detailList;
    private CreditNotesAdapter mAdapter;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    private ProgressBar load;
    private Button add;
private LinearLayout view_credit_notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_credit_notes);

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
        load=(ProgressBar) findViewById(R.id.load);
        add=(Button) findViewById(R.id.add);


        view_credit_notes = findViewById(R.id.view_credit_notes);
        recyclerView = findViewById(R.id.recycler_view);
        detailList = new ArrayList<>();
        mAdapter = new CreditNotesAdapter(this, detailList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        fetchCreditNotes(userid);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewCreditNotesActivity.this,CreditNotes.class));

            }
        });

    }

    private void AnimateProgressBar(){

        if (progressStatus == 100) {
            progressStatus = 0;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    // Update the progress status
                    progressStatus += 1;

                    // Try to sleep the thread for 20 milliseconds
                    try {
                        Thread.sleep(20);  //3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            load.setProgress(progressStatus);
                            // Show the progress on TextView
//                            tv.setText(progressStatus + "/100");
                        }
                    });
                }
            }
        }).start(); // Start the operation
    }
    /**
     * method make volley network call and parses json
     */
    private void fetchCreditNotes(String user_id) {

        AnimateProgressBar();

        StringRequest request = new StringRequest(AppConfig.CREDIT_NOTES_G+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jObj = null;
                        JSONArray data = null;
                        try {
                            jObj = new JSONObject(response);
                            data = jObj.getJSONArray("data");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int count=1;
                        assert data != null;
                        if ( data.length()< count) {
                            load.setVisibility(View.GONE);
//                            Toast.makeText(getApplicationContext(), "Couldn't fetch the notifications! Please try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<All> alls = new Gson().fromJson(data.toString(), new TypeToken<List<All>>() {
                        }.getType());

                        // adding recipes to cart list
                        detailList.clear();

                        detailList.addAll(alls);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        load.setVisibility(View.GONE);
                        // stop animating Shimmer and hide the layout

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                if ( error instanceof NoConnectionError) {
                    S.T(view_credit_notes,"Check your Data Connection");
                    load.setVisibility(View.GONE);
                } else if (error instanceof TimeoutError) {
                    load.setVisibility(View.GONE);
                    S.T(view_credit_notes,"Connection Time Out");

                } else if (error instanceof AuthFailureError) {
                    load.setVisibility(View.GONE);
                    S.T(view_credit_notes,"Check your Data Connection");
                } else if (error instanceof ServerError) {
                    load.setVisibility(View.GONE);
                    S.T(view_credit_notes,"Could not reach the Server");
                } else if (error instanceof NetworkError) {
                    load.setVisibility(View.GONE);
                    S.T(view_credit_notes,"No Internet Connection");
                } else if (error instanceof ParseError) {
                    load.setVisibility(View.GONE);
//                    Toast.makeText(getApplicationContext(),
//                            error.getMessage(), Toast.LENGTH_LONG).show();
                }



            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

}
