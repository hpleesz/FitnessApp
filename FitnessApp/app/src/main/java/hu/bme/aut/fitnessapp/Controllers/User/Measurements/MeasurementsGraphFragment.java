package hu.bme.aut.fitnessapp.Controllers.User.Measurements;

import android.content.Context;
import android.os.Bundle;
/*
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;*/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.Models.User.Measurements.MeasurementsGraphFragmentModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.MeasurementAdapter;
import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Controllers.User.Weight.DateFormatter;


public class MeasurementsGraphFragment extends Fragment implements com.github.mikephil.charting.listener.OnChartValueSelectedListener, MeasurementsGraphFragmentModel.ListLoadedListener
{

    private MeasurementsGraphFragmentModel measurementsGraphFragmentModel;
    private MeasurementAdapter adapter;
    private String bodyPart;
    private LineChart chart;

    private RecyclerView recyclerView;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurements_graph, container, false);



        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bodyPart = bundle.getString("body part", "Shoulders");
        }
        newModel();
        rootView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                //newModel();
                //measurementsGraphFragmentModel.setBodyPart(bodyPart);
            }
        });

        TextView title = (TextView) rootView.findViewById(R.id.measurementsEntries);
        String text = bodyPart + " " + getString(R.string.entries);
        title.setText(text.replace('_', ' '));

        context = getContext();

        chart = (LineChart) rootView.findViewById(R.id.chartMeasurement);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.MeasurementRecyclerView);

        drawChart();
        return rootView;
    }

    private void newModel() {
        measurementsGraphFragmentModel = new MeasurementsGraphFragmentModel(this, bodyPart);
        measurementsGraphFragmentModel.initFirebase();
        measurementsGraphFragmentModel.loadList();
    }

    private void initRecyclerView(ArrayList<Measurement> measurements) {
        adapter = new MeasurementAdapter((MeasurementsGraphActivity) getActivity(), measurements, bodyPart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }


    private void drawChart() {
        chart.setOnChartValueSelectedListener(this);
        chart.setNoDataTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
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

        List<Entry> entries = measurementsGraphFragmentModel.loadEntries();

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



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && (adapter != null)) {
            measurementsGraphFragmentModel.loadList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        if(measurementsGraphFragmentModel == null) {
            //measurementsGraphFragmentModel = new MeasurementsGraphFragmentModel(this, bodyPart);

        }
        else

         */
        //measurementsGraphFragmentModel.loadList();
        //newModel();


        //measurementsGraphFragmentModel.loadList();
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
        drawChart();
    }

    @Override
    public void onChartUpdate(boolean drawValues) {
        updatechart(drawValues);
    }
}
