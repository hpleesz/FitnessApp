package hu.bme.aut.fitnessapp.controllers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.log4j.Logger;

import hu.bme.aut.fitnessapp.broadcast_receivers.NetworkChangeReceiver;
import hu.bme.aut.fitnessapp.R;

public class InternetCheckActivity extends AppCompatActivity {

    private BroadcastReceiver mNetworkReceiver;
    private static AlertDialog alertDialog;

    private static final Logger logger = Logger.getLogger(InternetCheckActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.content_internet_check, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

    }

    public static void dialog(boolean value){

        if(value){
            if(alertDialog != null) alertDialog.dismiss();
        }else {
            alertDialog.show();
        }
    }


    private void registerNetworkBroadcastForNougat() {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentExcaption", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

}
