package hu.bme.aut.fitnessapp.Controllers.Gym;

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

import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Models.GymModels.GymMainModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.Startup.LoginActivity;
import hu.bme.aut.fitnessapp.Controllers.Adapters.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class GymMainActivity extends InternetCheckActivity implements PublicLocationAdapter.LocationItemDeletedListener, PublicLocationAdapter.LocationItemSelectedListener,
        GymMainModel.ListLoaded{

    private GymMainModel gymMainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PublicLocation publicLocation = getPublicLocationIntent();
        gymMainModel = new GymMainModel(this, publicLocation);
        gymMainModel.loadList();

        setFloatingActionButton();

    }

    public PublicLocation getPublicLocationIntent() {
        Intent i = getIntent();
        return (PublicLocation) i.getSerializableExtra("new");

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GymMainActivity.this, NewPublicLocationActivity.class));

            }
        });
    }

    public void initRecyclerView(ArrayList<PublicLocation> itemlist) {
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
        switch (item.getItemId()) {
            case R.id.action_logout:
                gymMainModel.signOut();
                Intent intent= new Intent(GymMainActivity.this, LoginActivity.class);
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
}


