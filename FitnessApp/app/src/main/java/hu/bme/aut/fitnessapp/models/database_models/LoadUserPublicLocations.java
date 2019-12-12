package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.entities.UserPublicLocation;

public class LoadUserPublicLocations extends DatabaseConnection{

    private static final String USER_PUBLIC_LOCATIONS = "User_Public_Locations";

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

    private ValueEventListener eventListener;

    public void loadUserPublicLocations() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserPublicLocation> publicIDs = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    UserPublicLocation loc = new UserPublicLocation();
                    loc.setGymId(dataSnapshot1.getValue(String.class));
                    loc.setId(Integer.parseInt(dataSnapshot1.getKey()));
                    publicIDs.add(loc);
                }

                listLoadedListener.onUserPublicLocationsLoaded(publicIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(USER_PUBLIC_LOCATIONS).child(getUserId()).addValueEventListener(eventListener);
        
    }

    public void removeUserPublicLocation(final String id) {
        getDatabaseReference().child(USER_PUBLIC_LOCATIONS).addListenerForSingleValueEvent(new ValueEventListener() {
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
                logger.error(DB_ERROR, databaseError.toException());
            }

        });
    }

    public void removeItem(String idx) {
        getDatabaseReference().child(USER_PUBLIC_LOCATIONS).child(getUserId()).child(idx).removeValue();
    }

    public void addNewItem(int id, PublicLocation publicLocation) {
        getDatabaseReference().child(USER_PUBLIC_LOCATIONS).child(getUserId()).child(Integer.toString(id)).setValue(Long.toString(publicLocation.getId()));

    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child(USER_PUBLIC_LOCATIONS).child(getUserId()).removeEventListener(eventListener);
    }

}
