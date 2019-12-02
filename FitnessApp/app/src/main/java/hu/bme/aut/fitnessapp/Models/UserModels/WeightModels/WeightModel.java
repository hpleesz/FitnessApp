package hu.bme.aut.fitnessapp.Models.UserModels.WeightModels;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWeight;

import static android.content.Context.MODE_PRIVATE;
import static hu.bme.aut.fitnessapp.Controllers.User.Weight.WeightActivity.PERIOD;

public class WeightModel implements LoadWeight.WeightLoadedListener , LoadUser.UserLoadedListener
 {

    public enum Period {
        ALL, MONTH, WEEK
    }

    private LoadWeight loadWeight;
    private LoadUser loadUser;

    private ArrayList<Measurement> itemlist;
    private Period period = Period.ALL;
    private User user;

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

    public interface GoalReachedListener {
        void onGoalReached();
    }

    private WeightModel.WeightListListener weightListListener;
    private WeightModel.ChartListener chartListener;
    private WeightModel.GoalReachedListener goalReachedListener;

    public WeightModel(Object object) {
        weightListListener = (WeightModel.WeightListListener)object;
        chartListener = (WeightModel.ChartListener)object;
        goalReachedListener = (WeightModel.GoalReachedListener)object;
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
        weightListListener.onListLoaded(reverseList());
        chartListener.onChartReady();

        if(itemlist.size() > 0) {
            starting_date = Long.parseLong(itemlist.get(0).date);
            starting_weight = itemlist.get(0).value;
            chartListener.onChartUpdate(false);
            //checkProgress();
        }

    }

    private ArrayList<Measurement> reverseList() {
        ArrayList<Measurement> reverseList = new ArrayList<>();
        for(int i = itemlist.size()-1; i >= 0; i--) {
            reverseList.add(itemlist.get(i));
        }
        return reverseList;
    }


    public List<Entry> loadEntries() {

        List<Entry> entries = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        long limit;
        if(period == Period.ALL) {
            limit = starting_date * 1000;
        }
        else {
            if (period == Period.MONTH) {
                c.add(Calendar.DAY_OF_YEAR, -30);
            } else if (period == Period.WEEK) {
                c.add(Calendar.DAY_OF_YEAR, -7);
            }
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            limit = c.getTimeInMillis();
        }

        if (itemlist.size() > 0) {
            for (int i = 0; i < itemlist.size(); i++) {
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
        itemlist.add(newItem);
        checkProgress();
    }

    public void checkProgress() {
        if(user != null && !itemlist.isEmpty()) {
            if (isGoalReached()) {
                if (!user.goal_reached) {
                    user.goal_reached = true;
                    setInProgress();
                    goalReachedListener.onGoalReached();
                }
            } else {
                if (user.goal_reached) {
                    user.goal_reached = false;
                    setInProgress();
                }
            }
        }
    }

    public void setInProgress() {
        loadUser = new LoadUser();
        loadUser.setGoalReached(user.goal_reached);
    }

    public boolean isGoalReached() {
        double current_weight = itemlist.get(itemlist.size()-1).value;

        if (goal_weight > starting_weight) {
            return (current_weight / goal_weight) >= 1;
        }
        else if (goal_weight < starting_weight) {
            return (current_weight / goal_weight) <= 1;
        }
        else {
            return (current_weight / goal_weight) == 1;
        }
    }




    public Period setPeriod() {
        String selected = periodSharedPreferences.getString(FirebaseAuth.getInstance().getCurrentUser().getUid(), "all");
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
        itemlist.remove(item);
        checkProgress();
    }


    public void loadProgressInfo() {
        if(user == null) {
            /*user = new User();
            user.goal_weight = 60.0;
            goal_weight = user.goal_weight;
            user.goal_reached = false;*/

            loadUser = new LoadUser();
            loadUser.setListLoadedListener(this);
            loadUser.loadUser();

        }
    }


    @Override
    public void onUserLoaded(User user) {
            Log.d("user load", "called");
            this.user = user;
            try {
                goal_weight = user.goal_weight;
            } catch (Exception e) {
                goal_weight = user.goal_weight.doubleValue();
            }

        //checkProgress();
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

     public void removeListeners() {
         if(loadUser != null) loadUser.removeListeners();
         if(loadWeight != null) loadWeight.removeListeners();
     }
}
