package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.fragments.NewGoalReachedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWeightItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.PeriodSelectDialogFragment;
import hu.bme.aut.fitnessapp.tools.DateFormatter;


public class WeightActivity extends NavigationActivity {
        //implements NewWeightItemDialogFragment.NewWeightDialogListener, WeightAdapter.WeightItemDeletedListener, PeriodSelectDialogFragment.PeriodSelectDialogListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener {
/*
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        updatechart(true);
    }

    @Override
    public void onNothingSelected() {
        updatechart(false);
    }

    private enum Period {
        ALL, MONTH, WEEK
    }

    private WeightAdapter adapter;
    private WeightListDatabase database;
    private LineChart chart;
    private List<WeightItem> itemlist;
    private Period period = Period.ALL;

    private int startingday;
    private int startingmonth;
    private int startingyear;

    private SharedPreferences sharedPreferences;
    private SharedPreferences periodSharedPreferences;

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
        chart.setOnChartValueSelectedListener(this);
        chart.setNoDataTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateFormatter());
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(3f);
        xAxis.setAxisLineColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
        xAxis.setCenterAxisLabels(true);

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
        RecyclerView recyclerView = findViewById(R.id.WeightRecyclerView);
        adapter = new WeightAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void updatechart(boolean drawvalues) {
        chart.setData(loadEntries(drawvalues));
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private LineData loadEntries(boolean drawvalues) {

        List<Entry> entries = new ArrayList<Entry>();

        float weight = sharedPreferences.getFloat("Starting weight", 0);

        //long startingdate = makeLongDate(startingyear, startingmonth-1, startingday);
        long startingdate = makeLongDate(startingyear, startingmonth, startingday);

        //int starting_calculated = startingyear * 10000 + startingmonth * 100 + startingday;
        int starting_calculated = makeCalculatedWeight(startingyear, startingmonth, startingday);


        Calendar c = Calendar.getInstance();
        //int calculated_today = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE);
        int limit;
        int start = 0;
        if (period == Period.MONTH) {
            c.add(Calendar.DAY_OF_YEAR, -30);
            //limit = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE);
            //limit = makeCalculatedWeight(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
            limit = makeCalculatedWeight(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

            //limit = calculated_today - 100;
            //start = itemlist.size() - 30;
        } else if (period == Period.WEEK) {
            c.add(Calendar.DAY_OF_YEAR, -7);
            //limit = makeCalculatedWeight(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
            limit = makeCalculatedWeight(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

            //limit = calculated_today - 7;
            //start = itemlist.size() - 7;
        } else {
            limit = starting_calculated;
            //start = 0;
        }

        if (itemlist.size() > 0) {
            if (starting_calculated >= limit) {
                entries.add(new Entry((float) startingdate, weight));
                //start = 0;
            }
            for (int i = start; i < itemlist.size(); i++) {
                if (itemlist.get(i).weight_calculated >= limit) {
                    //long itemdate = makeLongDate(itemlist.get(i).weight_year, itemlist.get(i).weight_month-1, itemlist.get(i).weight_day);
                    long itemdate = makeLongDate(itemlist.get(i).weight_year, itemlist.get(i).weight_month, itemlist.get(i).weight_day);

                    entries.add(new Entry((float) itemdate, (float) itemlist.get(i).weight_value));
                }
            }

        }
        if (itemlist.size() == 0 && starting_calculated >= limit)
            entries.add(new Entry((float) startingdate, weight));
        if (entries.size() == 0) return null;

        LineDataSet dataSet = new LineDataSet(entries, "weights");
        //formazas
        dataSet.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        dataSet.setCircleColorHole(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        dataSet.setDrawValues(drawvalues);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(2f);
        dataSet.setHighLightColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        dataSet.setHighlightLineWidth(1f);
        dataSet.setValueTextSize(8f);
        return new LineData(dataSet);
    }


    public long makeLongDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public int makeCalculatedWeight(int year, int fixedmonth, int day) {
        int calculated = year * 10000 + fixedmonth * 100 + day;
        return calculated;
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
                updatechart(false);
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
                updatechart(false);
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
                updatechart(false);
            }
        }.execute();
    }

    public void setMostRecentWeightAsCurrent() {
        double weight = adapter.getLastItemWeight();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (weight == -1) {
            weight = sharedPreferences.getFloat("Starting weight", 0);
            editor.putFloat("Current weight", (float) weight);
        } else {
            editor.putFloat("Current weight", (float) weight);
        }
        editor.apply();
    }

    public void checkProgress() {
        SharedPreferences progressSharedPreferences = getSharedPreferences(PROGRESS, MODE_PRIVATE);
        SharedPreferences.Editor editor = progressSharedPreferences.edit();
        boolean reached_goal_already = progressSharedPreferences.getBoolean("Reached goal already", false);
        if (isGoalReached()) {
            if (!reached_goal_already) {
                new NewGoalReachedDialogFragment().show(getSupportFragmentManager(), NewGoalReachedDialogFragment.TAG);
                editor.putBoolean("Reached goal already", true);
            }
        } else {
            if (reached_goal_already)
                editor.putBoolean("Reached goal already", false);
        }
        editor.apply();
    }

    public boolean isGoalReached() {
        float goal_weight = sharedPreferences.getFloat("Goal weight", 0);
        float starting_weight = sharedPreferences.getFloat("Starting weight", 0);
        float current_weight = sharedPreferences.getFloat("Current weight", 0);

        if (goal_weight > starting_weight) return (current_weight / goal_weight) >= 1;
        else if (goal_weight < starting_weight) return (current_weight / goal_weight) <= 1;
        else return (current_weight / goal_weight) == 1;
    }

    @Override
    public void onPeriodSelected() {
        setPeriod();
        updatechart(false);
    }

    public void setPeriod() {
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        return true;
    }
 */}


