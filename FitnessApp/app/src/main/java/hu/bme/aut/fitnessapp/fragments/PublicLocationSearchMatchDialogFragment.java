package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.MainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.location.ChooseLocationAdapter;
import hu.bme.aut.fitnessapp.data.location.ChoosePublicLocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;
import hu.bme.aut.fitnessapp.data.location.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;

public class PublicLocationSearchMatchDialogFragment extends DialogFragment implements ChoosePublicLocationAdapter.LocationItemSelectedListener {

    private ChoosePublicLocationAdapter adapter;

    private PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener listener;


    @Override
    public void onItemSelected(PublicLocation item, int position) {
        listener.onLocationItemChosen(item);
    }

    public interface ChooseLocationItemDialogListener {
        void onLocationItemChosen(PublicLocation location);
    }

    public static final String TAG = "PublicLocationSearchMatchDialogFragment";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private PublicLocation item;

    private ArrayList<PublicLocation> itemList;
    private ArrayList<PublicLocation> matchList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) {
            listener = (PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the PublicLocationSearchMatchDialogFragment interface!");
        }

        item = (PublicLocation) getArguments().getSerializable("Location");


        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private void initRecyclerView(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.PublicLocationRecyclerView);
        adapter = new ChoosePublicLocationAdapter(this, matchList);
        //loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_public_location_search_match, null);
        //contentView.setClipToOutline(true);
        //checkBox = contentView.findViewById(R.id.CheckBox);

        final FragmentActivity activity = getActivity();
        if (activity instanceof PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) {
            listener = (PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewLocationItemDialogListener interface!");
        }

        loadList(contentView);


        return contentView;
    }


    private void loadList(final View contentview) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    long id = Long.parseLong(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    String description = dataSnapshot1.child("Description").getValue(String.class);
                    String zip = dataSnapshot1.child("Zip").getValue(String.class);
                    String country = dataSnapshot1.child("Country").getValue(String.class);
                    String city = dataSnapshot1.child("City").getValue(String.class);
                    String address = dataSnapshot1.child("Address").getValue(String.class);

                    ArrayList<Integer> equipment = new ArrayList<>();

                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    ArrayList<String[]> hours = new ArrayList<>();

                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Open_Hours").getChildren()) {
                        String[] open_close = new String[2];

                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            open_close[idx] = hour;
                        }
                        hours.add(open_close);
                    }

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    itemList.add(location);

                }

                findMatch();
                initRecyclerView(contentview);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Public_Locations").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void findMatch() {
        matchList = new ArrayList<>();

        ArrayList<Integer[]> times = new ArrayList<>();
        for (int i = 0; i < item.open_hours.size(); i++) {
            Integer[] open_close = new Integer[2];

            for (int j = 0; j < 2; j++) {
                if (!item.open_hours.get(i)[j].equals("")) {
                    String time = item.open_hours.get(i)[j].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    if (time.equals("")) open_close[j] = 0;
                    else open_close[j] = Integer.parseInt(time);
                } else {
                    open_close[j] = -1;
                }
            }
            times.add(open_close);
        }


        for (PublicLocation loc : itemList) {
            //ha van string és

            boolean match = true;

            if (!((item.name.equals("") || loc.name.equals(item.name)) &&
                    (item.description.equals("") || loc.description.equals(item.description)) &&
                    (item.zip.equals("") || loc.zip.equals(item.zip)) &&
                    (item.country.equals("") || loc.country.equals(item.country)) &&
                    (item.city.equals("") || loc.city.equals(item.city)) &&
                    (item.address.equals("") || loc.address.equals(item.address)))) {
                match = false;
                continue;
            }

            for (int i = 0; i < times.size(); i++) {
                if (times.get(i)[0] != -1) {

                    String time = loc.open_hours.get(i)[0].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    int loc_time;
                    if (time.equals("")) loc_time = 0;
                    else loc_time = Integer.parseInt(time);

                    if (times.get(i)[0] < loc_time) {
                        match = false;
                        break;
                    }
                }

                if (times.get(i)[1] != -1) {

                    String time = loc.open_hours.get(i)[1].replace(":", "");
                    time = time.replaceAll("^0+", "");
                    int loc_time;
                    if (time.equals("")) loc_time = 0;
                    else loc_time = Integer.parseInt(time);

                    if (times.get(i)[1] > loc_time) {
                        match = false;
                        break;
                    }
                }
            }
            for(Integer item : item.equipment) {
                if(!loc.equipment.contains(item)) {
                    match = false;
                    break;
                }

            }
            if(match) {
                matchList.add(loc);
            }
        }
            /*
            if( (!item.name.equals("") && !loc.name.equals(item.name)) || (!item.description.equals("") && !loc.description.equals(item.description)) || (!item.country.equals("") && !loc.country.equals(item.country))
            || (!item.city.equals("") && !loc.city.equals(item.city)) || (!item.address.equals("") && !loc.address.equals(item.address)) || (!item.zip.equals("") && !loc.zip.equals(item.zip))) {
                    match = false;
            }
            else {
                matchList.add(loc);
            }
            */


    }

    /*
    @Override
    public void onItemSelected(LocationItem item, int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Location", item.location_id);
        editor.putString("Location Name", item.location_name);
        editor.apply();
        listener.onLocationItemChosen();
        dismiss();
    }

     */


}
