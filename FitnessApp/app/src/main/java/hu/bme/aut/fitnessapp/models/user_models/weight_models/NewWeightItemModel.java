package hu.bme.aut.fitnessapp.models.user_models.weight_models;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.models.database_models.LoadWeight;

public class NewWeightItemModel implements LoadWeight.WeightLoadedListener{

    private List<Measurement> list;

    private LoadWeight loadWeight;

    public NewWeightItemModel() {}

    public void loadWeight() {
        loadWeight = new LoadWeight();
        loadWeight.setListLoadedListener(this);
        loadWeight.loadWeight();
    }

    @Override
    public void onWeightLoaded(ArrayList<Measurement> weight) {
        list = weight;
    }

    public boolean alreadyExists(Measurement item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.getDate().equals(list.get(i).getDate()))
                return true;
        }
        return false;
    }

    public void removeListeners() {
        if(loadWeight != null) loadWeight.removeListeners();
    }

}
