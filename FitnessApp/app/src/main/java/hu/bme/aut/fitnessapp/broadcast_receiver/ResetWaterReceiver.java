package hu.bme.aut.fitnessapp.broadcast_receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.WaterActivity;

import static android.content.Context.MODE_PRIVATE;

public class ResetWaterReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences water = context.getSharedPreferences(WaterActivity.WATER, MODE_PRIVATE);
        SharedPreferences.Editor editor = water.edit();
        editor.putFloat("Consumed", 0);
        editor.apply();

        PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        Intent intent2 = new Intent(context, ResetWaterReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //}

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


}
