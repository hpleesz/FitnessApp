package hu.bme.aut.fitnessapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.broadcast_receiver.BootReceiver;
import hu.bme.aut.fitnessapp.broadcast_receiver.NotificationReceiver;
import hu.bme.aut.fitnessapp.broadcast_receiver.ResetWaterReceiver;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseListDatabase;
import hu.bme.aut.fitnessapp.models.User;

public class UserActivity extends AppCompatActivity {

    private boolean male = false;
    private boolean female = false;
    private boolean lose_weight = false;
    private boolean gain_muscle = false;

    private ImageButton maleButton;
    private ImageButton femaleButton;
    private ImageButton loseWeightButton;
    private ImageButton gainMuscleButton;
    private EditText nameEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private EditText goalWeightEditText;
    private DatePicker datePicker;

    public static final String USER = "user data";

    //2 hours + 45 minutes
    public static final int INTERVAL = 2 * 60 * 60 * 1000 + 45 * 60 * 1000;

    //private ExerciseListDatabase database;
    private DatabaseReference database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        assignLayoutElements();
        setFloatingActionButton();
        setGenderOnClickListeners();
        setGoalOnClickListeners();
        //loadExercises();

    }

    public void assignLayoutElements() {
        nameEditText = findViewById(R.id.nameEditText);
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
        goalWeightEditText = findViewById(R.id.goalWeightEditText);

        datePicker = findViewById(R.id.dateOfBirthPicker);
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataValidity()) {
                    //saveUserData();
                    SharedPreferences user_settings = getSharedPreferences(USER, MODE_PRIVATE);
                    SharedPreferences.Editor editor = user_settings.edit();
                    editor.putBoolean("Notifications on", true);

                    editor.apply();

                    setFirstLoginFalse();
                    writeNewUser();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_positive, Toast.LENGTH_LONG);
                    toast.show();
                    startNotifications();
                    startResetWater();
                    //loadExercises();
                    //finish();
                    startActivity(new Intent(UserActivity.this, MainActivity.class));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_negative, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void setGenderOnClickListeners() {
        maleButton = findViewById(R.id.buttonMale);
        femaleButton = findViewById(R.id.buttonFemale);

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male = true;
                female = false;
                setGenderButtons();
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male = false;
                female = true;
                setGenderButtons();
            }
        });
    }

    private void setGenderButtons() {
        if (male) {
            maleButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_male));
            femaleButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_female_disabled));
        } else {
            femaleButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_female));
            maleButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_male_disabled));
        }
    }

    private void setGoalOnClickListeners() {
        loseWeightButton = findViewById(R.id.buttonLoseWeight);
        gainMuscleButton = findViewById(R.id.buttonGainMuscle);

        loseWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lose_weight)
                    lose_weight = false;
                else
                    lose_weight = true;
                setLoseWeightButton();
            }
        });

        gainMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gain_muscle)
                    gain_muscle = false;
                else
                    gain_muscle = true;
                setMuscleButton();
            }
        });
    }

    private void setLoseWeightButton() {
        if (lose_weight) {
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose));
        } else
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose_disabled));
    }

    private void setMuscleButton() {
        if (gain_muscle) {
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle));
        } else
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle_disabled));
    }


    /*
    private void saveUserData() {
        SharedPreferences user_settings = getSharedPreferences(USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = user_settings.edit();

        //name
        String name = nameEditText.getText().toString();
        editor.putString("Name", name);

        //gender
        editor.putBoolean("Male", male);

        //weight
        float weight = Float.parseFloat(weightEditText.getText().toString());
        editor.putFloat("Starting weight", weight);
        editor.putFloat("Current weight", weight);

        //height
        float height = Float.parseFloat(heightEditText.getText().toString());
        editor.putFloat("Height", height);

        //goal
        editor.putBoolean("Lose weight", lose_weight);
        editor.putBoolean("Gain muscle", gain_muscle);

        //goal weight
        float goal_weight = Float.parseFloat(goalWeightEditText.getText().toString());
        editor.putFloat("Goal weight", goal_weight);

        //date of birth
        int year = datePicker.getYear();
        editor.putInt("Year", year);
        //int month = datePicker.getMonth() + 1;
        int month = datePicker.getMonth();
        editor.putInt("Month", month);
        int day = datePicker.getDayOfMonth();
        editor.putInt("Day", day);

        Calendar c = Calendar.getInstance();
        int reg_year = c.get(Calendar.YEAR);
        //int reg_month = c.get(Calendar.MONTH) + 1;
        int reg_month = c.get(Calendar.MONTH);

        int reg_day = c.get(Calendar.DAY_OF_MONTH);

        editor.putInt("Registration year", reg_year);
        editor.putInt("Registration month", reg_month);
        editor.putInt("Registration day", reg_day);

        editor.putBoolean("Notifications on", true);

        editor.apply();

        setFirstLoginFalse();
    }
     */

    public boolean checkDataValidity() {
        int name_length = nameEditText.getText().toString().length();
        int weight_length = weightEditText.getText().toString().length();
        int height_length = heightEditText.getText().toString().length();
        int goal_length = goalWeightEditText.getText().toString().length();

        if (name_length == 0 || weight_length == 0 || height_length == 0 || goal_length == 0 || (!female && !male) || (!lose_weight && !gain_muscle)) {
            return false;
        } else
            return true;
    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.login_negative, Toast.LENGTH_LONG);
        toast.show();
    }


    public void setFirstLoginFalse() {
        SharedPreferences first = getSharedPreferences(MainActivity.FIRST, MODE_PRIVATE);
        SharedPreferences.Editor first_editor = first.edit();
        first_editor.putBoolean("First", false);
        first_editor.putBoolean("Load database", true);
        first_editor.apply();
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
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //}

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /*
    public void loadExercises() {

        database = Room.databaseBuilder(
                getApplicationContext(),
                ExerciseListDatabase.class,
                "exercises"
        ).build();

        final ArrayList<ExerciseItem> list = fillExerciseList();

        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                for (int i = 0; i < list.size(); i++) {
                    database.exerciseItemDao().insert(list.get(i));
                }
                return list;
            }
        }.execute();

    }


    public ArrayList<ExerciseItem> fillExerciseList() {
        Resources resources = getResources();
        String str;
        ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();

        int resID = resources.getIdentifier("hu.bme.aut.fitnessapp:raw/" + "exercises", null, null);
        InputStream is = resources.openRawResource(resID);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((str = br.readLine()) != null) {
                String[] line = str.split("\t");
                ExerciseItem newItem = new ExerciseItem();

                newItem.exercise_name = line[0];

                newItem.equipment1 = Integer.parseInt(line[1]);
                newItem.equipment2 = Integer.parseInt(line[2]);
                String[] muscles = line[3].split(", ");
                ArrayList<String> musclesList = new ArrayList<>();
                for (int i = 0; i < muscles.length; i++) {
                    musclesList.add(muscles[i]);
                }
                newItem.exercise_muscles = musclesList;
                newItem.reps_time = Integer.parseInt(line[4]);

                exerciseItems.add(newItem);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exerciseItems;
    }

     */

    private void writeNewUser() {
        String userId = mAuth.getCurrentUser().getUid();
        int gender;
        if(male) gender = 0;
        else gender = 1;
        User user = new User(
                nameEditText.getText().toString(),
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                gain_muscle,
                lose_weight,
                gender,
                Double.parseDouble(weightEditText.getText().toString()),
                Double.parseDouble(goalWeightEditText.getText().toString()),
                Double.parseDouble(heightEditText.getText().toString())
                );
        database.child("Profiles").child(userId).setValue(true);
        database.child("Users").child(userId).setValue(user);
    }

}