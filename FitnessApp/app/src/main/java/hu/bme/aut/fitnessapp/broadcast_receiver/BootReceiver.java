package hu.bme.aut.fitnessapp.broadcast_receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.UserActivity;


public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent alarmIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + UserActivity.INTERVAL, UserActivity.INTERVAL, pendingIntent);


            PendingIntent waterPendingIntent = PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager waterAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);

            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE, 1);
            }


            waterAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, waterPendingIntent);


        }
    }


}
