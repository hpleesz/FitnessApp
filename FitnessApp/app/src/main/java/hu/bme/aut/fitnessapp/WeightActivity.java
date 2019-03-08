package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewGoalReachedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWaterDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWeightItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.PeriodSelectDialogFragment;

public class WeightActivity extends NavigationActivity implements NewWeightItemDialogFragment.NewWeightDialogListener, WeightAdapter.WeightItemDeletedListener, PeriodSelectDialogFragment.PeriodSelectDialogListener {

    private enum Period {
        ALL, MONTH, WEEK
    }

    private RecyclerView recyclerView;
    private WeightAdapter adapter;
    private WeightListDatabase database;
    private LineChart chart;
    private List<WeightItem> itemlist;
    private Period period = Period.ALL;

    private int startingday;
    private int startingmonth;
    private int startingyear;

    private SharedPreferences sharedPreferences;
    SharedPreferences periodSharedPreferences;


    public static final String PROGRESS = "weight progress";
    public static final String PERIOD = "period";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_weight, null, false);
        mDrawerLayout.addView(contentView, 0);

        initializeValues();
        setPeriod();

        navigationView.getMenu().getItem(2).setChecked(true);

        initRecyclerView();
        setFloatingActionButton();
        drawChart();

    }

    public void initializeValues() {
        sharedPreferences = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);
        periodSharedPreferences = getSharedPreferences(PERIOD, MODE_PRIVATE);

        startingday = sharedPreferences.getInt("Registration day", 0);
        startingmonth = sharedPreferences.getInt("Registration month", 0);
        startingyear = sharedPreferences.getInt("Registration year", 0);

        database = Room.databaseBuilder(
                getApplicationContext(),
                WeightListDatabase.class,
                "weights"
        ).build();

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewWeightItemDialogFragment().show(getSupportFragmentManager(), NewWeightItemDialogFragment.TAG);
            }
        });
    }

    private void drawChart() {
        chart = (LineChart) findViewById(R.id.chartWeight);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateFormatter());
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(3f);
        xAxis.setAxisLineColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));

        YAxis yAxisLeft = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisLineWidth(3f);
        yAxisLeft.setAxisLineColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));


        float goal = sharedPreferences.getFloat("Goal weight", 0);
        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        limitLine.enableDashedLine(20f, 20f, 10f);

        yAxisLeft.addLimitLine(limitLine);

        YAxis yAxisRight = chart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);


        chart.setDescription(null);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.WeightRecyclerView);
        adapter = new WeightAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void updatechart()
    {
        chart.setData(loadEntries());
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private LineData loadEntries()
    {

        List<Entry> entries = new ArrayList<Entry>();

        float weight = sharedPreferences.getFloat("Starting weight", 0);

        long startingdate = makeLongDate(startingyear, startingmonth-1, startingday);
        int starting_calculated = startingyear * 10000 + startingmonth * 100 + startingday;

            if(itemlist.size() > 0) {
                Calendar c = Calendar.getInstance();
                int calculated_today = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE);
                int limit;
                int start;
                if(period == Period.MONTH) {
                    limit = calculated_today - 30;
                    start = itemlist.size() - 30;
                }
                else if (period == Period.WEEK){
                    limit = calculated_today - 7;
                    start = itemlist.size() - 7;
                }
                else {
                    limit = starting_calculated;
                    start = 0;
                }

                if(itemlist.get(0).weight_calculated > limit) {
                    entries.add(new Entry((float) startingdate, weight));
                    start = 0;
                }
                for(int i = start; i < itemlist.size(); i++){
                    if(itemlist.get(i).weight_calculated > limit) {
                        long itemdate = makeLongDate(itemlist.get(i).weight_year, itemlist.get(i).weight_month - 1, itemlist.get(i).weight_day);
                        entries.add(new Entry((float) itemdate, (float) itemlist.get(i).weight_value));
                    }
                }



            LineDataSet dataSet = new LineDataSet(entries, "weights");
            //formazas
            dataSet.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            dataSet.setCircleColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            dataSet.setCircleColorHole(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            dataSet.setLineWidth(3f);
            dataSet.setCircleRadius(5f);
            return new LineData(dataSet);
        }
        return null;
    }

    public long makeLongDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<WeightItem>>() {

            @Override
            protected List<WeightItem> doInBackground(Void... voids) {
                itemlist = adapter.getItems();
                return database.weightItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<WeightItem> weightItems) {
                adapter.update(weightItems);
                updatechart();
            }
        }.execute();
    }

    @Override
    public void onWeightItemCreated(final WeightItem newItem) {
        new AsyncTask<Void, Void, WeightItem>() {

            @Override
            protected WeightItem doInBackground(Void... voids) {
                newItem.weight_id = database.weightItemDao().insert(newItem);
                itemlist = adapter.getItems();
                return newItem;
            }

            @Override
            protected void onPostExecute(WeightItem weightItem) {
                adapter.addItem(weightItem);
                setMostRecentWeightAsCurrent();
                updatechart();
                checkProgress();
            }
        }.execute();
    }

    @Override
    public void onItemDeleted(final WeightItem item) {
        new AsyncTask<Void, Void, WeightItem>() {

            @Override
            protected WeightItem doInBackground(Void... voids) {
                database.weightItemDao().deleteItem(item);
                itemlist = adapter.getItems();
                return item;
            }

            @Override
            protected void onPostExecute(WeightItem weightItem) {
                adapter.deleteItem(weightItem);
                setMostRecentWeightAsCurrent();
                updatechart();
            }
        }.execute();
    }

    public void setMostRecentWeightAsCurrent() {
        double weight = adapter.getLastItemWeight();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(weight == -1){
            weight = sharedPreferences.getFloat("Starting weight", 0);
            editor.putFloat("Current weight", (float)weight);
        }
        else {
            editor.putFloat("Current weight", (float) weight);
        }
        editor.apply();
    }

    public void checkProgress() {
        SharedPreferences progressSharedPreferences = getSharedPreferences(PROGRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = progressSharedPreferences.edit();
        boolean reached_goal_already = progressSharedPreferences.getBoolean("Reached goal already", false);
        if(isGoalReached()) {
            if (!reached_goal_already) {
                new NewGoalReachedDialogFragment().show(getSupportFragmentManager(), NewGoalReachedDialogFragment.TAG);
                editor.putBoolean("Reached goal already", true);
            }
        }
        else {
            if (reached_goal_already)
                editor.putBoolean("Reached goal already", false);
        }
        editor.apply();
    }

    public boolean isGoalReached () {
        float goal_weight = sharedPreferences.getFloat("Goal weight", 0);
        float starting_weight = sharedPreferences.getFloat("Starting weight", 0);
        float current_weight = sharedPreferences.getFloat("Current weight", 0);

        if(goal_weight > starting_weight) return (current_weight / goal_weight) >= 1;
        else if(goal_weight < starting_weight) return (current_weight / goal_weight) <= 1;
        else return (current_weight / goal_weight) == 1;
    }

    @Override
    public void onPeriodSelected() {
        setPeriod();
        updatechart();
    }

    public void setPeriod(){
        String selected = periodSharedPreferences.getString("Period", "all");
        switch(selected){
            case "all" :
                period = Period.ALL;
                break;
            case "month":
                period = Period.MONTH;
                break;
            case "week":
                period = Period.WEEK;
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                new PeriodSelectDialogFragment().show(getSupportFragmentManager(), PeriodSelectDialogFragment.TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
