package hu.bme.aut.fitnessapp.Controllers.Gym;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.Gym.EditPublicLocationModel;

public class EditPublicLocationActivity extends NewPublicLocationActivity {

    private PublicLocation publicLocation;
    private EditPublicLocationModel publicLocationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        PublicLocation publicLocation = (PublicLocation) i.getSerializableExtra("edit");
        publicLocationModel = new EditPublicLocationModel(this, publicLocation);
        publicLocationModel.loadEquipment();
        setData();
    }

    @Override
    public void initRecyclerView(ArrayList<Equipment> equipment) {
        super.initRecyclerView(equipment);
        getAdapter().setCheckedEquipmentList(publicLocationModel.getPublicLocation().equipment);
    }

    public void setData() {

        getName().setText(publicLocationModel.getPublicLocation().name);
        getDescription().setText(publicLocationModel.getPublicLocation().description);
        getCountry().setText(publicLocationModel.getPublicLocation().country);
        getCity().setText(publicLocationModel.getPublicLocation().city);
        getZip().setText(publicLocationModel.getPublicLocation().zip);
        getAddress().setText(publicLocationModel.getPublicLocation().address);

        int j = 0;
        for(int i = 0; i < publicLocationModel.getPublicLocation().open_hours.size(); i++) {
            if(publicLocationModel.getPublicLocation().open_hours.get(i)[0].equals("") && publicLocationModel.getPublicLocation().open_hours.get(i)[1].equals("")) {
                getCheckBoxes().get(i).setChecked(false);
                getOpenHours().get(j).setEnabled(false);
                j++;
                getOpenHours().get(j).setEnabled(false);
                j++;
            }
            else {
                getCheckBoxes().get(i).setChecked(true);
                getOpenHours().get(j).setText(publicLocationModel.getPublicLocation().open_hours.get(i)[0]);
                j++;
                getOpenHours().get(j).setText(publicLocationModel.getPublicLocation().open_hours.get(i)[1]);
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
        PublicLocation location = new PublicLocation(publicLocationModel.getPublicLocation().id,
                getName().getText().toString(), getAdapter().getCheckedEquipmentList(), openClose,
                getDescription().getText().toString(), getZip().getText().toString(),
                getCountry().getText().toString(), getCity().getText().toString(),
                getAddress().getText().toString(), "");

        return location;
    }
}
