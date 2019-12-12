package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class LoadPublicLocations extends DatabaseConnection {

    private static final String PUBLIC_LOCATIONS = "Public_Locations";

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

    private ValueEventListener loadEventListener;
    private ValueEventListener loadByCreatorEventListener;
    private ValueEventListener loadByIdEventListener;

    private String id;

    public  void loadPublicLocations() {
            loadEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<PublicLocation> itemList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        long key = Long.parseLong(dataSnapshot1.getKey());
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
                            String[] openClose = new String[2];

                            for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                int idx = Integer.parseInt(dataSnapshot3.getKey());
                                String hour = dataSnapshot3.getValue(String.class);

                                openClose[idx] = hour;
                            }
                            hours.add(openClose);
                        }

                        PublicLocation location = new PublicLocation(key, name, equipment, hours, description, zip, country, city, address, getUserId());

                        itemList.add(location);

                    }

                    listLoadedListener.onPublicLocationsLoaded(itemList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.error(DB_ERROR, databaseError.toException());
                }

            };
            getDatabaseReference().child(PUBLIC_LOCATIONS).addValueEventListener(loadEventListener);
    }

    public void loadPublicLocationsByCreator() {
        loadByCreatorEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<PublicLocation> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    long key = Long.parseLong(dataSnapshot1.getKey());
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
                        String[] openClose = new String[2];

                        for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            openClose[idx] = hour;
                        }
                        hours.add(openClose);
                    }

                    PublicLocation location = new PublicLocation(key, name, equipment, hours, description, zip, country, city, address, getUserId());

                    itemlist.add(location);

                }

                listLoadedByCreatorListener.onPublicLocationsByCreatorLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(PUBLIC_LOCATIONS).orderByChild("Creator").equalTo(getUserId()).addValueEventListener(loadByCreatorEventListener);

    }

    public void loadPublicLocationByID(String id) {
        this.id = id;
        loadByIdEventListener = new ValueEventListener() {
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
                    String[] openClose = new String[2];

                    for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                        int idx = Integer.parseInt(dataSnapshot3.getKey());
                        String hour = dataSnapshot3.getValue(String.class);

                        openClose[idx] = hour;
                    }
                    hours.add(openClose);
                }

                PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userID);

                listLoadedByIDListener.onPublicLocationsByIDLoaded(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(id).addValueEventListener(loadByIdEventListener);


    }

    public void addNewItem(PublicLocation publicLocation) {
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Name").setValue(publicLocation.getName());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Description").setValue(publicLocation.getDescription());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Country").setValue(publicLocation.getCountry());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("City").setValue(publicLocation.getCity());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Address").setValue(publicLocation.getAddress());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Zip").setValue(publicLocation.getZip());
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Creator").setValue(getUserId());

        for(int i = 0; i < publicLocation.getEquipment().size(); i++) {
            getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Equipment").child(Integer.toString(i)).setValue(publicLocation.getEquipment().get(i));
        }

        for(int i = 0; i < publicLocation.getOpenHours().size(); i++) {
            getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Open_Hours").child(Integer.toString(i)).child("0").setValue(publicLocation.getOpenHours().get(i)[0]);
            getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).child("Open_Hours").child(Integer.toString(i)).child("1").setValue(publicLocation.getOpenHours().get(i)[1]);

        }

    }

    public void removeItem(PublicLocation publicLocation) {
        getDatabaseReference().child(PUBLIC_LOCATIONS).child(Long.toString(publicLocation.getId())).removeValue();

    }

    public void removeListeners() {
        if(loadEventListener != null) getDatabaseReference().child(PUBLIC_LOCATIONS).orderByChild("Creator").equalTo(getUserId()).removeEventListener(loadEventListener);
        if(loadByCreatorEventListener != null) getDatabaseReference().child(PUBLIC_LOCATIONS).orderByChild("Creator").equalTo(getUserId()).removeEventListener(loadByCreatorEventListener);
        if(loadByIdEventListener != null) getDatabaseReference().child(PUBLIC_LOCATIONS).child(id).removeEventListener(loadByIdEventListener);

    }

}
