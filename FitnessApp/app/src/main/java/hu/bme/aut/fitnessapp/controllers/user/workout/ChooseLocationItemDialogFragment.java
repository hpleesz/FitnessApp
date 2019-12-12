package hu.bme.aut.fitnessapp.controllers.user.workout;

import android.app.Dialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hu.bme.aut.fitnessapp.models.user_models.workout_models.ChooseLocationItemModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.ChooseLocationAdapter;
import hu.bme.aut.fitnessapp.controllers.adapters.ChoosePublicLocationAdapter;
import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.PublicLocation;


public class ChooseLocationItemDialogFragment extends DialogFragment implements //ChooseLocationAdapter.LocationItemSelectedListener
        ChoosePublicLocationAdapter.LocationItemSelectedListener, ChooseLocationAdapter.LocationItemSelectedListener, ChooseLocationItemModel.PublicLocationsLoaded,
        ChooseLocationItemModel.LocationsLoaded{

    private TextView noLocationTV;

    private ChooseLocationItemModel chooseLocationItemModel;

    private View contentView;

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

    @Override
    public void onLocationsLoaded() {
        initRecyclerView();
    }

    @Override
    public void onPublicLocationsLoaded() {
        initRecyclerView2();
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
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_location_item, null);
        noLocationTV = contentView.findViewById(R.id.noLocationTextView);

        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        chooseLocationItemModel = new ChooseLocationItemModel(this);
        chooseLocationItemModel.loadLocations();
        chooseLocationItemModel.loadPublicLocations();
    }

    public void initRecyclerView2() {
        RecyclerView recyclerView = contentView.findViewById(R.id.PublicLocationRecyclerView);
        ChoosePublicLocationAdapter adapter = new ChoosePublicLocationAdapter(this, chooseLocationItemModel.getItemList());
        if(chooseLocationItemModel.getItemList().isEmpty() && chooseLocationItemModel.getLocations() != null && chooseLocationItemModel.getLocations().isEmpty()) {
            noLocationTV.setVisibility(View.VISIBLE);
        }
        else {
            noLocationTV.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = contentView.findViewById(R.id.LocationRecyclerView);
        ChooseLocationAdapter adapter2 = new ChooseLocationAdapter(this, chooseLocationItemModel.getLocations());
        if(chooseLocationItemModel.getLocations().isEmpty() && chooseLocationItemModel.getItemList() != null && chooseLocationItemModel.getItemList().isEmpty()) {
            noLocationTV.setVisibility(View.VISIBLE);
        }
        else {
            noLocationTV.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter2);
    }

    @Override
    public void onStop() {
        super.onStop();
        chooseLocationItemModel.removeListeners();
    }
}