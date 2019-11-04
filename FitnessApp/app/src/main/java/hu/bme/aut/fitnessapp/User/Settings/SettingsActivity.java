package hu.bme.aut.fitnessapp.User.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Models.User;

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
    private EditText heightEditText;
    private EditText goalWeightEditText;
    private DatePicker datePicker;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ValueEventListener eventListener;

    public static final String NOTIFICATIONS = "Notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(5).setChecked(true);

        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        sharedPreferences = getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);


        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putBoolean(userID, true);
                } else {
                    editor.putBoolean(userID, false);
                }
                editor.apply();
            }
        });

        notificationSwitch.setChecked(sharedPreferences.getBoolean(userID, true));

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
            databaseReference.setValue(user);
    }

    public boolean checkDataValidity() {
        int name_length = nameEditText.getText().toString().length();
        int height_length = heightEditText.getText().toString().length();
        int goal_length = goalWeightEditText.getText().toString().length();

        if (name_length == 0 || height_length == 0 || goal_length == 0 || (!female && !male) || (!lose_weight && !gain_muscle)) {
            return false;
        } else
            return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameEditText.setText(user.name);
                heightEditText.setText(Double.toString(user.height));
                goalWeightEditText.setText(Double.toString(user.goal_weight));

                male = user.gender != 1;
                female = !male;

                lose_weight = user.lose_weight;
                gain_muscle = user.gain_muscle;
                setGenderButtons();
                setLoseWeightButton();
                setMuscleButton();

                datePicker.updateDate(user.year, user.month, user.day);
                datePicker.setMaxDate(System.currentTimeMillis());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(eventListener);

        this.eventListener = eventListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }


}
