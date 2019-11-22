package hu.bme.aut.fitnessapp.Controllers.User.Locations;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
/*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
*/
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.UserModels.LocationModels.LocationItemModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Location;

public class NewLocationItemDialogFragment extends DialogFragment implements EquipmentAdapter.OnCheckBoxClicked, LocationItemModel.ListLoadedListener {

    public EquipmentAdapter getAdapter() {
        return adapter;
    }

    private EquipmentAdapter adapter;

    public EditText getNameEditText() {
        return nameEditText;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    private View view;

    private EditText nameEditText;

    public static final String TAG = "NewLocationItemDialogFragment";

    private NewLocationItemDialogListener listener;

    public LocationItemModel getLocationItemModel() {
        return locationItemModel;
    }

    private LocationItemModel locationItemModel;

    @Override
    public void onChecked(int pos) {
        adapter.onChecked(pos);
    }

    @Override
    public void onUnchecked(int pos) {
        adapter.onUnchecked(pos);
    }

    @Override
    public void onListLoaded(ArrayList<Equipment> equipmentList) {
        initRecyclerView(equipmentList);
    }


    public interface NewLocationItemDialogListener {
        void onLocationItemCreated(Location newItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof NewLocationItemDialogListener) {
            listener = (NewLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewLocationItemDialogListener interface!");
        }

        locationItemModel = new LocationItemModel(this);
        locationItemModel.loadEquipment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!isValid()) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), R.string.no_name_entered, Toast.LENGTH_LONG);
                            toast.show();
                        } else if (getLocationItem().equipment.isEmpty()) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), R.string.no_equipment_selected, Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            listener.onLocationItemCreated(getLocationItem());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    public void initRecyclerView(ArrayList<Equipment> equipmentList) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.EquipmentRecyclerView);
        adapter = new EquipmentAdapter(this, equipmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    public View getContentView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_location_item, null);
        nameEditText = view.findViewById(R.id.LocationNameEditText);
        return view;
    }


    public boolean isValid() {
        return nameEditText.getText().length() > 0;
    }

    private Location getLocationItem() {
        Location location = new Location();
        location.id = 0;
        location.name = nameEditText.getText().toString();
        location.equipment = adapter.getCheckedEquipmentList();

        return location;
    }



}
