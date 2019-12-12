package hu.bme.aut.fitnessapp.controllers.gym;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.gym_models.NewPublicLocationModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class NewPublicLocationActivity extends InternetCheckActivity implements EquipmentAdapter.OnCheckBoxClicked, NewPublicLocationModel.ListLoaded{

    private EquipmentAdapter adapter;

    private EditText name;
    private EditText description;
    private EditText country;
    private EditText city;
    private EditText zip;
    private EditText address;

    private ArrayList<EditText> openHours;
    private ArrayList<CheckBox> checkBoxes;

    private NewPublicLocationModel publicLocationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_public_location);

        setToolbar();

        initializeEditTexts();
        initializeCheckBoxes();
    }

    @Override
    public void onStart() {
        super.onStart();
        publicLocationModel = new NewPublicLocationModel(this);
        publicLocationModel.loadEquipment();

        setDatePickers();
        setCheckBoxes();

        setFloatingActionButton();
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_check);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextsValid()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.gym_details_missing, Toast.LENGTH_LONG);
                    toast.show();
                } else if (getLocationItem().getEquipment().isEmpty()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.no_equipment_selected, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (!publicLocationModel.openCloseTimesValid(getLocationItem().getOpenHours()) || !checkBoxCheckedValid()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.gym_time_error, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (!publicLocationModel.openCloseTimesDiffValid(getLocationItem().getOpenHours())) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.gym_time_diff_error, Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(NewPublicLocationActivity.this, GymMainActivity.class);
                    intent.putExtra("new", getLocationItem());
                    startActivity(intent);
                }
            }
        });
    }

    public void initRecyclerView(List<Equipment> equipmentList) {
        RecyclerView recyclerView = findViewById(R.id.EquipmentRecyclerView);
        adapter = new EquipmentAdapter(this, equipmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onChecked(int pos) {
        adapter.onChecked(pos);
    }

    @Override
    public void onUnchecked(int pos) {
        adapter.onUnchecked(pos);
    }

    public void initializeEditTexts() {
        openHours = new ArrayList<>();
        EditText monStart = findViewById(R.id.MonStart);
        EditText monEnd = findViewById(R.id.MonEnd);
        EditText tuesStart = findViewById(R.id.TuesStart);
        EditText tuesEnd = findViewById(R.id.TuesEnd);
        EditText wedStart = findViewById(R.id.WedStart);
        EditText wedEnd = findViewById(R.id.WedEnd);
        EditText thursStart = findViewById(R.id.ThursStart);
        EditText thursEnd = findViewById(R.id.ThursEnd);
        EditText friStart = findViewById(R.id.FriStart);
        EditText friEnd = findViewById(R.id.FriEnd);
        EditText satStart = findViewById(R.id.SatStart);
        EditText satEnd = findViewById(R.id.SatEnd);
        EditText sunStart = findViewById(R.id.SunStart);
        EditText sunEnd = findViewById(R.id.SunEnd);

        openHours.add(monStart);
        openHours.add(monEnd);
        openHours.add(tuesStart);
        openHours.add(tuesEnd);
        openHours.add(wedStart);
        openHours.add(wedEnd);
        openHours.add(thursStart);
        openHours.add(thursEnd);
        openHours.add(friStart);
        openHours.add(friEnd);
        openHours.add(satStart);
        openHours.add(satEnd);
        openHours.add(sunStart);
        openHours.add(sunEnd);

        for(EditText editText : openHours) {
            editText.setEnabled(false);
        }

        name = findViewById(R.id.LocationNameEditText);
        description = findViewById(R.id.LocationDescriptionEditText);
        country = findViewById(R.id.LocationCountryEditText);
        city = findViewById(R.id.LocationCityEditText);
        zip = findViewById(R.id.LocationZipEditText);
        address = findViewById(R.id.LocationAddressEditText);
    }

    public void initializeCheckBoxes() {
        checkBoxes = new ArrayList<>();

        CheckBox monCheckBox = findViewById(R.id.monCheckBox);
        CheckBox tuesCheckBox = findViewById(R.id.tuesCheckBox);
        CheckBox wedCheckBox = findViewById(R.id.wedCheckBox);
        CheckBox thursCheckBox = findViewById(R.id.thursCheckBox);
        CheckBox friCheckBox = findViewById(R.id.friCheckBox);
        CheckBox satCheckBox = findViewById(R.id.satCheckBox);
        CheckBox sunCheckBox = findViewById(R.id.sunCheckBox);

        checkBoxes.add(monCheckBox);
        checkBoxes.add(tuesCheckBox);
        checkBoxes.add(wedCheckBox);
        checkBoxes.add(thursCheckBox);
        checkBoxes.add(friCheckBox);
        checkBoxes.add(satCheckBox);
        checkBoxes.add(sunCheckBox);

    }

    public void setDatePickers() {
        for(final EditText ET : openHours) {
            ET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(NewPublicLocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            String time = publicLocationModel.setTime(hourOfDay, minutes);
                            ET.setText(time);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();

                }
            });
        }
}

    public void setCheckBoxes() {
        for(final CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int idx = checkBoxes.indexOf(checkBox);
                    if(isChecked) {
                        openHours.get(idx * 2).setEnabled(true);
                        openHours.get(idx * 2 + 1).setEnabled(true);
                    }
                    else {
                        openHours.get(idx * 2).setText("");
                        openHours.get(idx * 2).setEnabled(false);
                        openHours.get(idx * 2 + 1).setText("");
                        openHours.get(idx * 2 + 1).setEnabled(false);
                    }
                }

            });
            checkBox.setChecked(false);
        }
}

    private boolean editTextsValid() {
        return name.getText().length() > 0 && description.getText().length() > 0 && country.getText().length() > 0 &&
                city.getText().length() > 0 && address.getText().length() > 0 && zip.getText().length() > 0;
    }

    private boolean checkBoxCheckedValid() {
        for(int i = 0; i < getLocationItem().getOpenHours().size(); i++) {
            String[] day = getLocationItem().getOpenHours().get(i);

            if(day[0].equals("") && day[1].equals("") && checkBoxes.get(i).isChecked()) {
                return false;
            }
        }
        return true;
    }

    public PublicLocation getLocationItem() {
        ArrayList<String[]> openClose = new ArrayList<>();
        for(int i = 0; i < openHours.size(); i = i + 2){
            String[] hours = new String[2];
            hours[0] = openHours.get(i).getText().toString();
            hours[1] = openHours.get(i+1).getText().toString();
            openClose.add(hours);
        }

        return new PublicLocation(Calendar.getInstance().getTimeInMillis(), name.getText().toString(), adapter.getCheckedEquipmentList(), openClose, description.getText().toString(),
                zip.getText().toString(), country.getText().toString(), city.getText().toString(), address.getText().toString(), "");
    }


    public EquipmentAdapter getAdapter() {
        return adapter;
    }

    public EditText getName() {
        return name;
    }

    public EditText getDescription() {
        return description;
    }

    public EditText getCountry() {
        return country;
    }

    public EditText getCity() {
        return city;
    }

    public EditText getZip() {
        return zip;
    }

    public EditText getAddress() {
        return address;
    }

    public List<EditText> getOpenHours() {
        return openHours;
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setAdapter(EquipmentAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onListLoaded(ArrayList<Equipment> equipment) {
        initRecyclerView(equipment);
    }

    @Override
    public void onStop() {
        super.onStop();
        publicLocationModel.removeListeners();
    }
}
