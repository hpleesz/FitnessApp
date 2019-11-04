package hu.bme.aut.fitnessapp.User.Workout;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.ChooseLocationAdapter;
import hu.bme.aut.fitnessapp.Adapters.ChoosePublicLocationAdapter;
import hu.bme.aut.fitnessapp.Models.Location;
import hu.bme.aut.fitnessapp.Models.PublicLocation;
import hu.bme.aut.fitnessapp.Models.UserPublicLocation;


public class ChooseLocationItemDialogFragment extends DialogFragment implements //ChooseLocationAdapter.LocationItemSelectedListener
        ChoosePublicLocationAdapter.LocationItemSelectedListener, ChooseLocationAdapter.LocationItemSelectedListener {

    private ChoosePublicLocationAdapter adapter;
    private ChooseLocationAdapter adapter2;
    private SharedPreferences sharedPreferences;
    private TextView noLocationTV;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private PublicLocation item;

    private ArrayList<PublicLocation> itemList;
    private ArrayList<Location> locations;
    private ArrayList<UserPublicLocation> publicIDs;


    private ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener listener;
    private ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener listener2;


    @Override
    public void onItemSelected(PublicLocation item, int position) {
        listener.onLocationItemChosen(item);
        dismiss();
    }

    @Override
    public void onItemSelected(Location item, int position) {
        listener2.onLocationItemChosen(item);
        dismiss();
    }

    public interface ChooseLocationItemDialogListener {
        void onLocationItemChosen(PublicLocation item);
    }

    public interface ChooseOwnLocationItemDialogListener {
        void onLocationItemChosen(Location item);
    }

    public static final String TAG = "ChooseLocationItemDialogFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener) {
            listener = (ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the ChooseLocationItemDialogListener interface!");
        }
        if (activity instanceof ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener) {
            listener2 = (ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the ChooseOwnLocationItemDialogListener interface!");
        }

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



    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_location_item, null);
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.WORKOUT, Context.MODE_PRIVATE);
        noLocationTV = (TextView) contentView.findViewById(R.id.noLocationTextView);

        loadList(contentView);
        loadList2(contentView);

        return contentView;
    }

    private void loadList(final View contentView) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locations = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    Location location = new Location(id, name, equipment);

                    locations.add(location);


                }
                initRecyclerView(contentView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    private void loadList2(final View contentView) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publicIDs = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    UserPublicLocation loc = new UserPublicLocation();
                    loc.gym_id = dataSnapshot1.getValue(String.class);
                    loc.id = Integer.parseInt(dataSnapshot1.getKey());
                    publicIDs.add(loc);
                }

                loadGyms(contentView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("User_Public_Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    private  void loadGyms(final View contentView) {
        itemList = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            initRecyclerView2(contentView);
        }
        for(UserPublicLocation loc: publicIDs) {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long id = Long.parseLong(dataSnapshot.getKey());
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String description = dataSnapshot.child("Description").getValue(String.class);
                    String zip = dataSnapshot.child("Zip").getValue(String.class);
                    String country = dataSnapshot.child("Country").getValue(String.class);
                    String city = dataSnapshot.child("City").getValue(String.class);
                    String address = dataSnapshot.child("Address").getValue(String.class);

                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    ArrayList<String[]> hours = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot.child("Open_Hours").getChildren()) {
                        String[] open_close = new String[2];

                        for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            open_close[idx] = hour;
                        }
                        hours.add(open_close);
                    }

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    int idx = -1;
                    for(int i = 0; i < itemList.size(); i++) {
                        if(itemList.get(i).id == location.id) {
                            idx = i;
                            break;
                        }
                    }
                    if(idx > -1) {
                        itemList.set(idx, location);
                    }
                    else {
                        itemList.add(location);
                    }


                    initRecyclerView2(contentView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }

            };
            databaseReference.child("Public_Locations").child(loc.gym_id).addValueEventListener(eventListener);

        }

    }

    private void initRecyclerView2(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.PublicLocationRecyclerView);
        adapter = new ChoosePublicLocationAdapter(this, itemList);
        //loadItemsInBackground();
        if(itemList.isEmpty() && locations.isEmpty()) {
            noLocationTV.setVisibility(View.VISIBLE);
        }
        else {
            noLocationTV.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.LocationRecyclerView);
        adapter2 = new ChooseLocationAdapter(this, locations);
        if(locations.isEmpty() && itemList.isEmpty()) {
            noLocationTV.setVisibility(View.VISIBLE);
        }
        else {
            noLocationTV.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter2);
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


        if (locationItems.isEmpty()) {
            noLocationTV.setVisibility(View.VISIBLE);
        }
    }

     */
}