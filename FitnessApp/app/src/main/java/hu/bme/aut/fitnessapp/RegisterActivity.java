package hu.bme.aut.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.models.Gym;
import hu.bme.aut.fitnessapp.models.Profile;
import hu.bme.aut.fitnessapp.models.User;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;

    private Spinner userType;
    private EditText email;
    private EditText password;
    private EditText password2;
    private Button register;

    private DatabaseReference database;

    private Boolean type_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();


        userType = findViewById(R.id.userTypeSpinner);
        userType.setOnItemSelectedListener(this);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        password2 = findViewById(R.id.password2EditText);
        register = findViewById(R.id.registerButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(password2.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
                                    if(type_user) {
                                        Intent intent= new Intent(RegisterActivity.this, UserActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);                                    }
                                    else {
                                        writeNewGym();
                                        Intent intent= new Intent(RegisterActivity.this, GymMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            type_user = true;
        }
        else {
            type_user = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(RegisterActivity.this, "Please choose profile type!", Toast.LENGTH_LONG).show();
    }

    private void writeNewGym() {
        String userId = mAuth.getCurrentUser().getUid();

        database.child("Profiles").child(userId).setValue(false);
        //List<Integer> public_locations = new ArrayList<>();
        //Gym gym = new Gym(public_locations);
        //Gym gym = new Gym(1, 2);
        //database.child("Gyms").child(userId).setValue(gym);
        //database.child("Gyms").child(userId).setValue(gym);
    }

}