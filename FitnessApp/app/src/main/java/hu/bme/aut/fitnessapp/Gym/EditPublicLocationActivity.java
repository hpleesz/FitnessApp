package hu.bme.aut.fitnessapp.Gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Models.PublicLocation;

public class EditPublicLocationActivity extends NewPublicLocationActivity {

    private PublicLocation publicLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("edit");

        setData();
    }

    @Override
    public void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.EquipmentRecyclerView);
        setAdapter(new EquipmentAdapter(this, getEquipmentList()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(getAdapter());
        getAdapter().setCheckedEquipmentList(publicLocation.equipment);

    }

    public void setData() {

        getName().setText(publicLocation.name);
        getDescription().setText(publicLocation.description);
        getCountry().setText(publicLocation.country);
        getCity().setText(publicLocation.city);
        getZip().setText(publicLocation.zip);
        getAddress().setText(publicLocation.address);

        int j = 0;
        for(int i = 0; i < publicLocation.open_hours.size(); i++) {
            if(publicLocation.open_hours.get(i)[0].equals("") && publicLocation.open_hours.get(i)[1].equals("")) {
                getCheckBoxes().get(i).setChecked(false);
                getOpenHours().get(j).setEnabled(false);
                j++;
                getOpenHours().get(j).setEnabled(false);
                j++;
            }
            else {
                getOpenHours().get(j).setText(publicLocation.open_hours.get(i)[0]);
                j++;
                getOpenHours().get(j).setText(publicLocation.open_hours.get(i)[1]);
                j++;
            }
        }

    }
}