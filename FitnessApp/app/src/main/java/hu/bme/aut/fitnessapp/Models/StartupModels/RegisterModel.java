package hu.bme.aut.fitnessapp.Models.StartupModels;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadProfile;

public class RegisterModel {

    private FirebaseAuth mAuth;
    private Boolean type_user;

    private RegisterModel.RegisterListener listener;

    private LoadProfile loadProfile;

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
                            if(type_user) {
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
            type_user = true;
        }
        else {
            type_user = false;
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