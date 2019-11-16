package hu.bme.aut.fitnessapp.Models.Startup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
//import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.BroadcastReceivers.BootReceiver;
import hu.bme.aut.fitnessapp.BroadcastReceivers.NotificationReceiver;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadProfile;

import static android.content.Context.MODE_PRIVATE;

public class LoginModel implements LoadProfile.ProfileLoadedListener{

    private FirebaseAuth mAuth;

    private boolean isUser = true;
    public static final int INTERVAL = 2 * 60 * 60 * 1000 + 45 * 60 * 1000;
    //public static final int INTERVAL = 2 * 60 * 1000;

    public static final String FIRST = "first sign in";

    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    private Context activity;
    private Object object;

    public interface loginListener {
        void onUserLogin();
        void onGymLogin();
        void onLoginError(String message);
    }

    private LoginModel.loginListener listener;


    public LoginModel(Context activity) {
        listener = (LoginModel.loginListener)activity;

        this.activity = activity;
        object = this;

        if(firstLogin()) {
            startNotifications();
            //startResetWater();
        }

    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            LoadProfile loadProfile = new LoadProfile(object);
                            loadProfile.loadProfile();
                            /*
                            String userId = mAuth.getCurrentUser().getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(userId);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get Post object and use the values to update the UI

                                    user = (boolean) dataSnapshot.getValue();

                                    if (user) {
                                        listener.onUserLogin();
                                        //((LoginActivity)activity).startUserActivity();
                                    } else {
                                        listener.onGymLogin();
                                        //((LoginActivity)activity).startGymActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                            databaseReference.addValueEventListener(eventListener);

                             */

                        } else {
                            listener.onLoginError(task.getException().getMessage());
                            //((LoginActivity)activity).errorMessage(task.getException().getMessage());
                           }
                    }
                });
    }

    @Override
    public void onProfileLoaded(boolean isUser) {
        this.isUser = isUser;
        if (this.isUser) {
            listener.onUserLogin();
        } else {
            listener.onGymLogin();
        }
    }

    public boolean firstLogin() {
        SharedPreferences first = activity.getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);
        if(first_login) {
            SharedPreferences.Editor first_editor = first.edit();
            first_editor.putBoolean("First login", false);
            first_editor.apply();
        }
        return first_login;
    }

    public void startNotifications() {

        PackageManager pm = activity.getPackageManager();
        ComponentName receiver = new ComponentName(activity, BootReceiver.class);
        Intent intent = new Intent(activity, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, pendingIntent);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

/*
    public void startResetWater() {

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        Intent intent = new Intent(this, ResetWaterReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 58);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //}

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

 */

}
