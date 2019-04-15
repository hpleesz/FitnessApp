package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.WeightActivity;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;

public class NewWeightItemDialogFragment extends DialogFragment {

    private EditText valueEditText;
    private DatePicker datePicker;
    private WeightListDatabase database;
    private List<WeightItem> list;
    private int reg_day;
    private int reg_month;
    private int reg_year;

    public static final String TAG = "NewWeightDialogFragment";

    public interface NewWeightDialogListener {
        void onWeightItemCreated(WeightItem item);
    }

    private NewWeightItemDialogFragment.NewWeightDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof NewWeightItemDialogFragment.NewWeightDialogListener) {
            listener = (NewWeightItemDialogFragment.NewWeightDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewWeightItemDialogListener interface!");
        }

        database = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                WeightListDatabase.class,
                "weights"
        ).build();

        loadDatabase();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar c = Calendar.getInstance();
                        if(getWeightItem().weight_value == -1) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "No weight entered.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if(alreadyExists(getWeightItem()) || (c.get(Calendar.YEAR) == reg_year && c.get(Calendar.MONTH) == reg_month && c.get(Calendar.DATE) == reg_day)){
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "Already entered weight for selected day!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else
                            listener.onWeightItemCreated(getWeightItem());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_weight, null);
        TextView title = (TextView)contentView.findViewById(R.id.weightFragmentTitle);
        title.setText(R.string.new_entry);
        valueEditText = contentView.findViewById(R.id.weightValueEditText);
        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserActivity.USER, Context.MODE_PRIVATE);
        reg_day = sharedPreferences.getInt("Registration day", 0);
        reg_month = sharedPreferences.getInt("Registration month", 0);
        reg_year = sharedPreferences.getInt("Registration year", 0);
        Calendar c = Calendar.getInstance();
        //c.set(reg_year, reg_month-1, reg_day);
        c.set(reg_year, reg_month, reg_day);
        datePicker.setMinDate(c.getTimeInMillis());


        return contentView;
    }

    private WeightItem getWeightItem() {
        WeightItem weightItem = new WeightItem();
        try {
            weightItem.weight_value = Double.parseDouble(valueEditText.getText().toString());
        } catch (NumberFormatException f) {
            weightItem.weight_value = -1;
        }

        weightItem.weight_day = datePicker.getDayOfMonth();
        //weightItem.weight_month = datePicker.getMonth() +1;
        weightItem.weight_month = datePicker.getMonth();
        weightItem.weight_year = datePicker.getYear();
        //weightItem.weight_calculated = weightItem.weight_year * 10000 + weightItem.weight_month * 100 + weightItem.weight_day;
        weightItem.weight_calculated = makeCalculatedWeight(weightItem.weight_year, weightItem.weight_month, weightItem.weight_day);
        return weightItem;
    }

    private void loadDatabase() {
        new AsyncTask<Void, Void, List<WeightItem>>() {

            @Override
            protected List<WeightItem> doInBackground(Void... voids) {
                list = database.weightItemDao().getAll();
                return list;
            }
        }.execute();
    }

    public boolean alreadyExists(WeightItem item){
        for(int i = 0; i < list.size(); i++){
            if(item.weight_calculated == list.get(i).weight_calculated)
                return true;
        }
        return false;
    }

    public int makeCalculatedWeight(int year, int fixedmonth, int day) {
        int calculated = year * 10000 + fixedmonth * 100 + day;
        return calculated;
    }
}
