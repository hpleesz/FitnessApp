package hu.bme.aut.fitnessapp.controllers.user;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.startup.LoginActivity;
import hu.bme.aut.fitnessapp.controllers.user.locations.LocationActivity;
import hu.bme.aut.fitnessapp.controllers.user.measurements.MeasurementsActivity;
import hu.bme.aut.fitnessapp.controllers.user.settings.SettingsActivity;
import hu.bme.aut.fitnessapp.controllers.user.water.WaterActivity;
import hu.bme.aut.fitnessapp.controllers.user.weight.WeightActivity;
import hu.bme.aut.fitnessapp.controllers.user.workout.MainActivity;

public class NavigationActivity extends InternetCheckActivity {


    protected DrawerLayout mDrawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

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
                            default:
                            case R.id.nav_workout:
                                navigateToActivity(MainActivity.class);
                                break;
                            case R.id.nav_measurements:
                                navigateToActivity(MeasurementsActivity.class);
                                break;
                            case R.id.nav_weight:
                                navigateToActivity(WeightActivity.class);
                                break;
                            case R.id.nav_location:
                                navigateToActivity(LocationActivity.class);
                                break;
                            case R.id.nav_water:
                                navigateToActivity(WaterActivity.class);
                                break;
                            case R.id.nav_settings:
                                navigateToActivity(SettingsActivity.class);
                                break;
                            case R.id.nav_logout:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
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
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout = findViewById(R.id.drawer_layout);
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

    private void navigateToActivity(Class<?> activity) {
        Intent userIntent = new Intent(NavigationActivity.this, activity);
        startActivity(userIntent);
    }

}
