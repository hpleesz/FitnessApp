package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.StretchActivity;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadStretch;

public class StretchModel extends StretchWarmUpModel implements LoadStretch.StretchLoadedListener {

    private VideoView videoView;
    private TextView titleTextView;
    private int idx = 0;

    public StretchModel(Context activity) {
        super(activity);
    }

    public void loadItems() {
        LoadStretch loadStretch = new LoadStretch(this);
        loadStretch.loadStretch();
        /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String item = dataSnapshot1.getValue(String.class);
                    items.add(item);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Stretch").addValueEventListener(eventListener);

         */
    }


    @Override
    public void onStretchLoaded(ArrayList<String> stretchList) {

        setItems(stretchList);
        getExerciseListLoadedListener().onExerciseListLoaded();
        /*
        ((StretchActivity)getActivity()).setLayoutElements();
        ((StretchActivity)getActivity()).setExercise();
        ((StretchActivity)getActivity()).setFloatingActionButtons();

         */
    }
}
