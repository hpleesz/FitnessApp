package hu.bme.aut.fitnessapp.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hu.bme.aut.fitnessapp.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.Settings.UserActivity;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private EditText password2;
    private Button register;
    private ProgressBar progressBar;

    private DatabaseReference database;

    private Boolean type_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setToolbar();
        assignLayoutElements();
        setSpinner();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        setRegisterClickListener();
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void assignLayoutElements() {
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        password2 = findViewById(R.id.password2EditText);
        register = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    public void setSpinner() {
        Spinner userType = findViewById(R.id.userTypeSpinner);
        userType.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
    }

    public void setRegisterClickListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                register.setEnabled(false);
                register.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));

                if(password.getText().toString().equals(password2.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
                                        if(type_user) {
                                            writeNewUser();
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
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    register.setEnabled(true);
                    register.setBackground(getResources().getDrawable(R.drawable.button_round));
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
    }

    private void writeNewUser() {
        String userId = mAuth.getCurrentUser().getUid();

        database.child("Profiles").child(userId).setValue(true);
    }

}