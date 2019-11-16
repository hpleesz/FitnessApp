package hu.bme.aut.fitnessapp.Models.User.Measurements;


import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadMeasurements;

public class MeasurementsGraphFragmentModel implements LoadMeasurements.MeasurementsByBodyPartLoadedListener{

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    private String bodyPart;
    private ArrayList<Measurement> itemlist;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Measurement> measurements);
        void onChartUpdate(boolean drawValues);
    }

    private MeasurementsGraphFragmentModel.ListLoadedListener listLoadedListener;

    public MeasurementsGraphFragmentModel(Fragment fragment, String bodyPart) {
        listLoadedListener = (MeasurementsGraphFragmentModel.ListLoadedListener)fragment;
        this.bodyPart = bodyPart;

        //loadList();
    }

    public void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadList() {
        LoadMeasurements loadMeasurements = new LoadMeasurements();
        loadMeasurements.setMeasurementsByBodyPartLoadedListener(this);
        loadMeasurements.loadMeasurementsByBodyPart(bodyPart);
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> entries = (Map) dataSnapshot.getValue();

                        //double weight_value = (double)dataSnapshot1.getValue();
                        String key = dataSnapshot1.getKey();
                        double weight_value = entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        itemlist.add(measurement);
                    }
                    catch (Exception e) {
                        Map<String, Long> entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = (double)entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        itemlist.add(measurement);
                    }

                }
                listLoadedListener.onListLoaded(itemlist);
                //initRecyclerView(itemlist);
                //drawChart();

                if(itemlist.size() > 0) {
                    listLoadedListener.onChartUpdate(false);
                    //updatechart(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Measurements").child(userId).child(bodyPart).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }

    @Override
    public void onMeasurementsByBodyPartLoaded(ArrayList<Measurement> measurements) {
        itemlist = measurements;
        listLoadedListener.onListLoaded(itemlist);

        if(itemlist.size() > 0) {
            listLoadedListener.onChartUpdate(false);
        }
    }

    public List<Entry> loadEntries() {
        List<Entry> entries = new ArrayList<>();

        if (itemlist.size() > 0) {
            for (int i = 0; i < itemlist.size(); i++) {
                entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).date) * 1000), (float) itemlist.get(i).value));
            }
        } else return null;
        return entries;
    }
}
