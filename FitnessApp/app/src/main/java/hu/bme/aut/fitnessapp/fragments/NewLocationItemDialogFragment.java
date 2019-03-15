package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseListDatabase;
import hu.bme.aut.fitnessapp.data.location.LocationItem;

public class NewLocationItemDialogFragment extends DialogFragment implements EquipmentAdapter.OnCheckBoxClicked {

    private EquipmentAdapter adapter;
    private EquipmentListDatabase database;

    private EditText nameEditText;

    public static final String TAG = "NewLocationItemDialogFragment";
    private NewLocationItemDialogListener listener;

    @Override
    public void onChecked(int pos) {
        adapter.onChecked(pos);
    }

    @Override
    public void onUnchecked(int pos) {
        adapter.onUnchecked(pos);
    }


    public interface NewLocationItemDialogListener {
        void onLocationItemCreated(LocationItem newItem);
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
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "No name entered.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if (getLocationItem().location_equipmentItems.isEmpty()){
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "Please select available equipment. If you don't have any equipment, select 'No equipment'.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            listener.onLocationItemCreated(getLocationItem());
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
        //contentView.setClipToOutline(true);
        nameEditText = contentView.findViewById(R.id.LocationNameEditText);
        //checkBox = contentView.findViewById(R.id.CheckBox);

        final FragmentActivity activity = getActivity();
        if (activity instanceof NewLocationItemDialogListener) {
            listener = (NewLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewLocationItemDialogListener interface!");
        }

        initializeDatabase();
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
            }
        }.execute();
    }

    private LocationItem getLocationItem() {
        LocationItem locationItem = new LocationItem();
        locationItem.location_name = nameEditText.getText().toString();
        locationItem.location_equipmentItems = adapter.getCheckedEquipmentList();

        return locationItem;
    }

    public void initializeDatabase() {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate (SupportSQLiteDatabase db) {
                final ArrayList<EquipmentItem> list = fillEquipmentList();

                new AsyncTask<Void, Void, List<EquipmentItem>>() {

                    @Override
                    protected List<EquipmentItem> doInBackground(Void... voids) {
                        for(int i = 0; i < list.size(); i++){
                            database.equipmentItemDao().insert(list.get(i));
                        }
                        return list;
                    }

                    @Override
                    protected void onPostExecute(List<EquipmentItem> equipmentItems) {
                        adapter.update(equipmentItems);
                    }
                }.execute();
            }
        };

        database = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                EquipmentListDatabase.class,
                "equipments"
        ).addCallback(rdc).build();


    }

    private ArrayList<EquipmentItem> fillEquipmentList() {

        Resources resources = getResources();
        String str;
        ArrayList<EquipmentItem> equipmentItems = new ArrayList<>();

        int resID = resources.getIdentifier("hu.bme.aut.fitnessapp:raw/" + "equipments", null, null);
        InputStream is = resources.openRawResource(resID);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int id = 1;
            while ((str = br.readLine()) != null) {
                final EquipmentItem newItem = new EquipmentItem();
                newItem.equipment_name = str;
                newItem.equipment_id = id;
                equipmentItems.add(newItem);
                id++;
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return equipmentItems;
    }


}
