package hu.bme.aut.fitnessapp.Models.UserModels.WeightModels;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWeight;

public class NewWeightItemModel implements LoadWeight.WeightLoadedListener{

    private List<Measurement> list;

    public NewWeightItemModel() {}

    public void loadWeight() {
        LoadWeight loadWeight = new LoadWeight();
        loadWeight.setListLoadedListener(this);
        loadWeight.loadWeight();
    }

    @Override
    public void onWeightLoaded(ArrayList<Measurement> weight) {
        list = weight;
    }

    public boolean alreadyExists(Measurement item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.date.equals(list.get(i).date))
                return true;
        }
        return false;
    }

}
