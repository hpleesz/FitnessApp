package hu.bme.aut.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


public class MainActivity extends NavigationActivity {

    public static final String FIRST = "first sign in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        checkFirstSignIn();

    }

    public void checkFirstSignIn() {
        SharedPreferences first = getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean isFirst = first.getBoolean("First", true);
        if(isFirst) {
            Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(userIntent);
        }
    }

}
