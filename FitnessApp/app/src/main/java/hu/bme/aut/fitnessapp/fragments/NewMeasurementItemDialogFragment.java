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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;

public class NewMeasurementItemDialogFragment extends DialogFragment {

    private EditText valueEditText;
    private DatePicker datePicker;
    private MeasurementDatabase database;
    private List<MeasurementItem> list;
    private int reg_day;
    private int reg_month;
    private int reg_year;
    private String alreadyExists;


    private ArrayList<EditText> editTexts;
    public static final String[] body_parts = {"Shoulders", "Chest", "Waist", "Hips", "Right Upper Arm", "Left Upper Arm", "Right Forearm", "Left Forearm", "Right Thigh", "Left Thigh", "Right Calf", "Left Calf"};


    public static final String TAG = "NewMeasurementDialogFragment";

    public interface NewMeasurementDialogListener {
        void onMeasurementItemsCreated(ArrayList<MeasurementItem> items);
    }

    private NewMeasurementItemDialogFragment.NewMeasurementDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof NewMeasurementItemDialogFragment.NewMeasurementDialogListener) {
            listener = (NewMeasurementItemDialogFragment.NewMeasurementDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewMeasurementItemDialogListener interface!");
        }

        alreadyExists = "";
        database = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                MeasurementDatabase.class,
                "measurements"
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
                        if(getMeasurementItems().size() == 0) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "No values entered.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if(alreadyExists(getMeasurementItems())) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), "Measurements for " + alreadyExists + " already entered for this date.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else{
                            ArrayList<MeasurementItem> items = getMeasurementItems();
                            listener.onMeasurementItemsCreated(items);

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_measurement, null);
        TextView title = (TextView)contentView.findViewById(R.id.measurementFragmentTitle);
        title.setText(R.string.new_entry);
        valueEditText = contentView.findViewById(R.id.weightValueEditText);
        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserActivity.USER, Context.MODE_PRIVATE);
        reg_day = sharedPreferences.getInt("Registration day", 0);
        reg_month = sharedPreferences.getInt("Registration month", 0);
        reg_year = sharedPreferences.getInt("Registration year", 0);
        Calendar c = Calendar.getInstance();
        c.set(reg_year, reg_month, reg_day);
        datePicker.setMinDate(c.getTimeInMillis());

        editTexts = new ArrayList<>();
        EditText shouldersEditText = (EditText) contentView.findViewById(R.id.ShouldersEditText);
        editTexts.add(shouldersEditText);
        EditText chestEditText = (EditText) contentView.findViewById(R.id.ChestEditText);
        editTexts.add(chestEditText);
        EditText waistEditText = (EditText) contentView.findViewById(R.id.WaistEditText);
        editTexts.add(waistEditText);
        EditText hipsEditText = (EditText) contentView.findViewById(R.id.HipsEditText);
        editTexts.add(hipsEditText);
        EditText rightUpperArmEditText = (EditText) contentView.findViewById(R.id.RightUpperArmEditText);
        editTexts.add(rightUpperArmEditText);
        EditText leftUpperArmEditText = (EditText) contentView.findViewById(R.id.LeftUpperArmEditText);
        editTexts.add(leftUpperArmEditText);
        EditText rightForearmEditText = (EditText) contentView.findViewById(R.id.RightForearmEditText);
        editTexts.add(rightForearmEditText);
        EditText leftForearmEditText = (EditText) contentView.findViewById(R.id.LeftForearmEditText);
        editTexts.add(leftForearmEditText);
        EditText rightThighEditText = (EditText) contentView.findViewById(R.id.RightThighEditText);
        editTexts.add(rightThighEditText);
        EditText leftThighEditText = (EditText) contentView.findViewById(R.id.LeftThighEditText);
        editTexts.add(leftThighEditText);
        EditText rightCalfEditText = (EditText) contentView.findViewById(R.id.RightCalfEditText);
        editTexts.add(rightCalfEditText);
        EditText leftCalfEditText = (EditText) contentView.findViewById(R.id.LeftCalfEditText);
        editTexts.add(leftCalfEditText);



        return contentView;
    }

    private ArrayList<MeasurementItem> getMeasurementItems() {
        ArrayList<MeasurementItem> newItems = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            MeasurementItem measurementItem = new MeasurementItem();
            measurementItem.measurement_day = datePicker.getDayOfMonth();
            measurementItem.measurement_month = datePicker.getMonth();
            measurementItem.measurement_year = datePicker.getYear();
            measurementItem.measurement_calculated = makeCalculatedMeasurement(measurementItem.measurement_year, measurementItem.measurement_month, measurementItem.measurement_day);
            measurementItem.body_part = body_parts[i];

            try {
                measurementItem.measurement_value = Double.parseDouble(editTexts.get(i).getText().toString());
            } catch (NumberFormatException f) {
                measurementItem.measurement_value = -1;
            }

            if(measurementItem.measurement_value != -1)
                newItems.add(measurementItem);
        }

        return newItems;
    }

    private void loadDatabase() {
        new AsyncTask<Void, Void, List<MeasurementItem>>() {

            @Override
            protected List<MeasurementItem> doInBackground(Void... voids) {
                list = database.measurementItemDao().getAll();
                return list;
            }

        }.execute();
    }


    public boolean alreadyExists(ArrayList<MeasurementItem> items){
        boolean exists = false;
        for(int j = 0; j < items.size(); j++) {
            for (int i = 0; i < list.size(); i++) {
                if ((items.get(j).measurement_calculated == list.get(i).measurement_calculated) && items.get(j).body_part.equals(list.get(i).body_part)) {
                    if (!alreadyExists.equals(""))
                        alreadyExists = alreadyExists + ", " + items.get(j).body_part;
                    else
                        alreadyExists = items.get(j).body_part;
                    exists = true;
                }
            }
        }
        return exists;
    }

    public int makeCalculatedMeasurement(int year, int fixedmonth, int day) {
        int calculated = year * 10000 + fixedmonth * 100 + day;
        return calculated;
    }
}
