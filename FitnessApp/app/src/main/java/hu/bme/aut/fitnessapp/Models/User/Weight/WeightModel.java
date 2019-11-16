package hu.bme.aut.fitnessapp.Models.User.Weight;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.Controllers.User.Weight.WeightActivity;
import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWeight;

import static android.content.Context.MODE_PRIVATE;
import static hu.bme.aut.fitnessapp.Controllers.User.Weight.WeightActivity.PERIOD;

public class WeightModel implements LoadWeight.WeightLoadedListener, LoadUser.UserLoadedListener{

    public enum Period {
        ALL, MONTH, WEEK
    }

    private DatabaseReference databaseReference;
    private String userId;

    public ArrayList<Measurement> getItemlist() {
        return itemlist;
    }

    public void setItemlist(ArrayList<Measurement> itemlist) {
        this.itemlist = itemlist;
    }

    public long getStarting_date() {
        return starting_date;
    }

    public void setStarting_date(long starting_date) {
        this.starting_date = starting_date;
    }

    public double getStarting_weight() {
        return starting_weight;
    }

    public void setStarting_weight(double starting_weight) {
        this.starting_weight = starting_weight;
    }

    public double getGoal_weight() {
        return goal_weight;
    }

    public void setGoal_weight(double goal_weight) {
        this.goal_weight = goal_weight;
    }

    private ArrayList<Measurement> itemlist;
    private Period period = Period.ALL;

    private long starting_date;
    private double starting_weight;

    private double goal_weight;

    private User user;
    private SharedPreferences periodSharedPreferences;

    private Context activity;

    public interface WeightListListener {
        void onListLoaded(ArrayList<Measurement> measurements);
    }

    public interface ChartListener {
        void onChartReady();
        void onChartUpdate(boolean drawValues);
    }

    private WeightModel.WeightListListener weightListListener;
    private WeightModel.ChartListener chartListener;

    public WeightModel(Context activity) {
        weightListListener = (WeightModel.WeightListListener)activity;
        chartListener = (WeightModel.ChartListener)activity;
        this.activity = activity;
        periodSharedPreferences = ((WeightActivity)activity).getSharedPreferences(PERIOD, MODE_PRIVATE);

    }

    public void initFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public void loadList() {
        LoadWeight loadWeight = new LoadWeight();
        loadWeight.setListLoadedListener(this);
        loadWeight.loadWeight();

        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> entries = (Map) dataSnapshot.getValue();

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
                    //checkProgress();

                }
                weightListListener.onListLoaded(itemlist);
                chartListener.onChartReady();

                //((WeightActivity)activity).initRecyclerView(itemlist);
                //((WeightActivity)activity).drawChart();

                if(itemlist.size() > 0) {
                    starting_date = Long.parseLong(itemlist.get(0).date);
                    starting_weight = itemlist.get(0).value;
                    chartListener.onChartUpdate(false);
                    //((WeightActivity)activity).updatechart(false);
                }
                //checkProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Weight").child(userId).addValueEventListener(eventListener);

         */
    }

    @Override
    public void onWeightLoaded(ArrayList<Measurement> weight) {
        itemlist = weight;
        weightListListener.onListLoaded(itemlist);
        chartListener.onChartReady();

        if(itemlist.size() > 0) {
            starting_date = Long.parseLong(itemlist.get(0).date);
            starting_weight = itemlist.get(0).value;
            chartListener.onChartUpdate(false);
        }
        //checkProgress();
    }


    public List<Entry> loadEntries() {

        List<Entry> entries = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        long limit;
        int start = 0;
        if (period == Period.MONTH) {
            c.add(Calendar.DAY_OF_YEAR, -30);

            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            limit = c.getTimeInMillis();

        } else if (period == Period.WEEK) {
            c.add(Calendar.DAY_OF_YEAR, -7);

            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            limit = c.getTimeInMillis();

        } else {
            limit = starting_date * 1000;
        }

        if (itemlist.size() > 0) {
            for (int i = start; i < itemlist.size(); i++) {
                if (Long.parseLong(itemlist.get(i).date) * 1000 >= limit) {
                    entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).date) * 1000), (float) itemlist.get(i).value));
                }
            }

        }
        if (entries.size() == 0) return null;


        return entries;
    }

    public void createWeight(final Measurement newItem) {
        databaseReference.child("Weight").child(userId).child(newItem.date).setValue(newItem.value);
    }
/*
    public void checkProgress() {

        if(user != null) {
            if (isGoalReached()) {
                if (!user.goal_reached) {
                    user.goal_reached = true;
                    new NewGoalReachedDialogFragment().show(getSupportFragmentManager(), NewGoalReachedDialogFragment.TAG);

                }
            } else {
                if (user.goal_reached)
                    user.goal_reached = false;
            }
            databaseReference.child("Users").child(userId).child("goal_reached").setValue(user.goal_reached);
        }

    }
 */

    public boolean isGoalReached() {

        double current_weight = itemlist.get(itemlist.size()-1).value;

        if (goal_weight > starting_weight) return (current_weight / goal_weight) >= 1;
        else if (goal_weight < starting_weight) return (current_weight / goal_weight) <= 1;
        else return (current_weight / goal_weight) == 1;
    }




    public Period setPeriod() {
        String selected = periodSharedPreferences.getString("Period", "all");
        switch (selected) {
            case "all":
                period = Period.ALL;
                break;
            case "month":
                period = Period.MONTH;
                break;
            case "week":
                period = Period.WEEK;
                break;
        }
        return period;
    }

    public void deleteWeight(final Measurement item) {
        databaseReference.child("Weight").child(userId).child(item.date).removeValue();
        //checkProgress();
    }


    public void loadProgressInfo() {
        LoadUser loadUser = new LoadUser(this);
        loadUser.loadUser();

        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                try {
                    goal_weight = user.goal_weight;
                }
                catch (Exception e) {
                    goal_weight = user.goal_weight.doubleValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
    }

    @Override
    public void onUserLoaded(User user) {
        this.user = user;
        try {
            goal_weight = this.user.goal_weight;
        }
        catch (Exception e) {
            goal_weight = this.user.goal_weight.doubleValue();
        }
    }


}
