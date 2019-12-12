package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.controllers.user.locations.PublicLocationSearchMatchDialogFragment;
import hu.bme.aut.fitnessapp.models.user_models.location_models.PublicSearchMatchModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationsUnitTest {
    private PublicLocationSearchMatchDialogFragment publicLocationSearchMatchDialogFragment;
    private PublicSearchMatchModel publicSearchMatchModel;

    @Before
    public void initLocations() {
        publicLocationSearchMatchDialogFragment = new PublicLocationSearchMatchDialogFragment();
        publicSearchMatchModel = new PublicSearchMatchModel(publicLocationSearchMatchDialogFragment, new PublicLocation(), new ArrayList<Boolean>());
    }

    @Test
    public void convertTimesToIntTest() {
        ArrayList<String[]> open_hours = new ArrayList<>();
        open_hours.add(new String[]{"08:00", "17:00"});
        open_hours.add(new String[]{"", ""});

        publicSearchMatchModel.setPublicLocation(new PublicLocation(0, "Name", new ArrayList<Integer>(), open_hours,
                "Description", "Zip", "Country", "City", "Address", "Creator"));

        List<Integer[]> times = publicSearchMatchModel.convertTimesToInt();

        ArrayList<Integer[]> converted = new ArrayList<>();
        converted.add(new Integer[]{800, 1700});
        converted.add(new Integer[]{-1, -1});

        assertEquals(converted.get(0)[0], times.get(0)[0]);
        assertEquals(converted.get(0)[1], times.get(0)[1]);
        assertEquals(converted.get(1)[0], times.get(1)[0]);
        assertEquals(converted.get(1)[1], times.get(1)[1]);

    }

    @Test
    public void locationDetailsMatchTest() {
        publicSearchMatchModel.setPublicLocation(new PublicLocation(0, "Name", new ArrayList<Integer>(),
                new ArrayList<String[]>(), "Description", "Zip", "Country", "City", "Address",
                "Creator"));

        PublicLocation location = new PublicLocation(0, "Name", new ArrayList<Integer>(), new ArrayList<String[]>(),
                "Description", "Zip", "Country", "City", "Address", "Creator");

        boolean val = publicSearchMatchModel.locationDetailsMatch(location);
        assertTrue(val);

        publicSearchMatchModel.setPublicLocation(new PublicLocation(0, "", new ArrayList<Integer>(),
                new ArrayList<String[]>(), "", "", "", "", "Address",
                ""));

        val = publicSearchMatchModel.locationDetailsMatch(location);
        assertTrue(val);


        publicSearchMatchModel.setPublicLocation(new PublicLocation(0, "", new ArrayList<Integer>(),
                new ArrayList<String[]>(), "Description2", "Zip2", "", "City", "Address",
                "Creator"));

        val = publicSearchMatchModel.locationDetailsMatch(location);
        assertFalse(val);
    }

    @Test
    public void compareTimesTest() {
        ArrayList<Integer[]> converted = new ArrayList<>();
        converted.add(new Integer[]{800, 1700});
        converted.add(new Integer[]{-1, -1});

        ArrayList<String[]> open_hours = new ArrayList<>();
        open_hours.add(new String[]{"08:00", "17:00"});
        open_hours.add(new String[]{"", ""});

        PublicLocation loc = new PublicLocation(0, "Name", new ArrayList<Integer>(), open_hours,
                "Description", "Zip", "Country", "City", "Address", "Creator");

        ArrayList<Boolean> openDays = new ArrayList<>();
        openDays.add(true);
        openDays.add(false);

        publicSearchMatchModel.setOpenDays(openDays);

        boolean val = publicSearchMatchModel.compareTimes(converted, loc);

        assertTrue(val);

        converted.set(0, new Integer[]{800, 1800});

        val = publicSearchMatchModel.compareTimes(converted, loc);

        assertFalse(val);

        converted.set(0, new Integer[]{800, 1600});
        openDays.set(1, true);

        publicSearchMatchModel.setOpenDays(openDays);

        val = publicSearchMatchModel.compareTimes(converted, loc);

        assertFalse(val);

    }

}


