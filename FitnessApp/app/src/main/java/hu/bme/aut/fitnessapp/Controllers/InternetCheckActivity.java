package hu.bme.aut.fitnessapp.Controllers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import hu.bme.aut.fitnessapp.BroadcastReceivers.NetworkChangeReceiver;
import hu.bme.aut.fitnessapp.R;

public class InternetCheckActivity extends AppCompatActivity {

    private BroadcastReceiver mNetworkReceiver;
    private static AlertDialog alertDialog;

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
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //}
        //else {
        //    registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //}
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

         */
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

}
