package hu.bme.aut.fitnessapp.Controllers.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import hu.bme.aut.fitnessapp.Controllers.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Models.StartupModels.LoginModel;
import hu.bme.aut.fitnessapp.R;

public class LoginActivity extends InternetCheckActivity implements LoginModel.loginListener{

    private TextView register;
    private Button login;
    private EditText email;
    private EditText password;

    private ProgressBar progressBar;

    private LoginModel loginModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeLayoutElements();
        loginModel = new LoginModel(this);
        loginModel.initFirebase();

        setLoginClickListener();
        setRegisterClickListener();
    }

    public void initializeLayoutElements() {
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        register = findViewById(R.id.registerTextView);
        login = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    public void startUserActivity() {
        Intent intent= new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void startGymActivity() {
        Intent intent= new Intent(LoginActivity.this, GymMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void errorMessage(String message) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        login.setEnabled(true);
        register.setEnabled(true);
        login.setBackground(getResources().getDrawable(R.drawable.button_round));
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

    }

    public void setLoginClickListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                login.setEnabled(false);
                register.setEnabled(false);
                login.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));
                loginModel.signIn(email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void setRegisterClickListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    @Override
    public void onUserLogin() {
        startUserActivity();
    }

    @Override
    public void onGymLogin() {
        startGymActivity();
    }

    @Override
    public void onLoginError(String message) {
        errorMessage(message);
    }
}
