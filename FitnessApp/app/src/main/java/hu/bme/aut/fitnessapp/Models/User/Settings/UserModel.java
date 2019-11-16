package hu.bme.aut.fitnessapp.Models.User.Settings;

import android.content.Context;
import android.content.SharedPreferences;
//import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Entities.User;

import static android.content.Context.MODE_PRIVATE;

public class UserModel extends UserSettingsModel {

    private Context activity;

    public interface RegisterCanceledListener {
        void onRegisterCanceled();
    }

    private UserModel.RegisterCanceledListener registerCanceledListener;

    public UserModel(Context activity) {
        registerCanceledListener = (UserModel.RegisterCanceledListener)activity;
        this.activity = activity;
    }

    @Override
    public void initFirebase() {
        setDatabase(FirebaseDatabase.getInstance().getReference());
        setmAuth(FirebaseAuth.getInstance());
        setUserId(getmAuth().getCurrentUser().getUid());
    }

    public void removeUser() {
        getmAuth().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getDatabase().child("Profiles").child(getUserId()).removeValue();
                            getDatabase().child("Users").child(getUserId()).removeValue();
                            registerCanceledListener.onRegisterCanceled();
                            //((UserActivity)activity).removedUser();
                        }
                    }
                });
    }

    public boolean isValid(int name_length, int height_length, int goal_length, int weight_length) {
        return !(name_length == 0 || weight_length == 0 || height_length == 0 || goal_length == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLose_weight() && !isGain_muscle()));
    }

    public void backPressed() {
        getDatabase().child("Profiles").child(getUserId()).removeValue();
        getDatabase().child("Users").child(getUserId()).removeValue();
    }


    public void writeNewUser(String name, int year, int month, int day, String goal, String height, String weight) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getUserId(), true);
        editor.apply();

        String userId = getmAuth().getCurrentUser().getUid();
        User user = newUser(name, year, month, day, goal, height);

        getDatabase().child("Users").child(userId).setValue(user);
        getDatabase().child("Workout_Details").child(userId).child("In_Progress").setValue(false);

        String type = "";
        if(isGain_muscle()) type = "Lower body";
        else type = "Cardio 1";

        getDatabase().child("Workout_Details").child(userId).child("Type").setValue(type);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long date = calendar.getTimeInMillis() / 1000;

        getDatabase().child("Water").child(userId).child(Long.toString(date)).setValue(0);
        getDatabase().child("Weight").child(userId).child(Long.toString(date)).setValue(Double.parseDouble(weight));
    }

}