package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hu.bme.aut.fitnessapp.MainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.location.ChooseLocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;


public class ChooseLocationItemDialogFragment extends DialogFragment implements ChooseLocationAdapter.LocationItemSelectedListener{

    private ChooseLocationAdapter adapter;
    private LocationListDatabase database;
    private SharedPreferences sharedPreferences;
    private TextView noLocationTV;

    private ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener listener;
    public interface ChooseLocationItemDialogListener {
        void onLocationItemChosen();
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
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.LocationRecyclerView);
        adapter = new ChooseLocationAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_location_item, null);
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.WORKOUT, Context.MODE_PRIVATE);
        noLocationTV = (TextView) contentView.findViewById(R.id.noLocationTextView);
        database = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                LocationListDatabase.class,
                "locations"
        ).build();

        initRecyclerView(contentView);

        return contentView;
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<LocationItem>>() {

            @Override
            protected List<LocationItem> doInBackground(Void... voids) {
                return database.locationItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<LocationItem> locationItems) {
                adapter.update(locationItems);
                if(locationItems.isEmpty()){
                    noLocationTV.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }


    @Override
    public void onItemSelected(LocationItem item, int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Location", item.location_id);
        editor.apply();
        listener.onLocationItemChosen();
        dismiss();
    }
}