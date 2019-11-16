package hu.bme.aut.fitnessapp.Controllers.User.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hu.bme.aut.fitnessapp.Models.User.Settings.SettingsModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;

public class SettingsActivity extends NavigationActivity implements SettingsModel.SettingsListener{

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
    private EditText heightEditText;
    private EditText goalWeightEditText;
    private DatePicker datePicker;

    private SettingsModel settingsModel;

    public static final String NOTIFICATIONS = "Notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(5).setChecked(true);

        settingsModel = new SettingsModel(this);
        settingsModel.initFirebase();
        settingsModel.loadUserdata();

        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsModel.checkChanged(isChecked);
            }
        });
        notificationSwitch.setChecked(settingsModel.isChecked());

        assignLayoutElements();



        setFloatingActionButton();
        setGenderOnClickListeners();
        setGoalOnClickListeners();

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_check));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataValidity()) {
                    settingsModel.saveUserData(nameEditText.getText().toString(), datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                            goalWeightEditText.getText().toString(), heightEditText.getText().toString());
                    Toast toast = Toast.makeText(getApplicationContext(), "Changes saved.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_negative, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void assignLayoutElements() {
        nameEditText = findViewById(R.id.nameEditText);
        heightEditText = findViewById(R.id.heightEditText);
        goalWeightEditText = findViewById(R.id.goalWeightEditText);
        maleButton = findViewById(R.id.buttonMale);
        femaleButton = findViewById(R.id.buttonFemale);
        loseWeightButton = findViewById(R.id.buttonLoseWeight);
        gainMuscleButton = findViewById(R.id.buttonGainMuscle);

        datePicker = findViewById(R.id.dateOfBirthPicker);
    }

    private void setGenderOnClickListeners() {

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsModel.maleSetting();
                setGenderButtons();
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsModel.femaleSetting();
                setGenderButtons();
            }
        });
    }

    public void setGenderButtons() {
        if (settingsModel.isMale()) {
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
                settingsModel.loseWeightSetting();
                setLoseWeightButton();
            }
        });

        gainMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsModel.gainMuscleSetting();
                setMuscleButton();
            }
        });
    }

    public void setLoseWeightButton() {
        if (settingsModel.isLose_weight()) {
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose));
        } else
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose_disabled));
    }

    public void setMuscleButton() {
        if (settingsModel.isGain_muscle()) {
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle));
        } else
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle_disabled));
    }

    public void setUserDetails(String name, String height, String goal, int year, int month, int day) {
        nameEditText.setText(name);
        heightEditText.setText(height);
        goalWeightEditText.setText(goal);

        setGenderButtons();
        setLoseWeightButton();
        setMuscleButton();

        datePicker.updateDate(year, month, day);
        datePicker.setMaxDate(System.currentTimeMillis());
    }


    public boolean checkDataValidity() {
        int name_length = nameEditText.getText().toString().length();
        int height_length = heightEditText.getText().toString().length();
        int goal_length = goalWeightEditText.getText().toString().length();

        return settingsModel.isValid(name_length, height_length, goal_length);
    }


    public void setMale(boolean male) {
        this.male = male;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public void setLose_weight(boolean lose_weight) {
        this.lose_weight = lose_weight;
    }

    public void setGain_muscle(boolean gain_muscle) {
        this.gain_muscle = gain_muscle;
    }

    @Override
    public void onSettingsLoaded(String name, String height, String weight, int year, int month, int day) {
        setUserDetails(name, height, weight, year, month, day);
    }
}
