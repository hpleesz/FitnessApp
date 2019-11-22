package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadMuscles extends DatabaseConnection {

    public interface MusclesLoadedListener {
        void onUpperBodyLoaded(ArrayList<String> muscles);
        void onLowerBodyLoaded(ArrayList<String> muscles);
    }

    private LoadMuscles.MusclesLoadedListener listLoadedListener;

    public LoadMuscles(Object object) {
        super();
        listLoadedListener = (LoadMuscles.MusclesLoadedListener)object;
    }

    public void loadLowerBodyParts() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> lower_body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    lower_body_parts.add(name);
                }

                listLoadedListener.onLowerBodyLoaded(lower_body_parts);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Muscles").child("Lower").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    public void loadUpperBodyParts() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> upper_body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    upper_body_parts.add(name);
                }

                listLoadedListener.onUpperBodyLoaded(upper_body_parts);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Muscles").child("Upper").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }
}
