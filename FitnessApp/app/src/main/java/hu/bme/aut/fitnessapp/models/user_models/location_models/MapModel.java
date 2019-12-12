package hu.bme.aut.fitnessapp.models.user_models.location_models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class MapModel {

    private PublicLocation publicLocation;
    private Context activity;

    public MapModel(Context activity, PublicLocation publicLocation) {
        this.activity = activity;
        this.publicLocation = publicLocation;

    }

    public Address getAddress() {
        String address = publicLocation.getCountry() + ", " + publicLocation.getZip() + ", " + publicLocation.getCity() + ", " + publicLocation.getAddress();
        return getLocationFromAddress(address);
    }

    public Address getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }

            return address.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public PublicLocation getPublicLocation() {
        return publicLocation;
    }
}
