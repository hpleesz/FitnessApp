package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadMuscles extends DatabaseConnection {

    private static final String MUSCLES = "Muscles";

    public interface MusclesLoadedListener {
        void onUpperBodyLoaded(ArrayList<String> muscles);
        void onLowerBodyLoaded(ArrayList<String> muscles);
    }

    private LoadMuscles.MusclesLoadedListener listLoadedListener;

    public LoadMuscles(Object object) {
        super();
        listLoadedListener = (LoadMuscles.MusclesLoadedListener)object;
    }

    private ValueEventListener lowerEventListener;
    private ValueEventListener upperEventListener;

    public void loadLowerBodyParts() {

        lowerEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> lowerBodyParts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    lowerBodyParts.add(name);
                }

                listLoadedListener.onLowerBodyLoaded(lowerBodyParts);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(MUSCLES).child("Lower").addValueEventListener(lowerEventListener);

    }

    public void loadUpperBodyParts() {

        upperEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> upperBodyParts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    upperBodyParts.add(name);
                }

                listLoadedListener.onUpperBodyLoaded(upperBodyParts);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error", databaseError.toException());
            }

        };
        getDatabaseReference().child(MUSCLES).child("Upper").addValueEventListener(upperEventListener);
    }

    public void removeListeners() {
        if(lowerEventListener != null) getDatabaseReference().child(MUSCLES).child("Lower").addValueEventListener(lowerEventListener);
        if(lowerEventListener != null) getDatabaseReference().child(MUSCLES).child("Upper").addValueEventListener(upperEventListener);
    }
}
