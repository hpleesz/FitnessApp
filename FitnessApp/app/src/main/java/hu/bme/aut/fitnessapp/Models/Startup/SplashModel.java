package hu.bme.aut.fitnessapp.Models.Startup;

import android.content.Context;
import android.content.SharedPreferences;
//import android.support.annotation.NonNull;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadProfile;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SplashModel implements LoadProfile.ProfileLoadedListener, LoadUser.UserLoadedListener {

    private FirebaseAuth mAuth;
    private boolean isUser;
    private String userId;
    private DatabaseReference databaseReference;

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

        //checkFirstLogin();
    }

    public void checkFirstLogin() {
        SharedPreferences first = activity.getSharedPreferences(LoginModel.FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);

        if (first_login) {
            listener.onNoActiveUser();
            //((SplashActivity)activity).startLogin();
        } else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                getUser();
            } else {
                listener.onNoActiveUser();
                //((SplashActivity)activity).startLogin();
            }
        }
    }

    public void getUser() {
        LoadProfile loadProfile = new LoadProfile(this);
        loadProfile.loadProfile();
        /*
        userId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = (boolean)dataSnapshot.getValue();

                if(user) {
                    getDetails();
                }
                else {
                    listener.onGymActive();
                    //((SplashActivity)activity).startGym();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Profiles").child(userId).addValueEventListener(eventListener);
    */
    }

    @Override
    public void onProfileLoaded(boolean isUser) {
        this.isUser = isUser;
        if (this.isUser) {
            getDetails();
        } else {
            listener.onGymActive();
        }
    }

    public void getDetails() {
        LoadUser loadUser = new LoadUser(this);
        loadUser.loadUser();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    mAuth.getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child("Profiles").child(userId).removeValue();
                                        listener.onNoActiveUser();
                                        //((SplashActivity)activity).startLogin();
                                    }
                                }
                            });
                }
                else {
                    listener.onUserActive();
                    //((SplashActivity)activity).startUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener);
    }

         */
    }

    @Override
    public void onUserLoaded(User user) {
        if(user == null) {
            mAuth.getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child("Profiles").child(userId).removeValue();
                                listener.onNoActiveUser();
                                //((SplashActivity)activity).startLogin();
                            }
                        }
                    });
        }
        else {
            listener.onUserActive();
            //((SplashActivity)activity).startUser();
        }
    }
}
