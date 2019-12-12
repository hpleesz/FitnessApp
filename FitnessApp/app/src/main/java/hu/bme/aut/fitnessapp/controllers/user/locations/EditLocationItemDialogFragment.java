package hu.bme.aut.fitnessapp.controllers.user.locations;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Location;

public class EditLocationItemDialogFragment extends NewLocationItemDialogFragment implements EquipmentAdapter.OnCheckBoxClicked {

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

        Location item = (Location) getArguments().getSerializable("Item");
        getLocationItemModel().setLocation(item);
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
        TextView title = getView().findViewById(R.id.locationFragmentTitle);
        title.setText(R.string.edit_location);
        getNameEditText().setText(getLocationItemModel().getLocation().getName());

        return getView();
    }

    @Override
    public void initRecyclerView(List<Equipment> equipmentList) {
        super.initRecyclerView(equipmentList);
        getAdapter().setCheckedEquipmentList(getLocationItemModel().getLocation().getEquipment());

    }

    @Override
    public Location getLocationItem() {
        Location location = new Location();
        location.setId(getLocationItemModel().getLocation().getId());
        location.setName(getNameEditText().getText().toString());
        location.setEquipment(getAdapter().getCheckedEquipmentList());

        return location;
    }


}