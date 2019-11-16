package hu.bme.aut.fitnessapp.Models.Startup;


import android.content.Context;
//import android.support.annotation.NonNull;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterModel {

    private FirebaseAuth mAuth;

    private DatabaseReference database;

    private Boolean type_user;
    private Context activity;

    private RegisterModel.RegisterListener listener;

    public interface RegisterListener {
        void onRegisterSuccess();
        void onUserRegister();
        void onGymRegister();
        void onRegisterError(String message);
    }

    public RegisterModel(Context activity) {
        listener = (RegisterModel.RegisterListener)activity;
        this.activity = activity;

    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void registerAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            listener.onRegisterSuccess();
                            //((RegisterActivity)activity).registerToast();
                            if(type_user) {
                                writeNewUser();
                                listener.onUserRegister();
                                //((RegisterActivity)activity).startUserActivity();
                            }
                            else {
                                writeNewGym();
                                listener.onGymRegister();
                                //((RegisterActivity)activity).startGymActivity();
                            }
                        }
                        else {
                            listener.onRegisterError(task.getException().getMessage());
                            //((RegisterActivity)activity).errorMessage(task.getException().getMessage());
                        }
                    }
                });
    }

    public void selectItem(int position) {
        if(position == 0) {
            type_user = true;
        }
        else {
            type_user = false;
        }
    }

    private void writeNewGym() {
        String userId = mAuth.getCurrentUser().getUid();

        database.child("Profiles").child(userId).setValue(false);
    }

    private void writeNewUser() {
        String userId = mAuth.getCurrentUser().getUid();

        database.child("Profiles").child(userId).setValue(true);
    }

}