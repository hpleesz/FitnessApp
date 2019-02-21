package hu.bme.aut.fitnessapp.broadcast_receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.WaterActivity;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, WaterActivity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CharSequence name = "Channel name";
                String description = "Channel description";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("default", name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[] { 1000 , 1000 , 1000, 1000});
                channel.setLightColor(Color.BLUE);
                channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes );
                notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.glass)
                .setContentTitle("Don't forget to drink!")
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));


        SharedPreferences water = context.getSharedPreferences(WaterActivity.WATER, MODE_PRIVATE);
        float consumed = water.getFloat("Consumed", 0);
        float recommended = water.getFloat("Recommended", 0);
        int percent = (int)((consumed / recommended) * 100);
        builder.setContentText("You are at " + percent + "% of your recommended water intake.");

        Date currentTime = Calendar.getInstance().getTime();

        if(isWithinRange(currentTime, 800, 2000)) {
            if ((isWithinRange(currentTime, 800, 1100) && (percent < 25))
                    || (isWithinRange(currentTime, 1100, 1400) && (percent < 50))
                    || (isWithinRange(currentTime, 1400, 1700) && (percent < 75))
                    || (isWithinRange(currentTime, 1700, 2000) && (percent < 100)))

                notificationManager.notify(100, builder.build()) ;

        }

    }

    public boolean isWithinRange(Date testDate, int from, int to) {
        Calendar c = Calendar.getInstance();
        c.setTime(testDate);
        int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        return to > from && t >= from && t<= to;
    }


}
