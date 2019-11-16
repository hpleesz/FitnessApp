package hu.bme.aut.fitnessapp.Models.User.Locations;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class MapModel {

    private PublicLocation publicLocation;
    private Context activity;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public MapModel(Context activity, PublicLocation publicLocation) {
        this.activity = activity;
        this.publicLocation = publicLocation;

        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity);

    }

    public Address getAddress() {
        String address = publicLocation.country + ", " + publicLocation.zip + ", " + publicLocation.city + ", " + publicLocation.address;
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
