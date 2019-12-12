package hu.bme.aut.fitnessapp.controllers.user.locations;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

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
import java.util.List;

import hu.bme.aut.fitnessapp.models.user_models.location_models.LocationItemModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Location;

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

    }

    @Override
    public void onStart() {
        super.onStart();
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
                        } else if (getLocationItem().getEquipment().isEmpty()) {
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


    public void initRecyclerView(List<Equipment> equipmentList) {
        RecyclerView recyclerView = view.findViewById(R.id.EquipmentRecyclerView);
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

    public Location getLocationItem() {
        Location location = new Location();
        location.setId(0);
        location.setName(nameEditText.getText().toString());
        location.setEquipment(adapter.getCheckedEquipmentList());

        return location;
    }

    @Override
    public void onStop() {
        super.onStop();
        locationItemModel.removeListeners();
    }


}
