package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.models.Equipment;
import hu.bme.aut.fitnessapp.models.Location;

public class EditLocationItemDialogFragment extends DialogFragment implements EquipmentAdapter.OnCheckBoxClicked {

    private Location item;

    private EquipmentAdapter adapter;
    private LocationAdapter adapter_location;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private ArrayList<Equipment> equipmentList;
    private EditText nameEditText;

    public static final String TAG = "EditLocationItemDialogFragment";
    private EditLocationItemDialogListener listener;

    @Override
    public void onChecked(int pos) {
        adapter.onChecked(pos);
    }

    @Override
    public void onUnchecked(int pos) {
        adapter.onUnchecked(pos);
    }


    public interface EditLocationItemDialogListener {
        void onLocationItemUpdated(Location newItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof EditLocationItemDialogListener) {
            listener = (EditLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the EditLocationItemDialogListener interface!");
        }
        int position = getArguments().getInt("Position");
        item = (Location) getArguments().getSerializable("Item");

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isValid()) {
                            listener.onLocationItemUpdated(getLocationItem());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    private void initRecyclerView(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.EquipmentRecyclerView);
        adapter = new EquipmentAdapter(this, equipmentList);
        //loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_location_item, null);
        TextView title = (TextView) contentView.findViewById(R.id.locationFragmentTitle);
        title.setText(R.string.edit_location);
        nameEditText = contentView.findViewById(R.id.LocationNameEditText);
        nameEditText.setText(item.name);

        final FragmentActivity activity = getActivity();
        if (activity instanceof EditLocationItemDialogListener) {
            listener = (EditLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewProductsItemDialogListener interface!");
        }
        loadEquipment(contentView);
        //initRecyclerView(contentView);


        return contentView;
    }


    private boolean isValid() {
        return nameEditText.getText().length() > 0;
    }

    private void loadEquipment(final View contentview) {

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    equipmentList = new ArrayList<>();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        int id = Integer.parseInt(dataSnapshot1.getKey());
                        String name = (String) dataSnapshot1.getValue();
                        Equipment equipment = new Equipment(id, name);
                        equipmentList.add(equipment);
                    }
                    initRecyclerView(contentview);
                    adapter.setCheckedEquipmentList(item.equipment);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }

            };
            databaseReference.child("Equipment").addValueEventListener(eventListener);


            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
            //this.eventListener = eventListener

    }

    private Location getLocationItem() {
        Location location = new Location();
        location.id = item.id;
        location.name = nameEditText.getText().toString();
        location.equipment = adapter.getCheckedEquipmentList();

        return location;
    }


}
