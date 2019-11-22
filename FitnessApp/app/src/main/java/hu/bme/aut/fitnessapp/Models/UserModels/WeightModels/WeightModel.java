package hu.bme.aut.fitnessapp.Models.UserModels.WeightModels;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWeight;

import static android.content.Context.MODE_PRIVATE;
import static hu.bme.aut.fitnessapp.Controllers.User.Weight.WeightActivity.PERIOD;

public class WeightModel implements LoadWeight.WeightLoadedListener, LoadUser.UserLoadedListener{

    public enum Period {
        ALL, MONTH, WEEK
    }

    private LoadWeight loadWeight;

    private ArrayList<Measurement> itemlist;
    private Period period = Period.ALL;

    private long starting_date;
    private double starting_weight;
    private double goal_weight;

    private SharedPreferences periodSharedPreferences;

    public interface WeightListListener {
        void onListLoaded(ArrayList<Measurement> measurements);
    }

    public interface ChartListener {
        void onChartReady();
        void onChartUpdate(boolean drawValues);
    }

    private WeightModel.WeightListListener weightListListener;
    private WeightModel.ChartListener chartListener;

    public WeightModel(Object object) {
        weightListListener = (WeightModel.WeightListListener)object;
        chartListener = (WeightModel.ChartListener)object;
        periodSharedPreferences = ((AppCompatActivity)object).getSharedPreferences(PERIOD, MODE_PRIVATE);

    }

    public void loadList() {
        loadWeight = new LoadWeight();
        loadWeight.setListLoadedListener(this);
        loadWeight.loadWeight();
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
        loadWeight.addNewItem(Long.parseLong(newItem.date), newItem.value);
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
        loadWeight.removeItem(Long.parseLong(item.date));
    }


    public void loadProgressInfo() {
        LoadUser loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }

    @Override
    public void onUserLoaded(User user) {
        try {
            goal_weight = user.goal_weight;
        }
        catch (Exception e) {
            goal_weight = user.goal_weight.doubleValue();
        }
    }

    public ArrayList<Measurement> getItemlist() {
        return itemlist;
    }

    public void setItemlist(ArrayList<Measurement> itemlist) {
        this.itemlist = itemlist;
    }

    public void setStarting_weight(double starting_weight) {
        this.starting_weight = starting_weight;
    }

    public void setGoal_weight(double goal_weight) {
        this.goal_weight = goal_weight;
    }

    public double getGoal_weight() {
        return goal_weight;
    }

}
