package hu.bme.aut.fitnessapp.Controllers.Startup;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import hu.bme.aut.fitnessapp.Controllers.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Models.Startup.RegisterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.Settings.UserActivity;

public class RegisterActivity extends InternetCheckActivity implements AdapterView.OnItemSelectedListener, RegisterModel.RegisterListener {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private EditText password2;
    private Button register;
    private ProgressBar progressBar;

    private DatabaseReference database;

    private Boolean type_user;

    private RegisterModel registerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setToolbar();
        assignLayoutElements();
        setSpinner();

        registerModel = new RegisterModel(this);
        registerModel.initFirebase();

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
                    registerModel.registerAccount(email.getText().toString(), password.getText().toString());
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

    public void registerToast() {
        Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG).show();
    }

    public void startUserActivity(){
        Intent intent= new Intent(RegisterActivity.this, UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void startGymActivity() {
        Intent intent= new Intent(RegisterActivity.this, GymMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void errorMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        registerModel.selectItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(RegisterActivity.this, "Please choose profile type!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterSuccess() {
        registerToast();
    }

    @Override
    public void onUserRegister() {
        startUserActivity();
    }

    @Override
    public void onGymRegister() {
        startGymActivity();
    }

    @Override
    public void onRegisterError(String message) {
        errorMessage(message);
    }
}