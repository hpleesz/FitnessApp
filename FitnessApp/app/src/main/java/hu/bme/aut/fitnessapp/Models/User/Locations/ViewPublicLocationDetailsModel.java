package hu.bme.aut.fitnessapp.Models.User.Locations;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;

public class ViewPublicLocationDetailsModel implements LoadEquipment.EquipmentLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{


    private PublicLocation publicLocation;
    private ArrayList<Equipment> equipmentList;
    private DatabaseReference databaseReference;

    private Context activity;

    public interface DisplayReadyListener {
        void onTitleReady(String title);
        void onDetailsReady(String desc, String address, String equipment, ArrayList<String> hours);
    }

    private ViewPublicLocationDetailsModel.DisplayReadyListener listener;

    public ViewPublicLocationDetailsModel(Context activity, PublicLocation publicLocation) {
        listener = (ViewPublicLocationDetailsModel.DisplayReadyListener)activity;
        this.publicLocation = publicLocation;
        this.activity = activity;

        //loadEquipment();
    }

    public void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();

       /*

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
                loadLocation();
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
*/
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
       equipmentList = equipment;
       loadPublicLocation();
    }


    private void loadPublicLocation() {
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(publicLocation.id));

       /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue(String.class);
                listener.onTitleReady(name);
                //((ViewPublicLocationDetailsActivity)activity).setTitle(name);

                String description = dataSnapshot.child("Description").getValue(String.class);

                String zip = dataSnapshot.child("Zip").getValue(String.class);
                String country = dataSnapshot.child("Country").getValue(String.class);
                String city = dataSnapshot.child("City").getValue(String.class);
                String address = dataSnapshot.child("Address").getValue(String.class);

                String full_address = country + "\n" + zip + ", " + city + "\n" + address;

                String creator = dataSnapshot.child("Creator").getValue(String.class);

                ArrayList<Integer> equipment = new ArrayList<>();

                String equipment_text = "";
                for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Equipment").getChildren()) {
                    int idx = dataSnapshot2.getValue(Integer.class);
                    equipment.add(idx);
                }

                if(equipment.contains(4) && equipment.contains(5)) equipment.remove(Integer.valueOf(4));
                if(equipment.contains(6) && equipment.contains(7)) equipment.remove(Integer.valueOf(6));
                if(equipment.size() == 1) {
                    equipment_text = equipmentList.get(equipment.get(0)-1).name;
                }
                else {
                    for (int equ : equipment) {
                        if(equ != 1) {
                            if (equipment_text.equals(""))
                                equipment_text = equipmentList.get(equ - 1).name;
                            else
                                equipment_text = equipment_text + "\n" + equipmentList.get(equ - 1).name;
                        }
                    }
                }

                ArrayList<String[]> hours = new ArrayList<>();

                for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Open_Hours").getChildren()) {
                    String[] open_close = new String[2];

                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                        int idx = Integer.parseInt(dataSnapshot3.getKey());
                        String hour = dataSnapshot3.getValue(String.class);

                        open_close[idx] = hour;
                    }
                    hours.add(open_close);
                }

                ArrayList<String> hour_text = new ArrayList<>();
                for(int i = 0; i < hours.size(); i ++) {
                    if(hours.get(i)[0].equals("")) {
                        hour_text.add("Closed");
                        //textViews.get(i).setText("Closed");
                    }
                    else {
                        String text = hours.get(i)[0] + " - " + hours.get(i)[1];
                        hour_text.add(text);
                        //textViews.get(i).setText(text);
                    }
                }

                listener.onDetailsReady(description, full_address, equipment_text, hour_text);
                //((ViewPublicLocationDetailsActivity)activity).setTextViews(description, full_address, equipment_text, hour_text);

                publicLocation = new PublicLocation(publicLocation.id, name, equipment, hours, description, zip, country, city, address, creator);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Public_Locations").child(Long.toString(publicLocation.id)).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

        */
    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation location) {
        publicLocation = location;
        listener.onTitleReady(publicLocation.name);
        String full_address = publicLocation.country + "\n" + publicLocation.zip + ", " + publicLocation.city + "\n" + publicLocation.address;

        ArrayList<Integer> equipment = publicLocation.equipment;
        String equipment_text = "";
        if(equipment.contains(4) && equipment.contains(5)) equipment.remove(Integer.valueOf(4));
        if(equipment.contains(6) && equipment.contains(7)) equipment.remove(Integer.valueOf(6));
        if(equipment.size() == 1) {
            equipment_text = equipmentList.get(equipment.get(0)-1).name;
        }
        else {
            for (int equ : equipment) {
                if(equ != 1) {
                    if (equipment_text.equals(""))
                        equipment_text = equipmentList.get(equ - 1).name;
                    else
                        equipment_text = equipment_text + "\n" + equipmentList.get(equ - 1).name;
                }
            }
        }

        ArrayList<String> hour_text = new ArrayList<>();
        for(int i = 0; i < publicLocation.open_hours.size(); i ++) {
            if(publicLocation.open_hours.get(i)[0].equals("")) {
                hour_text.add("Closed");
                //textViews.get(i).setText("Closed");
            }
            else {
                String text = publicLocation.open_hours.get(i)[0] + " - " + publicLocation.open_hours.get(i)[1];
                hour_text.add(text);
                //textViews.get(i).setText(text);
            }
        }

        listener.onDetailsReady(publicLocation.description, full_address, equipment_text, hour_text);
        //((ViewPublicLocationDetailsActivity)activity).setTextViews(description, full_address, equipment_text, hour_text);

        //publicLocation = new PublicLocation(publicLocation.id, name, equipment, hours, description, zip, country, city, address, creator);


    }


    public PublicLocation getPublicLocation() {
        return publicLocation;
    }

}
