package hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels;


import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadMeasurements;

public class MeasurementsGraphFragmentModel implements LoadMeasurements.MeasurementsByBodyPartLoadedListener{

    private String bodyPart;
    private ArrayList<Measurement> itemlist;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Measurement> measurements);
        void onChartUpdate(boolean drawValues);
    }

    private MeasurementsGraphFragmentModel.ListLoadedListener listLoadedListener;

    public MeasurementsGraphFragmentModel(Fragment fragment, String bodyPart) {
        listLoadedListener = (MeasurementsGraphFragmentModel.ListLoadedListener)fragment;
        this.bodyPart = bodyPart;
    }

    public void loadList() {
        LoadMeasurements loadMeasurements = new LoadMeasurements();
        loadMeasurements.setMeasurementsByBodyPartLoadedListener(this);
        loadMeasurements.loadMeasurementsByBodyPart(bodyPart);
    }

    @Override
    public void onMeasurementsByBodyPartLoaded(ArrayList<Measurement> measurements) {
        itemlist = measurements;
        listLoadedListener.onListLoaded(itemlist);

        if(itemlist.size() > 0) {
            listLoadedListener.onChartUpdate(false);
        }
    }

    public List<Entry> loadEntries() {
        List<Entry> entries = new ArrayList<>();

        if (itemlist.size() > 0) {
            for (int i = 0; i < itemlist.size(); i++) {
                entries.add(new Entry((float) (Long.parseLong(itemlist.get(i).date) * 1000), (float) itemlist.get(i).value));
            }
        } else return null;
        return entries;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

}
