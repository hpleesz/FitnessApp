package hu.bme.aut.fitnessapp.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.log4j.Logger;

import static hu.bme.aut.fitnessapp.controllers.InternetCheckActivity.dialog;


public class NetworkChangeReceiver extends BroadcastReceiver
{
    private static final Logger logger = Logger.getLogger(NetworkChangeReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (isOnline(context)) {
                dialog(true);
            } else {
                dialog(false);
            }
        } catch (NullPointerException e) {
            logger.error("NullPointerException", e);
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            logger.error("NullPointerException", e);
            return false;
        }
    }
}
