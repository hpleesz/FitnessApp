package hu.bme.aut.fitnessapp.models.startup_models;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.models.database_models.LoadProfile;

public class RegisterModel {

    private FirebaseAuth mAuth;
    private Boolean typeUser;
    private LoadProfile loadProfile;

    private RegisterModel.RegisterListener listener;

    public interface RegisterListener {
        void onRegisterSuccess();
        void onUserRegister();
        void onGymRegister();
        void onRegisterError(String message);
    }

    public RegisterModel(Object object) {
        listener = (RegisterModel.RegisterListener)object;
    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void registerAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            listener.onRegisterSuccess();
                            if(typeUser) {
                                writeNewUser();
                                listener.onUserRegister();
                            }
                            else {
                                writeNewGym();
                                listener.onGymRegister();
                            }
                        }
                        else {
                            listener.onRegisterError(task.getException().getMessage());
                        }
                    }
                });
    }

    public void selectItem(int position) {
        if(position == 0) {
            typeUser = true;
        }
        else {
            typeUser = false;
        }
    }

    private void writeNewGym() {
        loadProfile = new LoadProfile();
        loadProfile.addNewItem(false);
    }

    private void writeNewUser() {
        loadProfile = new LoadProfile();
        loadProfile.addNewItem(true);
    }

}