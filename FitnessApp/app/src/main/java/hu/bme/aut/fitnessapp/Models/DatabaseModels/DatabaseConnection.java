package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseConnection {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;


    public DatabaseConnection() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public String getUserId() {
        return userId;
    }
}
