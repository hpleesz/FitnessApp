package hu.bme.aut.fitnessapp.Models.User.Locations;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.UserPublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUserPublicLocations;

public class LocationModel implements LoadLocations.LocationsLoadedListener, LoadUserPublicLocations.UserPublicLocationsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private DatabaseReference databaseReference;
    private String userId;

    private ArrayList<Location> itemlist;
    private ArrayList<PublicLocation> public_itemlist;

    private PublicLocation newPublicLocation;

    private ArrayList<UserPublicLocation> publicIDs;

    private Context activity;

    private LocationModel.LocationsLoaded listener;
    private LocationModel.PublicLocationsLoaded listener2;

    public LocationModel(Context activity, PublicLocation publicLocation) {
        listener = (LocationModel.LocationsLoaded)activity;
        listener2 = (LocationModel.PublicLocationsLoaded)activity;

        this.activity = activity;
        newPublicLocation = publicLocation;

        //loadLocations();
        //loadListPublic();

    }

    public void initFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public interface LocationsLoaded {
        void onLocationsLoaded(ArrayList<Location> locations);
    }

    public interface PublicLocationsLoaded {
        void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations);
    }

    public void loadLocations() {
        LoadLocations loadLocations = new LoadLocations(this);
        loadLocations.loadLocations();
    }

    @Override
    public void onLocationsLoaded(ArrayList<Location> locations) {
        itemlist = locations;
        listener.onLocationsLoaded(itemlist);
    }
    /*
    private void loadList() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
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
                listener.onListLoaded(itemlist);
                //((LocationActivity)activity).initRecyclerView(itemlist);
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
    }

     */

    public void loadListPublic() {
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
        public_itemlist = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            if(newPublicLocation != null) {
                addNewItem();
            }
            listener2.onPublicLocationsLoaded(public_itemlist);
            //((LocationActivity)activity).initRecyclerView2(public_itemlist);
        }
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        for(UserPublicLocation loc: publicIDs) {
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
                    for(int i = 0; i < public_itemlist.size(); i++) {
                        if(public_itemlist.get(i).id == location.id) {
                            idx = i;
                            break;
                        }
                    }
                    //update
                    if(idx > -1) {
                        public_itemlist.set(idx, location);
                    }
                    //add
                    else {
                        public_itemlist.add(location);
                    }

                    listener2.onListLoaded2(public_itemlist);
                    //((LocationActivity)activity).initRecyclerView2(public_itemlist);
                    if(newPublicLocation != null) {
                        addNewItem();
                    }
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
        for(int i = 0; i < public_itemlist.size(); i++) {
            if(public_itemlist.get(i).id == publicLocation.id) {
                idx = i;
                break;
            }
        }
        //update
        if(idx > -1) {
            public_itemlist.set(idx, publicLocation);
        }
        //add
        else {
            public_itemlist.add(publicLocation);
        }

        listener2.onPublicLocationsLoaded(public_itemlist);
        //((LocationActivity)activity).initRecyclerView2(public_itemlist);
        if(newPublicLocation != null) {
            addNewItem();
        }
    }

    public void createLocationItem(final Location newItem) {
        int id = 0;
        if(!itemlist.isEmpty()) id = itemlist.get(itemlist.size()-1).id +1;
        databaseReference.child("Locations").child(userId).child(Integer.toString(id)).child("Name").setValue(newItem.name);
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child("Locations").child(userId).child(Integer.toString(id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    public void updateLocationItem(final Location newItem) {
        databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Name").setValue(newItem.name);
        databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Equipment").removeValue();
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    public void deleteLocationItem(final Location item) {
        databaseReference.child("Locations").child(userId).child(Integer.toString(item.id)).removeValue();
    }

    public void deletePublicLocationItem(PublicLocation item) {
        String idx = "";
        for(UserPublicLocation loc : publicIDs) {
            if(Long.parseLong(loc.gym_id) == item.id) {
                idx = Long.toString(loc.id);
            }
        }
        databaseReference.child("User_Public_Locations").child(userId).child(idx).removeValue();
    }

    public void addNewItem() {
        PublicLocation publicLocation = newPublicLocation;
        newPublicLocation = null;

        for(UserPublicLocation loc : publicIDs) {
            if(Long.toString(publicLocation.id).equals(loc.gym_id)) return;
        }

        int id = 0;
        if(!publicIDs.isEmpty()) id = publicIDs.get(publicIDs.size()-1).id + 1;
        databaseReference.child("User_Public_Locations").child(userId).child(Integer.toString(id)).setValue(Long.toString(publicLocation.id));
    }
}
