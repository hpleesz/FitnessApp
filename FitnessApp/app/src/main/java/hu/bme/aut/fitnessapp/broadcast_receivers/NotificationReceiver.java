package hu.bme.aut.fitnessapp.broadcast_receivers;

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

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;
import hu.bme.aut.fitnessapp.models.database_models.LoadWater;
import hu.bme.aut.fitnessapp.models.database_models.LoadWeight;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.settings.SettingsActivity;
import hu.bme.aut.fitnessapp.controllers.user.water.WaterActivity;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver implements LoadWeight.CurrentWeightLoadedListener, LoadWater.WaterLoadedListener, LoadUser.UserLoadedListener {

    private double currentWeight;
    private FirebaseAuth mAuth;
    private String userId;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeatingIntent = new Intent(context, WaterActivity.class);

        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Channel name";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000});
            channel.setLightColor(Color.BLUE);
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(context, "default")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.glass)
                .setContentTitle("Don't forget to drink!")
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));


        mAuth = FirebaseAuth.getInstance();
        if(userLoggedIn()) {
            userId = mAuth.getCurrentUser().getUid();
            this.context = context;
            getUser();
        }


    }

    public boolean isWithinRange(Date testDate, int from, int to) {
        Calendar c = Calendar.getInstance();
        c.setTime(testDate);
        int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        return to > from && t >= from && t <= to;
    }

    public boolean notificationsTurnedOn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(userId, true);
    }

    public boolean userLoggedIn() {
        return (mAuth.getCurrentUser() != null);
    }


    public void getData() {
        LoadWeight loadWeight = new LoadWeight();
        loadWeight.setCurrentWeightLoadedListener(this);
        loadWeight.loadCurrentWeight();
    }

    public void getWater() {
        LoadWater loadWater = new LoadWater();
        loadWater.setListLoadedListener(this);
        loadWater.loadWaterToday();
    }

    public void getUser() {
        LoadUser loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();

    }

    @Override
    public void onCurrentWeightLoaded(double weight) {
        currentWeight = weight;
        getWater();
    }

    @Override
    public void onWaterLoaded(double water) {

        float recommended = (float) (currentWeight * 0.033 + 1);
        double display = Math.round(recommended * 10d) / 10d;
        int percent = (int) ((water / display) * 100);

        builder.setContentText("You are at " + percent + "% of your recommended water intake.");

        Date currentTime = Calendar.getInstance().getTime();

        if (isWithinRange(currentTime, 800, 2000) &&
                (isWithinRange(currentTime, 800, 1100) && (percent < 25))
                || (isWithinRange(currentTime, 1100, 1400) && (percent < 50))
                || (isWithinRange(currentTime, 1400, 1700) && (percent < 75))
                || (isWithinRange(currentTime, 1700, 2000) && (percent < 100)))

                notificationManager.notify(100, builder.build());


    }

    @Override
    public void onUserLoaded(User user) {
        if(notificationsTurnedOn(context)) {
            getData();
        }
    }
}
