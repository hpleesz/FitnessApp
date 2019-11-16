package hu.bme.aut.fitnessapp.Models.User.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SettingsModel extends UserSettingsModel implements LoadUser.UserLoadedListener{

    private Switch notificationSwitch;
    private SharedPreferences sharedPreferences;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userID;

    private Context activity;

    public static final String NOTIFICATIONS = "Notifications";


    public interface SettingsListener{
        void onSettingsLoaded(String name, String height, String weight, int year, int month, int day);
    }

    private SettingsModel.SettingsListener settingsListener;

    public SettingsModel(Context activity) {
        settingsListener = (SettingsModel.SettingsListener)activity;
        this.activity = activity;

        sharedPreferences = ((SettingsActivity)activity).getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);

        //loadUserdata();

    }

    @Override
    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

    }

    public boolean isChecked() {
        return sharedPreferences.getBoolean(userID, true);
    }
    public void checkChanged(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putBoolean(userID, true);
        } else {
            editor.putBoolean(userID, false);
        }
        editor.apply();
    }


    public void saveUserData(String name, int year, int month, int day, String goal, String height) {
        User user = newUser(name, year, month, day, goal, height);
        databaseReference.setValue(user);
    }

    public boolean isValid(int name_length, int height_length, int goal_length) {
        return !(name_length == 0 || height_length == 0 || goal_length == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLose_weight() && !isGain_muscle()));
    }

    public void loadUserdata() {
        LoadUser loadUser = new LoadUser(this);
        loadUser.loadUser();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                male = user.gender != 1;
                female = !male;

                lose_weight = user.lose_weight;
                gain_muscle = user.gain_muscle;

                settingsListener.onSettingsLoaded(user.name, Double.toString(user.height), Double.toString(user.goal_weight), user.year, user.month, user.day);
                //((SettingsActivity)activity).setUserDetails(user.name, Double.toString(user.height), Double.toString(user.goal_weight), user.year, user.month, user.day);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(eventListener);

        this.eventListener = eventListener;

         */
    }

    @Override
    public void onUserLoaded(User user) {
        setMale(user.gender != 1);
        setFemale(!isMale());

        setLose_weight(user.lose_weight);
        setGain_muscle(user.gain_muscle);

        settingsListener.onSettingsLoaded(user.name, Double.toString(user.height), Double.toString(user.goal_weight), user.year, user.month, user.day);

    }

}
