package hu.bme.aut.fitnessapp.BroadcastReceivers;

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
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.ContextCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWater;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWeight;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Water.WaterActivity;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver implements LoadWeight.CurrentWeightLoadedListener, LoadWater.WaterLoadedListener, LoadUser.UserLoadedListener {

    private double current_weight;
    private double water2;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;
    private int percent;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private boolean isUser = true;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, WaterActivity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AudioAttributes audioAttributes = null;
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
        //}

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


        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        /*
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String userId = firebaseAuth.getCurrentUser().getUid();

        Query lastQuery = databaseReference.child("Weight").child(userId).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                try {
                    Map<String, Double> weight = (Map) dataSnapshot.getValue();
                    current_weight = weight.get(key);

                }
                catch(Exception e) {
                    Map<String, Long> weight = (Map) dataSnapshot.getValue();
                    current_weight = (double)weight.get(key);
                }

                getWater();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

         */

    }

    public void getWater() {
        LoadWater loadWater = new LoadWater();
        loadWater.setListLoadedListener(this);
        loadWater.loadWaterToday();
        /*
        Query lastWaterQuery = databaseReference.child("Water").child(userId).orderByKey().limitToLast(1);
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(calendar.YEAR);
                int month = calendar.get(calendar.MONTH);
                int day = calendar.get(calendar.DAY_OF_MONTH);

                calendar.set(year, month, day, 0,0,0);
                long today = calendar.getTimeInMillis() / 1000;

                if(!key.equals("") && Long.parseLong(key) == today) {
                    try {
                        Map<String, Double> water_entries = (Map) dataSnapshot.getValue();
                        water2 = water_entries.get(Long.toString(today));

                    }
                    catch(Exception e) {
                        Map<String, Long> water_entries = (Map) dataSnapshot.getValue();
                        water2 = (double)water_entries.get(Long.toString(today));
                    }
                }
                //else {
                //    water2 = 0.0;
                //    databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2);
                //}

                float recommended = (float) (current_weight * 0.033 + 1);
                double display = Math.round(recommended * 10d) / 10d;
                percent = (int) ((water2 / display) * 100);

                builder.setContentText("You are at " + percent + "% of your recommended water intake.");

                Date currentTime = Calendar.getInstance().getTime();

                notificationManager.notify(100, builder.build());

                if (isWithinRange(currentTime, 800, 2000)) {
                    if ((isWithinRange(currentTime, 800, 1100) && (percent < 25))
                            || (isWithinRange(currentTime, 1100, 1400) && (percent < 50))
                            || (isWithinRange(currentTime, 1400, 1700) && (percent < 75))
                            || (isWithinRange(currentTime, 1700, 2000) && (percent < 100)))

                        notificationManager.notify(100, builder.build());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

         */
    }

    public void getUser() {
        LoadUser loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();

        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getKey().equals(userId)) {
                        isUser = (boolean)dataSnapshot1.getValue();
                        break;
                    }
                }
                if(isUser && notificationsTurnedOn(context)) {
                    getData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Profiles").addValueEventListener(eventListener);
         */


    }

    @Override
    public void onCurrentWeightLoaded(double weight) {
        current_weight = weight;
        getWater();
    }

    @Override
    public void onWaterLoaded(double water) {
        water2 = water;

        float recommended = (float) (current_weight * 0.033 + 1);
        double display = Math.round(recommended * 10d) / 10d;
        percent = (int) ((water2 / display) * 100);

        builder.setContentText("You are at " + percent + "% of your recommended water intake.");

        Date currentTime = Calendar.getInstance().getTime();

        notificationManager.notify(100, builder.build());

        if (isWithinRange(currentTime, 800, 2000)) {
            if ((isWithinRange(currentTime, 800, 1100) && (percent < 25))
                    || (isWithinRange(currentTime, 1100, 1400) && (percent < 50))
                    || (isWithinRange(currentTime, 1400, 1700) && (percent < 75))
                    || (isWithinRange(currentTime, 1700, 2000) && (percent < 100)))

                notificationManager.notify(100, builder.build());

        }
    }

    @Override
    public void onUserLoaded(User user) {
        if(isUser && notificationsTurnedOn(context)) {
            getData();
        }
    }
}
