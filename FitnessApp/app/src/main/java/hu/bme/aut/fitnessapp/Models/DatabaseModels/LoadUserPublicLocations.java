package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.UserPublicLocation;

public class LoadUserPublicLocations extends DatabaseConnection{

    public interface UserPublicLocationsLoadedListener {
        void onUserPublicLocationsLoaded(ArrayList<UserPublicLocation> userPublicLocations);
    }

    private LoadUserPublicLocations.UserPublicLocationsLoadedListener listLoadedListener;

    public LoadUserPublicLocations() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadUserPublicLocations.UserPublicLocationsLoadedListener)object;

    }

    public void loadUserPublicLocations() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserPublicLocation> publicIDs = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    UserPublicLocation loc = new UserPublicLocation();
                    loc.gym_id = dataSnapshot1.getValue(String.class);
                    loc.id = Integer.parseInt(dataSnapshot1.getKey());
                    publicIDs.add(loc);
                }

                listLoadedListener.onUserPublicLocationsLoaded(publicIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("User_Public_Locations").child(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void removeUserPublicLocation(final String id) {
        getDatabaseReference().child("User_Public_Locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    String user = dataSnapshot1.getKey();
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        String val = (String) dataSnapshot2.getValue();

                        if(val.equals(id)){
                            String key = dataSnapshot2.getKey();
                            getDatabaseReference().child(user).child(key).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        });
    }

    public void removeItem(String idx) {
        getDatabaseReference().child("User_Public_Locations").child(getUserId()).child(idx).removeValue();
    }

    public void addNewItem(int id, PublicLocation publicLocation) {
        getDatabaseReference().child("User_Public_Locations").child(getUserId()).child(Integer.toString(id)).setValue(Long.toString(publicLocation.id));

    }

}
