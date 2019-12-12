package hu.bme.aut.fitnessapp.controllers.gym;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.models.gym_models.EditPublicLocationModel;

public class EditPublicLocationActivity extends NewPublicLocationActivity {

    private EditPublicLocationModel publicLocationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("edit");
    }

    private PublicLocation publicLocation;

    @Override
    public void onStart() {
        super.onStart();

        publicLocationModel = new EditPublicLocationModel(this, publicLocation);
        publicLocationModel.loadEquipment();
        setDetails();
    }

    @Override
    public void initRecyclerView(List<Equipment> equipment) {
        super.initRecyclerView(equipment);
        getAdapter().setCheckedEquipmentList(publicLocationModel.getPublicLocation().getEquipment());
    }

    public void setDetails() {

        getName().setText(publicLocationModel.getPublicLocation().getName());
        getDescription().setText(publicLocationModel.getPublicLocation().getDescription());
        getCountry().setText(publicLocationModel.getPublicLocation().getCountry());
        getCity().setText(publicLocationModel.getPublicLocation().getCity());
        getZip().setText(publicLocationModel.getPublicLocation().getZip());
        getAddress().setText(publicLocationModel.getPublicLocation().getAddress());

        int j = 0;
        for(int i = 0; i < publicLocationModel.getPublicLocation().getOpenHours().size(); i++) {
            if(publicLocationModel.getPublicLocation().getOpenHours().get(i)[0].equals("") && publicLocationModel.getPublicLocation().getOpenHours().get(i)[1].equals("")) {
                getCheckBoxes().get(i).setChecked(false);
                getOpenHours().get(j).setEnabled(false);
                j++;
                getOpenHours().get(j).setEnabled(false);
                j++;
            }
            else {
                getCheckBoxes().get(i).setChecked(true);
                getOpenHours().get(j).setText(publicLocationModel.getPublicLocation().getOpenHours().get(i)[0]);
                getOpenHours().get(j).setEnabled(true);
                j++;
                getOpenHours().get(j).setText(publicLocationModel.getPublicLocation().getOpenHours().get(i)[1]);
                getOpenHours().get(j).setEnabled(true);
                j++;
            }
        }

    }

    @Override
    public PublicLocation getLocationItem() {
        ArrayList<String[]> openClose = new ArrayList<>();
        for(int i = 0; i < getOpenHours().size(); i = i + 2){
            String[] hours = new String[2];
            hours[0] = getOpenHours().get(i).getText().toString();
            hours[1] = getOpenHours().get(i+1).getText().toString();
            openClose.add(hours);
        }

        return new PublicLocation(publicLocationModel.getPublicLocation().getId(),
                getName().getText().toString(), getAdapter().getCheckedEquipmentList(), openClose,
                getDescription().getText().toString(), getZip().getText().toString(),
                getCountry().getText().toString(), getCity().getText().toString(),
                getAddress().getText().toString(), "");
    }

    @Override
    public void onStop() {
        super.onStop();
        publicLocationModel.removeListeners();
    }
}


