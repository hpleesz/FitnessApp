package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import hu.bme.aut.fitnessapp.Entities.Location;

public class LoadLocations extends DatabaseConnection{

    public interface LocationsLoadedListener {
        void onLocationsLoaded(ArrayList<Location> locations);
    }

    private LoadLocations.LocationsLoadedListener listLoadedListener;

    public LoadLocations(Object object) {
        super();
        listLoadedListener = (LoadLocations.LocationsLoadedListener)object;
    }

    public void loadLocations() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Location> itemlist = new ArrayList<>();
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

                    itemlist.add(location);


                }
                listLoadedListener.onLocationsLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Locations").child(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

}
