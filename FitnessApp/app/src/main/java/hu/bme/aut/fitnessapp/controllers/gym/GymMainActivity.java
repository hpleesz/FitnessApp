package hu.bme.aut.fitnessapp.controllers.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.gym_models.GymMainModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.startup.LoginActivity;
import hu.bme.aut.fitnessapp.controllers.adapters.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class GymMainActivity extends InternetCheckActivity implements PublicLocationAdapter.LocationItemDeletedListener, PublicLocationAdapter.LocationItemSelectedListener,
        GymMainModel.ListLoaded{

    private GymMainModel gymMainModel;
    private PublicLocation publicLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        publicLocation = getPublicLocationIntent();

        setFloatingActionButton();

    }


    @Override
    public void onStart() {
        super.onStart();
        gymMainModel = new GymMainModel(this, publicLocation);
        gymMainModel.loadList();
    }

    public PublicLocation getPublicLocationIntent() {
        Intent i = getIntent();
        return (PublicLocation) i.getSerializableExtra("new");

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GymMainActivity.this, NewPublicLocationActivity.class));

            }
        });
    }

    public void initRecyclerView(List<PublicLocation> itemlist) {
        RecyclerView recyclerView = findViewById(R.id.PublicLocationRecyclerView);
        PublicLocationAdapter adapter = new PublicLocationAdapter(this, this, itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemDeleted(final PublicLocation item) {
        gymMainModel.deleteItem(item);
    }

    @Override
    public void onItemSelected(final PublicLocation item, int position) {
        Intent intent = new Intent(GymMainActivity.this, EditPublicLocationActivity.class);
        intent.putExtra("edit", item);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gym, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            gymMainModel.signOut();
            Intent intent = new Intent(GymMainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(true);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onListLoaded(ArrayList<PublicLocation> locations) {
        initRecyclerView(locations);
    }

    @Override
    public void onStop() {
        super.onStop();
        gymMainModel.removeListeners();
    }
}


