package hu.bme.aut.fitnessapp.models.user_models.location_models;

import androidx.fragment.app.DialogFragment;


import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;

public class PublicSearchMatchModel implements LoadPublicLocations.PublicLocationsLoadedListener{

    private PublicLocation publicLocation;
    private List<Boolean> openDays;
    private ArrayList<PublicLocation> itemList;
    private ArrayList<PublicLocation> matchList;
    private LoadPublicLocations loadPublicLocations;

    private PublicSearchMatchModel.ListLoadedListener listLoadedListener;
    private PublicSearchMatchModel.NoMatchListener noMatchListener;


    public interface ListLoadedListener {
        void onListLoaded();
    }

    public interface NoMatchListener {
        void onNoMatchFound();
    }


    public PublicSearchMatchModel(DialogFragment fragment, PublicLocation publicLocation, List<Boolean> openDays) {
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

        List<Integer[]> times = convertTimesToInt();


        for (PublicLocation loc : itemList) {

            boolean match = true;

            if (!locationDetailsMatch(loc)) {
                continue;
            }

            if(!compareTimes(times, loc)) {
                continue;
            }

            for(Integer item : publicLocation.getEquipment()) {
                if(!loc.getEquipment().contains(item)) {
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

    public List<Integer[]> convertTimesToInt() {
        ArrayList<Integer[]> times = new ArrayList<>();
        for (int i = 0; i < publicLocation.getOpenHours().size(); i++) {
            Integer[] openClose = new Integer[2];

            for (int j = 0; j < 2; j++) {
                if (!publicLocation.getOpenHours().get(i)[j].equals("")) {
                    String time = publicLocation.getOpenHours().get(i)[j].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    if (time.equals("")) openClose[j] = 0;
                    else openClose[j] = Integer.parseInt(time);
                } else {
                    openClose[j] = -1;
                }
            }
            times.add(openClose);
        }
        return times;
    }


    public boolean locationDetailsMatch(PublicLocation loc) {
        return ((publicLocation.getName().equals("") || loc.getName().equals(publicLocation.getName())) &&
                (publicLocation.getDescription().equals("") || loc.getDescription().equals(publicLocation.getDescription())) &&
                (publicLocation.getZip().equals("") || loc.getZip().equals(publicLocation.getZip())) &&
                (publicLocation.getCountry().equals("") || loc.getCountry().equals(publicLocation.getCountry())) &&
                (publicLocation.getCity().equals("") || loc.getCity().equals(publicLocation.getCity())) &&
                (publicLocation.getAddress().equals("") || loc.getAddress().equals(publicLocation.getAddress())));
    }

    public boolean compareTimes(List<Integer[]> times, PublicLocation loc) {
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i)[0] != -1) {

                String time = loc.getOpenHours().get(i)[0].replace(":", "");
                time = time.replaceAll("^0+", "");
                int locTime;
                if (time.equals("")) locTime = 0;
                else locTime = Integer.parseInt(time);

                if (times.get(i)[0] < locTime) {
                    return false;
                }
            }

            if (times.get(i)[1] != -1) {

                String time = loc.getOpenHours().get(i)[1].replace(":", "");
                time = time.replaceAll("^0+", "");
                int locTime;
                if (time.equals("")) locTime = 0;
                else locTime = Integer.parseInt(time);

                if (times.get(i)[1] > locTime) {
                    return false;
                }
            }
            if(openDays.get(i) && times.get(i)[0] == -1 && times.get(i)[1] == -1) {
                if(loc.getOpenHours().get(i)[0].equals("")) {
                    return false;
                }
            }
        }
        return true;
    }


    public void setPublicLocation(PublicLocation publicLocation) {
        this.publicLocation = publicLocation;
    }

    public List<PublicLocation> getMatchList() {
        return matchList;
    }

    public void setOpenDays(List<Boolean> openDays) {
        this.openDays = openDays;
    }

    public void removeListeners() {
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }


}
