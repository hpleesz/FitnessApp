package hu.bme.aut.fitnessapp.Models.StartupModels;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.BroadcastReceivers.BootReceiver;
import hu.bme.aut.fitnessapp.BroadcastReceivers.NotificationReceiver;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadProfile;

import static android.content.Context.MODE_PRIVATE;

public class LoginModel implements LoadProfile.ProfileLoadedListener{

    private FirebaseAuth mAuth;

    public static final int INTERVAL = 2 * 60 * 60 * 1000 + 45 * 60 * 1000;
    //public static final int INTERVAL = 2 * 60 * 1000;

    public static final String FIRST = "first sign in";

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
                            LoadProfile loadProfile = new LoadProfile();
                            loadProfile.setListLoadedListener(object);
                            loadProfile.loadProfile();

                        } else {
                            listener.onLoginError(task.getException().getMessage());
                           }
                    }
                });
    }

    @Override
    public void onProfileLoaded(boolean isUser) {
        if (isUser) {
            listener.onUserLogin();
        } else {
            listener.onGymLogin();
        }
    }

    private boolean firstLogin() {
        SharedPreferences first = activity.getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);
        if(first_login) {
            SharedPreferences.Editor first_editor = first.edit();
            first_editor.putBoolean("First login", false);
            first_editor.apply();
        }
        return first_login;
    }

    private void startNotifications() {

        PackageManager pm = activity.getPackageManager();
        ComponentName receiver = new ComponentName(activity, BootReceiver.class);
        Intent intent = new Intent(activity, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, pendingIntent);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
