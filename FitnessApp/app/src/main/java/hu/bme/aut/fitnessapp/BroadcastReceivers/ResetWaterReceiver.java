package hu.bme.aut.fitnessapp.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ResetWaterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        /*
        SharedPreferences water = context.getSharedPreferences(WaterActivity.WATER, MODE_PRIVATE);
        SharedPreferences.Editor editor = water.edit();
        editor.putFloat("Consumed", 0);
        editor.apply();
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            //Intent i = new Intent(context, ResetWaterService.class);
            //context.startService(i);
            //databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2);
            String userId = mAuth.getCurrentUser().getUid();
            double water2 = 0.0;
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(calendar.YEAR);
            int month = calendar.get(calendar.MONTH);
            int day = calendar.get(calendar.DAY_OF_MONTH);

            calendar.set(year, month, day, 0,0,0);
            long today = calendar.getTimeInMillis() / 1000;

            databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved. " + databaseError.getMessage());
                        Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();

                    } else {
                        System.out.println("Data saved successfully.");
                        Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }

        PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        Intent intent2 = new Intent(context, ResetWaterReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


}
