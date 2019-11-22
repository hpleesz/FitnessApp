package hu.bme.aut.fitnessapp.Controllers.User.Weight;

import android.content.Context;
import android.os.Bundle;
/*
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

 */
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.Models.UserModels.WeightModels.WeightModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Controllers.Adapters.WeightAdapter;
import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Entities.User;

public class WeightActivity extends NavigationActivity implements NewWeightItemDialogFragment.NewWeightDialogListener, WeightAdapter.WeightItemDeletedListener, PeriodSelectDialogFragment.PeriodSelectDialogListener, com.github.mikephil.charting.listener.OnChartValueSelectedListener,
WeightModel.ChartListener, WeightModel.WeightListListener{

    private LineChart chart;

    public static final String PERIOD = "period";

    private WeightModel weightModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_weight, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(2).setChecked(true);

        initializeValues();
        weightModel.setPeriod();

        setFloatingActionButton();
    }

    public void initializeValues() {

        weightModel = new WeightModel(this);
        weightModel.loadProgressInfo();
        weightModel.loadList();

        chart = (LineChart) findViewById(R.id.chartWeight);

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


    public void drawChart() {
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

        LimitLine limitLine = new LimitLine((float) weightModel.getGoal_weight(), "Goal");
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


    public void initRecyclerView(ArrayList<Measurement> itemlist) {
        RecyclerView recyclerView = findViewById(R.id.WeightRecyclerView);
        WeightAdapter adapter = new WeightAdapter(this, itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    public void updatechart(boolean drawvalues) {
        List<Entry> entries = weightModel.loadEntries();

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

        chart.setData(new LineData(dataSet));

        chart.notifyDataSetChanged();
        chart.invalidate();


    }


    @Override
    public void onWeightItemCreated(final Measurement newItem) {
        weightModel.createWeight(newItem);
        //databaseReference.child("Weight").child(userId).child(newItem.date).setValue(newItem.value);
        //itemlist = adapter.getItems();
        //checkProgress();
    }

    @Override
    public void onPeriodSelected() {
        weightModel.setPeriod();
        updatechart(false);
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
    public void onItemDeleted(final Measurement item) {
        weightModel.deleteWeight(item);
        //databaseReference.child("Weight").child(userId).child(item.date).removeValue();
        //checkProgress();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        updatechart(true);
    }

    @Override
    public void onNothingSelected() {
        updatechart(false);
    }

    @Override
    public void onListLoaded(ArrayList<Measurement> measurements) {
        initRecyclerView(measurements);
    }

    @Override
    public void onChartReady() {
        drawChart();
    }

    @Override
    public void onChartUpdate(boolean drawValues) {
        updatechart(drawValues);
    }
}
