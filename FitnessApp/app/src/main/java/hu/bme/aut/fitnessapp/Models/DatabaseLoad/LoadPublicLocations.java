package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.UserPublicLocation;

public class LoadPublicLocations extends DatabaseConnection {

    public interface PublicLocationsLoadedListener {
        void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations);
    }

    public interface PublicLocationsByCreatorLoadedListener {
        void onPublicLocationsByCreatorLoaded(ArrayList<PublicLocation> publicLocations);
    }

    public interface PublicLocationsByIDLoadedListener {
        void onPublicLocationsByIDLoaded(PublicLocation publicLocations);
    }

    private LoadPublicLocations.PublicLocationsLoadedListener listLoadedListener;
    private LoadPublicLocations.PublicLocationsByCreatorLoadedListener listLoadedByCreatorListener;
    private LoadPublicLocations.PublicLocationsByIDLoadedListener listLoadedByIDListener;

    public LoadPublicLocations() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadPublicLocations.PublicLocationsLoadedListener)object;
    }

    public void setListLoadedByCreatorListener(Object object) {
        listLoadedByCreatorListener = (LoadPublicLocations.PublicLocationsByCreatorLoadedListener)object;
    }

    public void setListLoadedByIDListener(Object object) {
        listLoadedByIDListener = (LoadPublicLocations.PublicLocationsByIDLoadedListener)object;
    }


    public  void loadPublicLocations() {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<PublicLocation> itemList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        long id = Long.parseLong(dataSnapshot1.getKey());
                        String name = dataSnapshot1.child("Name").getValue(String.class);
                        String description = dataSnapshot1.child("Description").getValue(String.class);
                        String zip = dataSnapshot1.child("Zip").getValue(String.class);
                        String country = dataSnapshot1.child("Country").getValue(String.class);
                        String city = dataSnapshot1.child("City").getValue(String.class);
                        String address = dataSnapshot1.child("Address").getValue(String.class);

                        ArrayList<Integer> equipment = new ArrayList<>();

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Equipment").getChildren()) {
                            int idx = dataSnapshot2.getValue(Integer.class);
                            equipment.add(idx);
                        }

                        ArrayList<String[]> hours = new ArrayList<>();

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Open_Hours").getChildren()) {
                            String[] open_close = new String[2];

                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                int idx = Integer.parseInt(dataSnapshot3.getKey());
                                String hour = dataSnapshot3.getValue(String.class);

                                open_close[idx] = hour;
                            }
                            hours.add(open_close);
                        }

                        PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, getUserId());

                        itemList.add(location);

                    }

                    listLoadedListener.onPublicLocationsLoaded(itemList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }

            };
            getDatabaseReference().child("Public_Locations").addValueEventListener(eventListener);
    }

    public void loadPublicLocationsByCreator() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<PublicLocation> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    long id = Long.parseLong(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    String description = dataSnapshot1.child("Description").getValue(String.class);
                    String zip = dataSnapshot1.child("Zip").getValue(String.class);
                    String country = dataSnapshot1.child("Country").getValue(String.class);
                    String city = dataSnapshot1.child("City").getValue(String.class);
                    String address = dataSnapshot1.child("Address").getValue(String.class);

                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    ArrayList<String[]> hours = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Open_Hours").getChildren()) {
                        String[] open_close = new String[2];

                        for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            open_close[idx] = hour;
                        }
                        hours.add(open_close);
                    }

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, getUserId());

                    itemlist.add(location);

                }

                listLoadedByCreatorListener.onPublicLocationsByCreatorLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Public_Locations").orderByChild("Creator").equalTo(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void loadPublicLocationByID(String id) {
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
                String userID = dataSnapshot.child("Creator").getValue(String.class);

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

                PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userID);

                listLoadedByIDListener.onPublicLocationsByIDLoaded(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Public_Locations").child(id).addValueEventListener(eventListener);


    }

}
