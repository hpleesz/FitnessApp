package hu.bme.aut.fitnessapp.Models.User.Locations;

//import android.support.v4.app.DialogFragment;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;

public class PublicSearchMatchModel implements LoadPublicLocations.PublicLocationsLoadedListener{

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private PublicLocation publicLocation;

    private ArrayList<Boolean> openDays;

    private ArrayList<PublicLocation> itemList;

    public ArrayList<PublicLocation> getMatchList() {
        return matchList;
    }

    private ArrayList<PublicLocation> matchList;

    public interface ListLoadedListener {
        void onListLoaded();
    }

    public interface NoMatchListener {
        void onNoMatchFound();
    }

    private PublicSearchMatchModel.ListLoadedListener listLoadedListener;
    private PublicSearchMatchModel.NoMatchListener noMatchListener;

    public PublicSearchMatchModel(DialogFragment fragment, PublicLocation publicLocation, ArrayList<Boolean> openDays) {
        listLoadedListener = (PublicSearchMatchModel.ListLoadedListener)fragment;
        noMatchListener = (PublicSearchMatchModel.NoMatchListener) fragment;
        this.publicLocation = publicLocation;
        this.openDays = openDays;


        //loadList();
    }

    public void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadList() {
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedListener(this);
        loadPublicLocations.loadPublicLocations();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList = new ArrayList<>();
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

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    itemList.add(location);

                }

                findMatch();
                listLoadedListener.onListLoaded();
                //initRecyclerView(contentview);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Public_Locations").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }

    @Override
    public void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations) {
        itemList = publicLocations;
        findMatch();
        listLoadedListener.onListLoaded();

    }

    public void findMatch() {
        matchList = new ArrayList<>();

        ArrayList<Integer[]> times = convertTimesToInt();


        for (PublicLocation loc : itemList) {

            boolean match = true;

            if (!locationDetailsMatch(loc)) {
                continue;
            }

            if(!compareTimes(times, loc)) {
                continue;
            }

            for(Integer item : publicLocation.equipment) {
                if(!loc.equipment.contains(item)) {
                    match = false;
                    break;
                }

            }
            if(match) {
                matchList.add(loc);
            }
        }

        if(matchList.isEmpty()) {
            noMatchListener.onNoMatchFound();
        }
    }

    public ArrayList<Integer[]> convertTimesToInt() {
        ArrayList<Integer[]> times = new ArrayList<>();
        for (int i = 0; i < publicLocation.open_hours.size(); i++) {
            Integer[] open_close = new Integer[2];

            for (int j = 0; j < 2; j++) {
                if (!publicLocation.open_hours.get(i)[j].equals("")) {
                    String time = publicLocation.open_hours.get(i)[j].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    if (time.equals("")) open_close[j] = 0;
                    else open_close[j] = Integer.parseInt(time);
                } else {
                    open_close[j] = -1;
                }
            }
            times.add(open_close);
        }
        return times;
    }


    public boolean locationDetailsMatch(PublicLocation loc) {
        return ((publicLocation.name.equals("") || loc.name.equals(publicLocation.name)) &&
                (publicLocation.description.equals("") || loc.description.equals(publicLocation.description)) &&
                (publicLocation.zip.equals("") || loc.zip.equals(publicLocation.zip)) &&
                (publicLocation.country.equals("") || loc.country.equals(publicLocation.country)) &&
                (publicLocation.city.equals("") || loc.city.equals(publicLocation.city)) &&
                (publicLocation.address.equals("") || loc.address.equals(publicLocation.address)));
    }

    public boolean compareTimes(ArrayList<Integer[]> times, PublicLocation loc) {
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i)[0] != -1) {

                String time = loc.open_hours.get(i)[0].replace(":", "");
                time = time.replaceAll("^0+", "");
                int loc_time;
                if (time.equals("")) loc_time = 0;
                else loc_time = Integer.parseInt(time);

                if (times.get(i)[0] < loc_time) {
                    return false;
                }
            }

            if (times.get(i)[1] != -1) {

                String time = loc.open_hours.get(i)[1].replace(":", "");
                time = time.replaceAll("^0+", "");
                int loc_time;
                if (time.equals("")) loc_time = 0;
                else loc_time = Integer.parseInt(time);

                if (times.get(i)[1] > loc_time) {
                    return false;
                }
            }
            if(openDays.get(i) && times.get(i)[0] == -1 && times.get(i)[1] == -1) {
                if(loc.open_hours.get(i)[0].equals("")) {
                    return false;
                }
            }
        }
        return true;
    }


    public void setPublicLocation(PublicLocation publicLocation) {
        this.publicLocation = publicLocation;
    }

    public void setOpenDays(ArrayList<Boolean> openDays) {
        this.openDays = openDays;
    }


}
