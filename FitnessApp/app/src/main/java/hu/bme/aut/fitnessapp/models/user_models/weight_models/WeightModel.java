package hu.bme.aut.fitnessapp.models.user_models.weight_models;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;
import hu.bme.aut.fitnessapp.models.database_models.LoadWeight;

import static android.content.Context.MODE_PRIVATE;
import static hu.bme.aut.fitnessapp.controllers.user.weight.WeightActivity.PERIOD;

public class WeightModel implements LoadWeight.WeightLoadedListener , LoadUser.UserLoadedListener
 {

    public enum Period {
        ALL, MONTH, WEEK
    }

    private LoadWeight loadWeight;
    private LoadUser loadUser;

    private List<Measurement> itemlist;
    private Period period = Period.ALL;
    private User user;

    private long startingDate;
    private double startingWeight;
    private double goalWeight;

    private SharedPreferences periodSharedPreferences;

     private WeightModel.WeightListListener weightListListener;
     private WeightModel.ChartListener chartListener;
     private WeightModel.GoalReachedListener goalReachedListener;

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

        if(!itemlist.isEmpty()) {
            startingDate = Long.parseLong(itemlist.get(0).getDate());
            startingWeight = itemlist.get(0).getValue();
            chartListener.onChartUpdate(false);
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
            limit = startingDate * 1000;
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

        if (!itemlist.isEmpty()) {
            for (int i = 0; i < itemlist.size(); i++) {
                if (Long.parseLong(itemlist.get(i).getDate()) * 1000 >= limit) {
                    entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).getDate()) * 1000), (float) itemlist.get(i).getValue()));
                }
            }

        }
        if (entries.isEmpty()) return null;


        return entries;
    }

    public void createWeight(final Measurement newItem) {
        loadWeight.addNewItem(Long.parseLong(newItem.getDate()), newItem.getValue());
        itemlist.add(newItem);
        checkProgress();
    }

    public void checkProgress() {
        if(user != null && !itemlist.isEmpty()) {
            if (isGoalReached()) {
                if (!user.getGoalReached()) {
                    user.setGoalReached(true);
                    setInProgress();
                    goalReachedListener.onGoalReached();
                }
            } else {
                if (user.getGoalReached()) {
                    user.setGoalReached(false);
                    setInProgress();
                }
            }
        }
    }

    public void setInProgress() {
        loadUser = new LoadUser();
        loadUser.setGoalReached(user.getGoalReached());
    }

    public boolean isGoalReached() {
        double currentWeight = itemlist.get(itemlist.size()-1).getValue();

        if (goalWeight > startingWeight) {
            return (currentWeight / goalWeight) >= 1;
        }
        else if (goalWeight < startingWeight) {
            return (currentWeight / goalWeight) <= 1;
        }
        else {
            return (currentWeight / goalWeight) == 1;
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
            default:
                period = Period.ALL;
                break;
        }
        return period;
    }

    public void deleteWeight(final Measurement item) {
        loadWeight.removeItem(Long.parseLong(item.getDate()));
        itemlist.remove(item);
        checkProgress();
    }


    public void loadProgressInfo() {
        if(user == null) {
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
                goalWeight = user.getGoalWeight();
            } catch (Exception e) {
                goalWeight = user.getGoalWeight().doubleValue();
            }

    }


    public List<Measurement> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<Measurement> itemlist) {
        this.itemlist = itemlist;
    }

    public void setStartingWeight(double startingWeight) {
        this.startingWeight = startingWeight;
    }

    public void setGoalWeight(double goalWeight) {
        this.goalWeight = goalWeight;
    }

    public double getGoalWeight() {
        return goalWeight;
    }

     public void removeListeners() {
         if(loadUser != null) loadUser.removeListeners();
         if(loadWeight != null) loadWeight.removeListeners();
     }
}
