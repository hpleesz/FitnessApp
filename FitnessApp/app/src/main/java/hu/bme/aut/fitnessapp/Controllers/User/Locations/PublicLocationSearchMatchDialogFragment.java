package hu.bme.aut.fitnessapp.Controllers.User.Locations;

import android.app.Dialog;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.User.Locations.PublicSearchMatchModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.ChoosePublicLocationAdapter;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class PublicLocationSearchMatchDialogFragment extends DialogFragment implements ChoosePublicLocationAdapter.LocationItemSelectedListener, PublicSearchMatchModel.ListLoadedListener, PublicSearchMatchModel.NoMatchListener {

    private ChoosePublicLocationAdapter adapter;

    private PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener listener;

    private TextView noLocations;

    private View contentView;


    @Override
    public void onItemSelected(PublicLocation item, int position) {
        listener.onLocationItemChosen(item);
    }

    @Override
    public void onListLoaded() {
        initRecyclerView();
    }

    @Override
    public void onNoMatchFound() {
        noLocations.setVisibility(View.VISIBLE);
    }

    public interface ChooseLocationItemDialogListener {
        void onLocationItemChosen(PublicLocation location);
    }

    public static final String TAG = "PublicLocationSearchMatchDialogFragment";

    private PublicLocation item;

    private ArrayList<PublicLocation> itemList;
    private ArrayList<PublicLocation> matchList;

    private ArrayList<Boolean> openDays;

    private PublicSearchMatchModel publichSearchMatchModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) {
            listener = (PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the PublicLocationSearchMatchDialogFragment interface!");
        }

        item = (PublicLocation) getArguments().getSerializable("Location");
        openDays = (ArrayList<Boolean>) getArguments().getSerializable("Checkboxes");

        publichSearchMatchModel = new PublicSearchMatchModel(this, item, openDays);
        publichSearchMatchModel.initFirebase();
        publichSearchMatchModel.loadList();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.PublicLocationRecyclerView);
        adapter = new ChoosePublicLocationAdapter(this, publichSearchMatchModel.getMatchList());
        //loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private View getContentView() {
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_public_location_search_match, null);
        //contentView.setClipToOutline(true);
        //checkBox = contentView.findViewById(R.id.CheckBox);

        final FragmentActivity activity = getActivity();
        if (activity instanceof PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) {
            listener = (PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewLocationItemDialogListener interface!");
        }

        noLocations = contentView.findViewById(R.id.noLocationTextView);

        //loadList(contentView);


        return contentView;
    }



    public void setItem(PublicLocation item) {
        this.item = item;
    }


    public void setOpenDays(ArrayList<Boolean> openDays) {
        this.openDays = openDays;
    }


}
