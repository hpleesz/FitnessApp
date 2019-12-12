package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadBodyParts extends DatabaseConnection{

    private static final String BODY_PARTS = "Body_Parts";

    public interface BodyPartsLoadedListener {
        void onBodyPartsLoaded(ArrayList<String> bodyParts);
    }

    private LoadBodyParts.BodyPartsLoadedListener listLoadedListener;

    public LoadBodyParts(Object object) {
        super();
        listLoadedListener = (LoadBodyParts.BodyPartsLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadBodyParts() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> bodyParts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String bodyPart = (String) dataSnapshot1.getValue();
                    bodyParts.add(bodyPart);
                }

                listLoadedListener.onBodyPartsLoaded(bodyParts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(BODY_PARTS).addValueEventListener(eventListener);
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child(BODY_PARTS).removeEventListener(eventListener);

    }

}
