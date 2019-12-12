package hu.bme.aut.fitnessapp.controllers.user.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hu.bme.aut.fitnessapp.models.user_models.settings_models.SettingsModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.NavigationActivity;

public class SettingsActivity extends NavigationActivity implements SettingsModel.SettingsListener{

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

        assignLayoutElements();

        setFloatingActionButton();
        setGenderOnClickListeners();
        setGoalOnClickListeners();

    }

    @Override
    public void onStart() {
        super.onStart();
        settingsModel = new SettingsModel(this);
        settingsModel.initFirebase();
        settingsModel.loadUserdata();

        Switch notificationSwitch = findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsModel.checkChanged(isChecked);
            }
        });
        notificationSwitch.setChecked(settingsModel.isChecked());
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
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
        if (settingsModel.isLoseWeight()) {
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose));
        } else
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose_disabled));
    }

    public void setMuscleButton() {
        if (settingsModel.isGainMuscle()) {
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
        int nameLength = nameEditText.getText().toString().length();
        int heightLength = heightEditText.getText().toString().length();
        int goalLength = goalWeightEditText.getText().toString().length();

        return settingsModel.isValid(nameLength, heightLength, goalLength);
    }

    @Override
    public void onSettingsLoaded(String name, String height, String weight, int year, int month, int day) {
        setUserDetails(name, height, weight, year, month, day);
    }

    @Override
    public void onStop() {
        super.onStop();
        settingsModel.removeListeners();
    }
}
