package hu.bme.aut.fitnessapp.models.startup_models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadProfile;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SplashModel implements LoadProfile.ProfileLoadedListener, LoadUser.UserLoadedListener {

    private FirebaseAuth mAuth;
    private LoadProfile loadProfile;
    private LoadUser loadUser;

    private Context activity;

    private SplashModel.ActiveUserListener listener;

    public interface ActiveUserListener {
        void onNoActiveUser();
        void onUserActive();
        void onGymActive();
    }


    public SplashModel(Context activity) {
        listener = (SplashModel.ActiveUserListener) activity;
        this.activity = activity;
    }

    public void checkFirstLogin() {
        SharedPreferences first = activity.getSharedPreferences(LoginModel.FIRST, MODE_PRIVATE);
        boolean firstLogin = first.getBoolean("First login", true);

        if (firstLogin) {
            listener.onNoActiveUser();
        } else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                getUser();
            } else {
                listener.onNoActiveUser();
            }
        }
    }

    private void getUser() {
        loadProfile = new LoadProfile();
        loadProfile.setListLoadedListener(this);
        loadProfile.loadProfile();
    }

    @Override
    public void onProfileLoaded(boolean isUser) {
        if (isUser) {
            getDetails();
        } else {
            listener.onGymActive();
        }
    }

    private void getDetails() {
        loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }

    @Override
    public void onUserLoaded(User user) {
        if(user == null) {
            mAuth.getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadProfile.removeItem();
                                listener.onNoActiveUser();
                            }
                        }
                    });
        }
        else {
            listener.onUserActive();
        }
    }

    public void removeListeners() {
        if(loadProfile != null) loadProfile.removeListeners();
        if(loadUser != null) loadUser.removeListeners();
    }
}
