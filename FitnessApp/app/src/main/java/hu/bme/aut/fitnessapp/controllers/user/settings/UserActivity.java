package hu.bme.aut.fitnessapp.controllers.user.settings;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.user_models.settings_models.UserModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.startup.LoginActivity;
import hu.bme.aut.fitnessapp.controllers.user.workout.MainActivity;

public class UserActivity extends InternetCheckActivity implements UserModel.RegisterCanceledListener{

    private ImageButton maleButton;
    private ImageButton femaleButton;
    private ImageButton loseWeightButton;
    private ImageButton gainMuscleButton;
    private EditText nameEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private EditText goalWeightEditText;
    private DatePicker datePicker;

    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        userModel = new UserModel(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel.removeUser();
            }
        });


        assignLayoutElements();
        setFloatingActionButton();

        setGenderOnClickListeners();
        setGoalOnClickListeners();

    }

    public void removedUser() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.registration_canceled_toast, Toast.LENGTH_LONG);
        toast.show();
        Intent intent= new Intent(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void assignLayoutElements() {
        nameEditText = findViewById(R.id.nameEditText);
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
        goalWeightEditText = findViewById(R.id.goalWeightEditText);

        maleButton = findViewById(R.id.buttonMale);
        femaleButton = findViewById(R.id.buttonFemale);
        loseWeightButton = findViewById(R.id.buttonLoseWeight);
        gainMuscleButton = findViewById(R.id.buttonGainMuscle);

        datePicker = findViewById(R.id.dateOfBirthPicker);
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataValidity()) {

                    userModel.writeNewUser(nameEditText.getText().toString(), datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                            goalWeightEditText.getText().toString(), heightEditText.getText().toString(), weightEditText.getText().toString());

                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_positive, Toast.LENGTH_LONG);
                    toast.show();

                    startActivity(new Intent(UserActivity.this, MainActivity.class));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_negative, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }



    private void setGenderOnClickListeners() {
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel.maleSetting();
                setGenderButtons();
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel.femaleSetting();
                setGenderButtons();
            }
        });
    }

    private void setGenderButtons() {
        if (userModel.isMale()) {
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
                userModel.loseWeightSetting();
                setLoseWeightButton();
            }
        });

        gainMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModel.gainMuscleSetting();
                setMuscleButton();
            }
        });
    }

    public void setLoseWeightButton() {
        if (userModel.isLoseWeight()) {
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose));
        } else
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose_disabled));
    }

    public void setMuscleButton() {
        if (userModel.isGainMuscle()) {
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle));
        } else
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle_disabled));
    }


    public boolean checkDataValidity() {
        int nameLength = nameEditText.getText().toString().length();
        int weightLength = weightEditText.getText().toString().length();
        int heightLength = heightEditText.getText().toString().length();
        int goalLength = goalWeightEditText.getText().toString().length();

        return userModel.isValid(nameLength, heightLength, goalLength, weightLength);
    }

    @Override
    public void onBackPressed() {
        userModel.backPressed();

        Intent intent= new Intent(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast toast = Toast.makeText(getApplicationContext(), R.string.registration_canceled_toast, Toast.LENGTH_LONG);
        toast.show();
    }


    @Override
    public void onRegisterCanceled() {
        removedUser();
    }

}