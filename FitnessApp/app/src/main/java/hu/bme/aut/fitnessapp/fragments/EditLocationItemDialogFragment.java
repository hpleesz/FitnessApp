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

import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;

public class EditLocationItemDialogFragment extends DialogFragment implements EquipmentAdapter.OnCheckBoxClicked{

    private int position;
    private LocationItem item;

    private EquipmentAdapter adapter;
    private LocationAdapter adapter_location;
    private EquipmentListDatabase database;

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
        void onLocationItemUpdated(LocationItem newItem);
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
        position = getArguments().getInt("Position");
        item = (LocationItem) getArguments().getSerializable("Item");
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
        adapter = new EquipmentAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_location_item, null);
        TextView title = (TextView)contentView.findViewById(R.id.locationFragmentTitle);
        title.setText(R.string.edit_location);
        nameEditText = contentView.findViewById(R.id.LocationNameEditText);
        nameEditText.setText(item.location_name);

        final FragmentActivity activity = getActivity();
        if (activity instanceof EditLocationItemDialogListener) {
            listener = (EditLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewProductsItemDialogListener interface!");
        }


        database = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                EquipmentListDatabase.class,
                "equipments"
        ).build();

        initRecyclerView(contentView);


        return contentView;
    }


    private boolean isValid() {
        return nameEditText.getText().length() > 0;
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                return database.equipmentItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<EquipmentItem> equipmentItemList) {
                adapter.update(equipmentItemList);
                adapter.setCheckedEquipmentList(item.location_equipmentItems);

            }
        }.execute();
    }

    private LocationItem getLocationItem() {
        LocationItem locationItem = new LocationItem();
        locationItem.location_name = nameEditText.getText().toString();
        locationItem.location_id = item.location_id;
        locationItem.location_equipmentItems = adapter.getCheckedEquipmentList();

        return locationItem;
    }




}
