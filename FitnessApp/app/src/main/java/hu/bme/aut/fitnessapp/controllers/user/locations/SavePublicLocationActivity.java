package hu.bme.aut.fitnessapp.controllers.user.locations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.controllers.gym.NewPublicLocationActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class SavePublicLocationActivity extends NewPublicLocationActivity implements EquipmentAdapter.OnCheckBoxClicked, PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uncheckCheckBoxes();

    }

    public void uncheckCheckBoxes() {
            for(final CheckBox checkBox : getCheckBoxes()) {
                checkBox.setChecked(false);
            }
    }

    @Override
    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Boolean> openDays = new ArrayList<>();
                for(CheckBox checkBox : getCheckBoxes()) {
                    if(checkBox.isChecked()) openDays.add(true);
                    else openDays.add(false);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("Location", getLocationItem());
                bundle.putSerializable("Checkboxes", openDays);
                PublicLocationSearchMatchDialogFragment fragment = new PublicLocationSearchMatchDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), PublicLocationSearchMatchDialogFragment.TAG);

            }
        });
    }

    @Override
    public void onLocationItemChosen(PublicLocation location) {
        Intent intent = new Intent(SavePublicLocationActivity.this, LocationActivity.class);
        intent.putExtra("Location", location);
        startActivity(intent);
    }
}
