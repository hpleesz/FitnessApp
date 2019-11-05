package hu.bme.aut.fitnessapp.User.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.InternetCheckActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Startup.LoginActivity;
import hu.bme.aut.fitnessapp.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Models.User;

public class UserActivity extends InternetCheckActivity {

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

    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    database.child("Profiles").child(userId).removeValue();
                                    database.child("Users").child(userId).removeValue();
                                    Toast toast = Toast.makeText(getApplicationContext(), "Registration canceled", Toast.LENGTH_LONG);
                                    toast.show();
                                    Intent intent= new Intent(UserActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });


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
                if (checkDataValidity()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(userId, true);
                    editor.apply();

                    writeNewUser();
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
        database.child("Profiles").child(userId).removeValue();
        database.child("Users").child(userId).removeValue();
        Intent intent= new Intent(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast toast = Toast.makeText(getApplicationContext(), "Registration canceled", Toast.LENGTH_LONG);
        toast.show();
    }



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
                Double.parseDouble(goalWeightEditText.getText().toString()),
                Double.parseDouble(heightEditText.getText().toString())
                );

        database.child("Users").child(userId).setValue(user);
        database.child("Workout_Details").child(userId).child("In_Progress").setValue(false);

        String type = "";
        if(gain_muscle) type = "Lower Body";
        else type = "Cardio 1";

        database.child("Workout_Details").child(userId).child("Type").setValue(type);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long date = calendar.getTimeInMillis() / 1000;

        database.child("Measurement").child(userId).child(Long.toString(date)).setValue(Double.parseDouble(weightEditText.getText().toString()));
    }

}