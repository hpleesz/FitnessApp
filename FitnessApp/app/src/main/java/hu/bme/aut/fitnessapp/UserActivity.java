package hu.bme.aut.fitnessapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assignLayoutElements();
        setFloatingActionButton();
        setGenderOnClickListeners();
        setGoalOnClickListeners();

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
                if(checkDataValidity()){
                    saveUserData();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.login_positive, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                else {
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
        }
        else {
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
                if(lose_weight)
                    lose_weight = false;
                else
                    lose_weight = true;
                setLoseWeightButton();
            }
        });

        gainMuscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gain_muscle)
                    gain_muscle = false;
                else
                    gain_muscle = true;
                setMuscleButton();
            }
        });
    }


    private void setLoseWeightButton() {
        if(lose_weight) {
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose));
        }
        else
            loseWeightButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_lose_disabled));
    }

    private void setMuscleButton() {
        if(gain_muscle) {
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle));
        }
        else
            gainMuscleButton.setImageDrawable(getResources().getDrawable(R.drawable.goal_muscle_disabled));
    }


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
        int month = datePicker.getMonth() + 1;
        editor.putInt("Month", month);
        int day = datePicker.getDayOfMonth();
        editor.putInt("Day", day);

        editor.apply();

        setFirstLoginFalse();
    }

    public boolean checkDataValidity() {
        int name_length = nameEditText.getText().toString().length();
        int weight_length = weightEditText.getText().toString().length();
        int height_length = heightEditText.getText().toString().length();
        int goal_length = goalWeightEditText.getText().toString().length();

        if(name_length == 0 || weight_length == 0 || height_length == 0 || goal_length == 0 || (!female && !male) || (!lose_weight && !gain_muscle)) {
            return false;
        }
        else
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
        first_editor.apply();
    }

}
