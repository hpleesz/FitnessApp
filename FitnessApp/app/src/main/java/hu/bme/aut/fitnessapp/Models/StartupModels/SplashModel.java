package hu.bme.aut.fitnessapp.Models.StartupModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadProfile;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SplashModel implements LoadProfile.ProfileLoadedListener, LoadUser.UserLoadedListener {

    private FirebaseAuth mAuth;
    private LoadProfile loadProfile;

    private Context activity;

    public interface ActiveUserListener {
        void onNoActiveUser();
        void onUserActive();
        void onGymActive();
    }

    private SplashModel.ActiveUserListener listener;

    public SplashModel(Context activity) {
        listener = (SplashModel.ActiveUserListener) activity;
        this.activity = activity;
    }

    public void checkFirstLogin() {
        SharedPreferences first = activity.getSharedPreferences(LoginModel.FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);

        if (first_login) {
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
        LoadUser loadUser = new LoadUser();
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
}
