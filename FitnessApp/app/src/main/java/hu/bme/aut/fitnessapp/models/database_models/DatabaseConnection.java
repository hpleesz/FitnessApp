package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.log4j.Logger;

public class DatabaseConnection {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public static final String DB_ERROR = "Database error";
    public static final Logger logger = Logger.getLogger(DatabaseConnection.class);



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
