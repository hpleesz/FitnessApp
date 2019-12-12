package hu.bme.aut.fitnessapp.broadcast_receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import hu.bme.aut.fitnessapp.models.startup_models.LoginModel;


public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent alarmIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + LoginModel.INTERVAL, LoginModel.INTERVAL, pendingIntent);
            
        }
    }


}
