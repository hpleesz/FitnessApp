package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.WarmUpActivity;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWarmUp;

public class WarmUpModel extends StretchWarmUpModel implements LoadWarmUp.WarmUpLoadedListener{

    private String type;

    public boolean isLower() {
        return lower;
    }

    private boolean lower = true;

    private ArrayList<String> items;


    public WarmUpModel(Context activity, String type) {
        super(activity);
        this.type = type;
    }


    public void loadItems() {
        setType(((WarmUpActivity)getActivity()).getIntentType());
        getType();
        LoadWarmUp loadWarmUp = new LoadWarmUp(this);
        loadWarmUp.loadWarmUp(lower);
        /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        getType();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String item = dataSnapshot1.child("Name").getValue(String.class);
                    if(lower) {
                        if(dataSnapshot1.child("Lower").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                    else {
                        if(dataSnapshot1.child("Upper").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                }
                setItems(items);
                ((WarmUpActivity)getActivity()).setLayoutElements();
                ((WarmUpActivity)getActivity()).setExercise();
                ((WarmUpActivity)getActivity()).setFloatingActionButtons();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Warmup").addValueEventListener(eventListener);

         */
    }

    public void getType() {
        switch (type) {
            case "Cardio 1":
            case "Cardio 2":
            case "Lower body":
                lower = true;
                break;
            case "Upper body":
                lower = false;
                break;
        }
    }

    @Override
    public void onWarmUpLoaded(ArrayList<String> warmUpList) {
        setItems(warmUpList);
        getExerciseListLoadedListener().onExerciseListLoaded();
    /*
        ((WarmUpActivity)getActivity()).setLayoutElements();
        ((WarmUpActivity)getActivity()).setExercise();
        ((WarmUpActivity)getActivity()).setFloatingActionButtons();

     */
    }

    public void setType(String type) {
        this.type = type;
    }
}
