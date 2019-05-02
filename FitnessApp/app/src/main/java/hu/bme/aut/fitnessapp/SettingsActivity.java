package hu.bme.aut.fitnessapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends NavigationActivity {

    private Switch notificationSwitch;
    private SharedPreferences sharedPreferences;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(5).setChecked(true);


        sharedPreferences = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);


        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean("Notifications on", true);
                } else {
                    editor.putBoolean("Notifications on", false);
                }
                editor.apply();
            }
        });

        notificationSwitch.setChecked(sharedPreferences.getBoolean("Notifications on", true));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_check));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataValidity()) {
                    saveUserData();
                    Toast toast = Toast.makeText(getApplicationContext(), "Changes saved.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_negative, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        assignLayoutElements();
        setGenderOnClickListeners();
        setGoalOnClickListeners();

    }

    public void assignLayoutElements() {
        nameEditText = findViewById(R.id.nameEditText);
        String name = sharedPreferences.getString("Name", "");
        nameEditText.setText(name);

        weightEditText = findViewById(R.id.weightEditText);
        float starting_weight = sharedPreferences.getFloat("Starting weight", 0);
        weightEditText.setText(Float.toString(starting_weight));

        heightEditText = findViewById(R.id.heightEditText);
        float height = sharedPreferences.getFloat("Height", 0);
        heightEditText.setText(Float.toString(height));

        goalWeightEditText = findViewById(R.id.goalWeightEditText);
        float goal_weight = sharedPreferences.getFloat("Goal weight", 0);
        goalWeightEditText.setText(Float.toString(goal_weight));

        maleButton = findViewById(R.id.buttonMale);
        femaleButton = findViewById(R.id.buttonFemale);
        loseWeightButton = findViewById(R.id.buttonLoseWeight);
        gainMuscleButton = findViewById(R.id.buttonGainMuscle);

        male = sharedPreferences.getBoolean("Male", true);
        female = !male;
        lose_weight = sharedPreferences.getBoolean("Lose weight", true);
        gain_muscle = sharedPreferences.getBoolean("Gain muscle", true);
        setGenderButtons();
        setLoseWeightButton();
        setMuscleButton();

        datePicker = findViewById(R.id.dateOfBirthPicker);
        int year = sharedPreferences.getInt("Year", 2019);
        int month = sharedPreferences.getInt("Month", 1);
        int day = sharedPreferences.getInt("Day", 1);
        datePicker.updateDate(year, month, day);
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    private void setGenderOnClickListeners() {

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


    private void saveUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

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

        SharedPreferences workout_settings = getSharedPreferences(MainActivity.WORKOUT, MODE_PRIVATE);
        SharedPreferences.Editor workout_editor = workout_settings.edit();

        if (gain_muscle) workout_editor.putString("Workout type", "Lower body");
        else workout_editor.putString("Workout type", "Cardio 1");

        workout_editor.apply();

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

        editor.putBoolean("Notifications on", true);

        editor.apply();
    }

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

}
