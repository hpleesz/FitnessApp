package hu.bme.aut.fitnessapp.Startup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.InternetCheckActivity;
import hu.bme.aut.fitnessapp.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.BroadcastReceivers.BootReceiver;
import hu.bme.aut.fitnessapp.BroadcastReceivers.NotificationReceiver;
import hu.bme.aut.fitnessapp.BroadcastReceivers.ResetWaterReceiver;

public class LoginActivity extends InternetCheckActivity {

    private FirebaseAuth mAuth;

    private TextView register;
    private Button login;
    private EditText email;
    private EditText password;
    private boolean user = true;
    //public static final int INTERVAL = 2 * 60 * 60 * 1000 + 45 * 60 * 1000;
    public static final int INTERVAL = 2 * 60 * 1000;

    public static final String FIRST = "first sign in";

    private DatabaseReference databaseReference;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        initializeLayoutElements();
        setLoginClickListener();
        setRegisterClickListener();

        if(firstLogin()) {
            startNotifications();
            //startResetWater();
        }

    }

    public void initializeLayoutElements() {
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        register = findViewById(R.id.registerTextView);
        login = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    public void setLoginClickListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                login.setEnabled(false);
                register.setEnabled(false);
                login.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(userId);
                                    ValueEventListener eventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // Get Post object and use the values to update the UI

                                            user = (boolean)dataSnapshot.getValue();

                                            if(user) {
                                                Intent intent= new Intent(LoginActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                            else {
                                                Intent intent= new Intent(LoginActivity.this, GymMainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    };
                                    databaseReference.addValueEventListener(eventListener);

                                }
                                else {
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                    login.setEnabled(true);
                                    register.setEnabled(true);
                                    login.setBackground(getResources().getDrawable(R.drawable.button_round));
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    public void setRegisterClickListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    public boolean firstLogin() {
        SharedPreferences first = getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);
        if(first_login) {
            SharedPreferences.Editor first_editor = first.edit();
            first_editor.putBoolean("First login", false);
            first_editor.apply();
        }
        return first_login;
    }

    public void startNotifications() {

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, pendingIntent);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


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

}
