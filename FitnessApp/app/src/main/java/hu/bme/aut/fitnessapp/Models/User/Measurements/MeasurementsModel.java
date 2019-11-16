package hu.bme.aut.fitnessapp.Models.User.Measurements;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadBodyParts;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadMeasurements;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUser;

public class MeasurementsModel implements LoadUser.UserLoadedListener, LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.LastMeasurementsLoadedListener {

    private Map<String, Double> list;
    private ArrayList<String> body_parts;

    private DatabaseReference databaseReference;
    private String userId;

    private User user;
    private LayoutInflater inflater;

    private Context activity;


    public interface CurrentMeasurementsListener {
        void onMeasurementsLoaded(ArrayList<String> measurements);
    }

    public interface GenderListener {
        void onGenderLoaded(int gender);
    }

    private MeasurementsModel.CurrentMeasurementsListener measurementsListener;
    private MeasurementsModel.GenderListener genderListener;

    public MeasurementsModel(Context activity) {
        measurementsListener = (MeasurementsModel.CurrentMeasurementsListener)activity;
        genderListener = (MeasurementsModel.GenderListener)activity;
        this.activity = activity;

        //loadUser();


    }

    public void loadBodyPartsDatabase() {
        LoadBodyParts loadBodyParts = new LoadBodyParts(this);
        loadBodyParts.loadBodyParts();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String body_part = (String) dataSnapshot1.getValue();
                    body_parts.add(body_part);
                }
                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Body_Parts").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }


    @Override
    public void onBodyPartsLoaded(ArrayList<String> bodyParts) {
        this.body_parts = bodyParts;
        loadMeasurements();
    }

    public void loadMeasurements() {
        LoadMeasurements loadMeasurements = new LoadMeasurements();
        loadMeasurements.setLastMeasurementsLoadedListener(this);
        loadMeasurements.loadLastMeasurements(this.body_parts);
        /*
        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new HashMap<>();

                for (String body_part : body_parts) {
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(body_part);
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> entries = (Map) dataSnapshot1.getValue();

                            //double weight_value = (double)dataSnapshot1.getValue();
                            String key = dataSnapshot2.getKey();
                            double weight_value = entries.get(key);
                            list.put(body_part, weight_value);
                        }
                        catch (Exception e) {
                            Map<String, Long> entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = (double)entries.get(key);
                            list.put(body_part, weight_value);
                        }
                    }
                }

                ArrayList<String> texts = new ArrayList<>();
                for(int i = 0; i < body_parts.size(); i++) {
                    Double text = list.get(body_parts.get(i));
                    if(text != null) {
                        texts.add(text + " " + "cm");
                    }
                    else {
                        texts.add("-- cm");
                    }
                }

                measurementsListener.onMeasurementsLoaded(texts);
                //((MeasurementsActivity)activity).setCurrentMeasurements(texts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Measurements").child(userId).addValueEventListener(eventListener2);

         */
    }

    @Override
    public void onLastMeasurementsLoaded(HashMap<String, Double> measurements) {
        list = measurements;
        ArrayList<String> texts = new ArrayList<>();
        for(int i = 0; i < body_parts.size(); i++) {
            Double text = list.get(body_parts.get(i));
            if(text != null) {
                texts.add(text + " " + "cm");
            }
            else {
                texts.add("-- cm");
            }
        }

        measurementsListener.onMeasurementsLoaded(texts);
    }

    public void loadUser() {
        LoadUser loadUser = new LoadUser(this);
        loadUser.loadUser();
        /*
        ValueEventListener eventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                genderListener.onGenderLoaded(user.gender);
                //((MeasurementsActivity)activity).setDrawerLayout(user.gender);

                loadBodyPartsDatabase();

                //loadDatabase();
                //setCurrentMeasurements();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener3);


         */
    }


    @Override
    public void onUserLoaded(User user) {
        this.user = user;
        genderListener.onGenderLoaded(this.user.gender);
        //((MeasurementsActivity)activity).setDrawerLayout(user.gender);

        loadBodyPartsDatabase();
    }


}
