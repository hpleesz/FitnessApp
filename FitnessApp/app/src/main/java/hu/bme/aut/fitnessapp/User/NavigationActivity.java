package hu.bme.aut.fitnessapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.InternetCheckActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Startup.LoginActivity;
import hu.bme.aut.fitnessapp.User.Locations.LocationActivity;
import hu.bme.aut.fitnessapp.User.Measurements.MeasurementsActivity;
import hu.bme.aut.fitnessapp.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.User.Water.WaterActivity;
import hu.bme.aut.fitnessapp.User.Weight.WeightActivity;
import hu.bme.aut.fitnessapp.User.Workout.MainActivity;

public class NavigationActivity extends InternetCheckActivity {


    protected DrawerLayout mDrawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()) {
                            case R.id.nav_workout: {
                                Intent userIntent = new Intent(NavigationActivity.this, MainActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_measurements: {
                                Intent userIntent = new Intent(NavigationActivity.this, MeasurementsActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_weight: {
                                Intent userIntent = new Intent(NavigationActivity.this, WeightActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_location: {
                                Intent userIntent = new Intent(NavigationActivity.this, LocationActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_water: {
                                Intent userIntent = new Intent(NavigationActivity.this, WaterActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_settings: {
                                Intent userIntent = new Intent(NavigationActivity.this, SettingsActivity.class);
                                startActivity(userIntent);
                                break;
                            }
                            case R.id.nav_logout: {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent= new Intent(NavigationActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                            }
                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            //case R.id.action_settings:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else {
            layout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

}
