package hu.bme.aut.fitnessapp.models.user_models.measurement_models;


import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.models.database_models.LoadMeasurements;

public class MeasurementsGraphFragmentModel implements LoadMeasurements.MeasurementsByBodyPartLoadedListener{

    private String bodyPart;
    private ArrayList<Measurement> itemlist;

    private LoadMeasurements loadMeasurements;

    private MeasurementsGraphFragmentModel.ListLoadedListener listLoadedListener;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Measurement> measurements);
        void onChartUpdate(boolean drawValues);
    }

    public MeasurementsGraphFragmentModel(Fragment fragment, String bodyPart) {
        listLoadedListener = (MeasurementsGraphFragmentModel.ListLoadedListener)fragment;
        this.bodyPart = bodyPart;
    }

    public void loadList() {
        loadMeasurements = new LoadMeasurements();
        loadMeasurements.setMeasurementsByBodyPartLoadedListener(this);
        loadMeasurements.loadMeasurementsByBodyPart(bodyPart);
    }

    @Override
    public void onMeasurementsByBodyPartLoaded(ArrayList<Measurement> measurements) {
        itemlist = measurements;
        listLoadedListener.onListLoaded(reverseList());

        if(!itemlist.isEmpty()) {
            listLoadedListener.onChartUpdate(false);
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

        if (!itemlist.isEmpty()) {
            for (int i = 0; i < itemlist.size(); i++) {
                entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).getDate()) * 1000), (float) itemlist.get(i).getValue()));
            }
        } else return null;
        return entries;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void removeListeners() {
        if(loadMeasurements != null) loadMeasurements.removeListeners();
    }

}
