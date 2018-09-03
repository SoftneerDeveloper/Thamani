package com.project.thamani.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.project.thamani.R;

public class PayActivity extends AppCompatActivity {

    private CardView cash;
    private TextView txt_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_total=(TextView) findViewById(R.id.total);
        Intent intent = getIntent();

        final Double total = intent.getExtras().getDouble("total");
        txt_total.setText("Ksh."+total.toString());

        cash=(CardView) findViewById(R.id.cash);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(PayActivity.this, FinishActivity.class);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
