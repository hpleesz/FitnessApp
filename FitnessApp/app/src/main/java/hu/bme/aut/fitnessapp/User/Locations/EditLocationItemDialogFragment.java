package hu.bme.aut.fitnessapp.User.Locations;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.Location;

public class EditLocationItemDialogFragment extends NewLocationItemDialogFragment implements EquipmentAdapter.OnCheckBoxClicked {

    private Location item;

    private ArrayList<Equipment> equipmentList;

    public static final String TAG = "EditLocationItemDialogFragment";
    private EditLocationItemDialogListener listener;


    public interface EditLocationItemDialogListener {
        void onLocationItemUpdated(Location newItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity instanceof EditLocationItemDialogListener) {
            listener = (EditLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the EditLocationItemDialogListener interface!");
        }

        item = (Location) getArguments().getSerializable("Item");

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
                            listener.onLocationItemUpdated(getLocationItem());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    @Override
    public View getContentView() {
        super.getContentView();
        TextView title = (TextView) getView().findViewById(R.id.locationFragmentTitle);
        title.setText(R.string.edit_location);
        getNameEditText().setText(item.name);

        return getView();
    }

    @Override
    public void initRecyclerView(View rootView) {
        super.initRecyclerView(rootView);
        getAdapter().setCheckedEquipmentList(item.equipment);

    }

    private Location getLocationItem() {
        Location location = new Location();
        location.id = item.id;
        location.name = getNameEditText().getText().toString();
        location.equipment = getAdapter().getCheckedEquipmentList();

        return location;
    }


}
