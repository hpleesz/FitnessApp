package hu.bme.aut.fitnessapp.Models.UserModels.LocationModels;

import androidx.fragment.app.DialogFragment;


import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadPublicLocations;

public class PublicSearchMatchModel implements LoadPublicLocations.PublicLocationsLoadedListener{

    private PublicLocation publicLocation;
    private ArrayList<Boolean> openDays;
    private ArrayList<PublicLocation> itemList;
    private ArrayList<PublicLocation> matchList;

    public interface ListLoadedListener {
        void onListLoaded();
    }

    public interface NoMatchListener {
        void onNoMatchFound();
    }

    private PublicSearchMatchModel.ListLoadedListener listLoadedListener;
    private PublicSearchMatchModel.NoMatchListener noMatchListener;

    private LoadPublicLocations loadPublicLocations;

    public PublicSearchMatchModel(DialogFragment fragment, PublicLocation publicLocation, ArrayList<Boolean> openDays) {
        listLoadedListener = (PublicSearchMatchModel.ListLoadedListener)fragment;
        noMatchListener = (PublicSearchMatchModel.NoMatchListener) fragment;
        this.publicLocation = publicLocation;
        this.openDays = openDays;

    }


    public void loadList() {
        loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedListener(this);
        loadPublicLocations.loadPublicLocations();
    }

    @Override
    public void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations) {
        itemList = publicLocations;
        findMatch();
        listLoadedListener.onListLoaded();

    }

    public void findMatch() {
        matchList = new ArrayList<>();

        ArrayList<Integer[]> times = convertTimesToInt();


        for (PublicLocation loc : itemList) {

            boolean match = true;

            if (!locationDetailsMatch(loc)) {
                continue;
            }

            if(!compareTimes(times, loc)) {
                continue;
            }

            for(Integer item : publicLocation.equipment) {
                if(!loc.equipment.contains(item)) {
                    match = false;
                    break;
                }

            }
            if(match) {
                matchList.add(loc);
            }
        }

        if(matchList.isEmpty()) {
            noMatchListener.onNoMatchFound();
        }
    }

    public ArrayList<Integer[]> convertTimesToInt() {
        ArrayList<Integer[]> times = new ArrayList<>();
        for (int i = 0; i < publicLocation.open_hours.size(); i++) {
            Integer[] open_close = new Integer[2];

            for (int j = 0; j < 2; j++) {
                if (!publicLocation.open_hours.get(i)[j].equals("")) {
                    String time = publicLocation.open_hours.get(i)[j].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    if (time.equals("")) open_close[j] = 0;
                    else open_close[j] = Integer.parseInt(time);
                } else {
                    open_close[j] = -1;
                }
            }
            times.add(open_close);
        }
        return times;
    }


    public boolean locationDetailsMatch(PublicLocation loc) {
        return ((publicLocation.name.equals("") || loc.name.equals(publicLocation.name)) &&
                (publicLocation.description.equals("") || loc.description.equals(publicLocation.description)) &&
                (publicLocation.zip.equals("") || loc.zip.equals(publicLocation.zip)) &&
                (publicLocation.country.equals("") || loc.country.equals(publicLocation.country)) &&
                (publicLocation.city.equals("") || loc.city.equals(publicLocation.city)) &&
                (publicLocation.address.equals("") || loc.address.equals(publicLocation.address)));
    }

    public boolean compareTimes(ArrayList<Integer[]> times, PublicLocation loc) {
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i)[0] != -1) {

                String time = loc.open_hours.get(i)[0].replace(":", "");
                time = time.replaceAll("^0+", "");
                int loc_time;
                if (time.equals("")) loc_time = 0;
                else loc_time = Integer.parseInt(time);

                if (times.get(i)[0] < loc_time) {
                    return false;
                }
            }

            if (times.get(i)[1] != -1) {

                String time = loc.open_hours.get(i)[1].replace(":", "");
                time = time.replaceAll("^0+", "");
                int loc_time;
                if (time.equals("")) loc_time = 0;
                else loc_time = Integer.parseInt(time);

                if (times.get(i)[1] > loc_time) {
                    return false;
                }
            }
            if(openDays.get(i) && times.get(i)[0] == -1 && times.get(i)[1] == -1) {
                if(loc.open_hours.get(i)[0].equals("")) {
                    return false;
                }
            }
        }
        return true;
    }


    public void setPublicLocation(PublicLocation publicLocation) {
        this.publicLocation = publicLocation;
    }

    public ArrayList<PublicLocation> getMatchList() {
        return matchList;
    }

    public void setOpenDays(ArrayList<Boolean> openDays) {
        this.openDays = openDays;
    }

    public void removeListeners() {
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }


}
