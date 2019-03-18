package com.project.thamani.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class S {
    public static void m(String message){
        Log.d("Thamani", "" + message);
    }

    public static void t(LinearLayout layout, String message){
//        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
//        sbView.setBackgroundColor(ContextCompat.getColor(, R.color.colorAccent));
        snackbar.show();
    }

    public static void T(LinearLayout layout, String message){

        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
