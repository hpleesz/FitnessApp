package hu.bme.aut.fitnessapp.fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.MeasurementsActivity;
import hu.bme.aut.fitnessapp.MeasurementsGraphActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.WeightActivity;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementAdapter;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.models.Measurement;
import hu.bme.aut.fitnessapp.models.Weight;
import hu.bme.aut.fitnessapp.tools.DateFormatter;


public class MeasurementsGraphFragment extends Fragment implements com.github.mikephil.charting.listener.OnChartValueSelectedListener
{

    private MeasurementDatabase database;
    private MeasurementAdapter adapter;
    private String bodyPart;
    private LineChart chart;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private RecyclerView recyclerView;

    private ArrayList<Weight> itemlist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurements_graph, container, false);

        rootView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                loadList();
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bodyPart = bundle.getString("body part", "Shoulders");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView title = (TextView) rootView.findViewById(R.id.measurementsEntries);
        String text = bodyPart + " " + getString(R.string.entries);
        title.setText(text);

        chart = (LineChart) rootView.findViewById(R.id.chartMeasurement);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.MeasurementRecyclerView);

        drawChart();
        return rootView;
    }

    private void initRecyclerView() {
        adapter = new MeasurementAdapter((MeasurementsGraphActivity) getActivity(), itemlist, bodyPart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }



    private void drawChart() {
        chart.setOnChartValueSelectedListener(this);
        chart.setNoDataTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateFormatter());
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(3f);
        xAxis.setAxisLineColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorBlack));
        xAxis.setCenterAxisLabels(true);

        YAxis yAxisLeft = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisLineWidth(3f);
        yAxisLeft.setAxisLineColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorBlack));


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

    public void updatechart(boolean drawvalues) {
        chart.setData(loadEntries(drawvalues));
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private LineData loadEntries(boolean drawvalues) {

        List<Entry> entries = new ArrayList<>();

        if (itemlist.size() > 0) {
            for (int i = 0; i < itemlist.size(); i++) {
                    entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).date) * 1000), (float) itemlist.get(i).value));
            }
        } else return null;

        LineDataSet dataSet = new LineDataSet(entries, "measurements");
        //formazas
        dataSet.setColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorPrimary));
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAccent));
        dataSet.setCircleColorHole(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAccent));
        dataSet.setDrawValues(drawvalues);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(2f);
        dataSet.setHighLightColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAccent));
        dataSet.setHighlightLineWidth(1f);
        dataSet.setValueTextSize(8f);
        return new LineData(dataSet);
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
                    updatechart(false);

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
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (adapter != null)) {
            loadList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadList();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        updatechart(true);
    }

    @Override
    public void onNothingSelected() {
        updatechart(false);
    }


}
