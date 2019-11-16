package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;
import android.content.SharedPreferences;
//import android.support.v4.app.DialogFragment;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.UserPublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUserPublicLocations;

public class ChooseLocationItemModel implements LoadLocations.LocationsLoadedListener, LoadUserPublicLocations.UserPublicLocationsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private FirebaseAuth firebaseAuth;
    private String userId;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    private ArrayList<PublicLocation> itemList;

    public ArrayList<PublicLocation> getItemList() {
        return itemList;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    private ArrayList<Location> locations;
    private ArrayList<UserPublicLocation> publicIDs;

    private DialogFragment activity;

    private ChooseLocationItemModel.LocationsLoaded listener;
    private ChooseLocationItemModel.PublicLocationsLoaded listener2;


    public ChooseLocationItemModel(DialogFragment activity, Context context) {
        this.activity = activity;
        listener = (ChooseLocationItemModel.LocationsLoaded)activity;
        listener2 = (ChooseLocationItemModel.PublicLocationsLoaded)activity;

    }

    public interface LocationsLoaded {
        void onLocationsLoaded();
    }

    public interface PublicLocationsLoaded {
        void onPublicLocationsLoaded();
    }

    public void loadLocations() {
        LoadLocations loadLocations = new LoadLocations(this);
        loadLocations.loadLocations();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locations = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    Location location = new Location(id, name, equipment);

                    locations.add(location);


                }
                listener.onLocationsLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }
    @Override
    public void onLocationsLoaded(ArrayList<Location> locations) {
        this.locations = locations;
        listener.onLocationsLoaded();
    }

    public void loadPublicLocations() {
        LoadUserPublicLocations loadUserPublicLocations = new LoadUserPublicLocations();
        loadUserPublicLocations.setListLoadedListener(this);
        loadUserPublicLocations.loadUserPublicLocations();

        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publicIDs = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    UserPublicLocation loc = new UserPublicLocation();
                    loc.gym_id = dataSnapshot1.getValue(String.class);
                    loc.id = Integer.parseInt(dataSnapshot1.getKey());
                    publicIDs.add(loc);
                }

                loadGyms();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("User_Public_Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }

    @Override
    public void onUserPublicLocationsLoaded(ArrayList<UserPublicLocation> userPublicLocations) {
        publicIDs = userPublicLocations;
        loadGyms();
    }

    private  void loadGyms() {
        itemList = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            listener2.onPublicLocationsLoaded();
        }
        for(UserPublicLocation loc: publicIDs) {
            LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
            loadPublicLocations.setListLoadedByIDListener(this);
            loadPublicLocations.loadPublicLocationByID(loc.gym_id);
            /*
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long id = Long.parseLong(dataSnapshot.getKey());
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String description = dataSnapshot.child("Description").getValue(String.class);
                    String zip = dataSnapshot.child("Zip").getValue(String.class);
                    String country = dataSnapshot.child("Country").getValue(String.class);
                    String city = dataSnapshot.child("City").getValue(String.class);
                    String address = dataSnapshot.child("Address").getValue(String.class);

                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    ArrayList<String[]> hours = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot.child("Open_Hours").getChildren()) {
                        String[] open_close = new String[2];

                        for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            open_close[idx] = hour;
                        }
                        hours.add(open_close);
                    }

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    int idx = -1;
                    for(int i = 0; i < itemList.size(); i++) {
                        if(itemList.get(i).id == location.id) {
                            idx = i;
                            break;
                        }
                    }
                    if(idx > -1) {
                        itemList.set(idx, location);
                    }
                    else {
                        itemList.add(location);
                    }


                    listener2.onPublicLocationsLoaded();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }

            };
            databaseReference.child("Public_Locations").child(loc.gym_id).addValueEventListener(eventListener);


             */
        }

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        int idx = -1;
        for(int i = 0; i < itemList.size(); i++) {
            if(itemList.get(i).id == publicLocation.id) {
                idx = i;
                break;
            }
        }
        //update
        if(idx > -1) {
            itemList.set(idx, publicLocation);
        }
        //add
        else {
            itemList.add(publicLocation);
        }

        listener2.onPublicLocationsLoaded();

    }




}
