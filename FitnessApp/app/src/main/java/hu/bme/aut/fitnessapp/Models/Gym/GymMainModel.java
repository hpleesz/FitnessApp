package hu.bme.aut.fitnessapp.Models.Gym;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadProfile;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUserPublicLocations;

public class GymMainModel implements LoadPublicLocations.PublicLocationsByCreatorLoadedListener{
    private DatabaseReference databaseReference;
    private String userId;

    private ArrayList<PublicLocation> itemlist;
    private PublicLocation publicLocation;

    private Context activity;

    private GymMainModel.ListLoaded listener;


    public interface ListLoaded {
        void onListLoaded(ArrayList<PublicLocation> locations);
    }


    public GymMainModel(Context activity, PublicLocation publicLocation) {
        listener = (GymMainModel.ListLoaded)activity;

        this.activity = activity;
        this.publicLocation = publicLocation;
        if(publicLocation != null) {
            addNewItem();
        }

        //loadList();

    }

    public void initFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Public_Locations");
    }

    public void loadList() {
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByCreatorListener(this);
        loadPublicLocations.loadPublicLocationsByCreator();
    }

    @Override
    public void onPublicLocationsByCreatorLoaded(ArrayList<PublicLocation> publicLocations) {
        itemlist = publicLocations;
        listener.onListLoaded(itemlist);
    }
    /*
    private void loadList() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
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

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    itemlist.add(location);

                }

                listener.onListLoaded(itemlist);
                //((GymMainActivity)activity).initRecyclerView(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.orderByChild("Creator").equalTo(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

     */


    public void deleteItem(final PublicLocation item) {
        databaseReference.child(Long.toString(item.id)).removeValue();
        LoadUserPublicLocations loadUserPublicLocations = new LoadUserPublicLocations();
        loadUserPublicLocations.removeUserPublicLocation(Long.toString(item.id));
        /*
        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("User_Public_Locations");

        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    String user = dataSnapshot1.getKey();
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        String val = (String) dataSnapshot2.getValue();
                        String id = Long.toString(item.id);

                        if(val.equals(id)){
                            String key = dataSnapshot2.getKey();
                            databaseReferenceUser.child(user).child(key).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        });

         */

    }


    public void addNewItem() {
        databaseReference.child(Long.toString(publicLocation.id)).child("Name").setValue(publicLocation.name);
        databaseReference.child(Long.toString(publicLocation.id)).child("Description").setValue(publicLocation.description);
        databaseReference.child(Long.toString(publicLocation.id)).child("Country").setValue(publicLocation.country);
        databaseReference.child(Long.toString(publicLocation.id)).child("City").setValue(publicLocation.city);
        databaseReference.child(Long.toString(publicLocation.id)).child("Address").setValue(publicLocation.address);
        databaseReference.child(Long.toString(publicLocation.id)).child("Zip").setValue(publicLocation.zip);
        databaseReference.child(Long.toString(publicLocation.id)).child("Creator").setValue(userId);

        for(int i = 0; i < publicLocation.equipment.size(); i++) {
            databaseReference.child(Long.toString(publicLocation.id)).child("Equipment").child(Integer.toString(i)).setValue(publicLocation.equipment.get(i));
        }

        for(int i = 0; i < publicLocation.open_hours.size(); i++) {
            databaseReference.child(Long.toString(publicLocation.id)).child("Open_Hours").child(Integer.toString(i)).child("0").setValue(publicLocation.open_hours.get(i)[0]);
            databaseReference.child(Long.toString(publicLocation.id)).child("Open_Hours").child(Integer.toString(i)).child("1").setValue(publicLocation.open_hours.get(i)[1]);

        }



    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

}


