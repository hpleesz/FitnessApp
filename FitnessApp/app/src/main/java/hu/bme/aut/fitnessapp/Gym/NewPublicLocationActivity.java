package hu.bme.aut.fitnessapp.Gym;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.PublicLocation;

public class NewPublicLocationActivity extends AppCompatActivity implements EquipmentAdapter.OnCheckBoxClicked{

    private DatabaseReference databaseReference;

    private ArrayList<Equipment> equipmentList;

    private EquipmentAdapter adapter;

    private EditText name;
    private EditText description;
    private EditText country;
    private EditText city;
    private EditText zip;
    private EditText address;

    private ArrayList<EditText> openHours;
    private ArrayList<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_public_location);

        setToolbar();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializeEditTexts();
        initializeCheckBoxes();

        setDatePickers();
        setCheckBoxes();

        setFloatingActionButton();
        loadEquipment();
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
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), "Please fill name, description and address out", Toast.LENGTH_LONG);
                    toast.show();
                } else if (getLocationItem().equipment.isEmpty()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.no_equipment_selected, Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (!openCloseTimesValid() || !checkBoxCheckedValid()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), "If gym is closed uncheck checkbox, otherwise fill out both times", Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (!openCloseTimesDiffValid()) {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), "Close time must be later than open time", Toast.LENGTH_LONG);
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

    private void loadEquipment() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipmentList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = (String) dataSnapshot1.getValue();
                    Equipment equipment = new Equipment(id, name);
                    equipmentList.add(equipment);
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Equipment").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    public void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.EquipmentRecyclerView);
        adapter = new EquipmentAdapter(this, equipmentList);
        //loadItemsInBackground();
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
                            String hour = Integer.toString(hourOfDay);
                            String min = Integer.toString(minutes);

                            if(hourOfDay < 10) hour = "0" + hour;
                            if(minutes < 10) min = "0" + min;
                            ET.setText(hour + ":" + min);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();

                }
            });
        }
}

public void setCheckBoxes() {
        for(final CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(true);
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
        }
}
    private boolean editTextsValid() {
        return name.getText().length() > 0 && description.getText().length() > 0 && country.getText().length() > 0 &&
                city.getText().length() > 0 && address.getText().length() > 0 && zip.getText().length() > 0;
    }

    private boolean openCloseTimesValid() {
        for(String[] day : getLocationItem().open_hours) {
            if((day[0].equals("") && !day[1].equals("")) || (!day[0].equals("") && day[1].equals(""))) {
                return false;
            }
        }
        return true;
    }

    private boolean openCloseTimesDiffValid() {
        for(String[] day : getLocationItem().open_hours) {
            if(!day[0].equals("")) {
                String open = day[0].replace(":", "");
                String close = day[1].replace(":", "");
                open = open.replaceAll("^0+", "");
                close = close.replaceAll("^0+", "");

                int open_num = 0;
                int close_num = 0;
                if(!open.equals("")) open_num = Integer.parseInt(open);
                if(!close.equals("")) close_num = Integer.parseInt(close);

                int diff = close_num - open_num;
                if(diff <= 0) return false;
            }
        }
        return true;
    }

    private boolean checkBoxCheckedValid() {
        for(String[] day : getLocationItem().open_hours) {
            if(day[0].equals("") && day[1].equals("") && checkBoxes.get(getLocationItem().open_hours.indexOf(day)).isChecked()) {
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
        PublicLocation location = new PublicLocation(Calendar.getInstance().getTimeInMillis(), name.getText().toString(), adapter.getCheckedEquipmentList(), openClose, description.getText().toString(),
                zip.getText().toString(), country.getText().toString(), city.getText().toString(), address.getText().toString(), "");

        return location;
    }

    public ArrayList<Equipment> getEquipmentList() {
        return equipmentList;
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

    public ArrayList<EditText> getOpenHours() {
        return openHours;
    }

    public ArrayList<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setAdapter(EquipmentAdapter adapter) {
        this.adapter = adapter;
    }

}