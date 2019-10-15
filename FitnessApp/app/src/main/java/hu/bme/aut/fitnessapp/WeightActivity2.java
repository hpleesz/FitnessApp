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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.fragments.NewGoalReachedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWeightItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.PeriodSelectDialogFragment;
import hu.bme.aut.fitnessapp.models.User;
import hu.bme.aut.fitnessapp.models.Weight;
import hu.bme.aut.fitnessapp.tools.DateFormatter;

public class WeightActivity2 extends NavigationActivity implements NewWeightItemDialogFragment.NewWeightDialogListener, WeightAdapter.WeightItemDeletedListener, PeriodSelectDialogFragment.PeriodSelectDialogListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener{

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

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private LineChart chart;
    private ArrayList<Weight> itemlist;
    private Period period = Period.ALL;

    private long starting_date;
    private double starting_weight;

    private double goal_weight;

    private User user;

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

        loadList();
        loadProgressInfo();
        setFloatingActionButton();
    }

    public void initializeValues() {

        periodSharedPreferences = getSharedPreferences(PERIOD, MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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


        /*
        float goal = sharedPreferences.getFloat("Goal weight", 0);
        LimitLine limitLine = new LimitLine(goal, "Goal");
        limitLine.setLineColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        limitLine.enableDashedLine(20f, 20f, 10f);

        yAxisLeft.addLimitLine(limitLine);

         */

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
        adapter = new WeightAdapter(this, itemlist);
        //loadItemsInBackground();
        //loadList();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void loadList() {

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
                    Weight weight = new Weight(key, weight_value);
                    itemlist.add(weight);
                }
                catch (Exception e) {
                    Map<String, Long> entries = (Map) dataSnapshot.getValue();

                    String key = dataSnapshot1.getKey();
                    double weight_value = (double)entries.get(key);
                    Weight weight = new Weight(key, weight_value);
                    itemlist.add(weight);
                }

            }
            initRecyclerView();

            drawChart();

            if(itemlist.size() > 0) {
                starting_date = Long.parseLong(itemlist.get(0).date);
                starting_weight = itemlist.get(0).value;
                updatechart(false);

            }
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Weight").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }


    public void updatechart(boolean drawvalues) {
        chart.setData(loadEntries(drawvalues));
        chart.notifyDataSetChanged();
        chart.invalidate();
    }


    private LineData loadEntries(boolean drawvalues) {

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

    @Override
    public void onWeightItemCreated(final Weight newItem) {
        databaseReference.child("Weight").child(userId).child(newItem.date).setValue(newItem.value);
        checkProgress();
    }

    public void checkProgress() {

        if (isGoalReached()) {
            if (!user.goal_reached) {
                user.goal_reached = true;

            }
        } else {
            if (user.goal_reached)
                user.goal_reached = false;
        }
        databaseReference.child("Users").child(userId).child("goal_reached").setValue(user.goal_reached);

    }

    public boolean isGoalReached() {

        double current_weight = itemlist.get(itemlist.size()-1).value;

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

    @Override
    public void onItemDeleted(final Weight item) {
        databaseReference.child("Weight").child(userId).child(item.date).removeValue();
    }


    public void loadProgressInfo() {

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
    }
}
