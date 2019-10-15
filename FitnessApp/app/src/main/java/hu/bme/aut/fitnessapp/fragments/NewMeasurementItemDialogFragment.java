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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.models.Weight;

public class NewMeasurementItemDialogFragment extends DialogFragment {

    private DatePicker datePicker;
    private MeasurementDatabase database;
    private List<MeasurementItem> list;
    private String alreadyExists;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private String date;

    private HashMap<String, Double> new_entries;


    private ArrayList<EditText> editTexts;
    //public static final String[] body_parts = {"Shoulders", "Chest", "Waist", "Hips", "Right Upper Arm", "Left Upper Arm", "Right Forearm", "Left Forearm", "Right Thigh", "Left Thigh", "Right Calf", "Left Calf"};
    public ArrayList<String> body_parts;

    private ArrayList<ArrayList<Weight>> entries;

    public static final String TAG = "NewMeasurementDialogFragment";

    public interface NewMeasurementDialogListener {
        void onMeasurementItemsCreated(HashMap<String, Double> new_entries, String date);
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
        entries = new ArrayList<>();
        //loadDatabase();
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
                        if (getMeasurementItems().size() == 0) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), R.string.no_values_entered, Toast.LENGTH_LONG);
                            toast.show();
                        } else if (alreadyExists(getMeasurementItems())) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), getString(R.string.measurements_for) + " " + alreadyExists + " " + getString(R.string.already_entered), Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            listener.onMeasurementItemsCreated(getMeasurementItems(), date);

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_measurement, null);
        TextView title = (TextView) contentView.findViewById(R.id.measurementFragmentTitle);
        title.setText(R.string.new_entry);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadBodyPartsDatabase();

        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());

        /*
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(UserActivity.USER, Context.MODE_PRIVATE);
        int reg_day = sharedPreferences.getInt("Registration day", 0);
        int reg_month = sharedPreferences.getInt("Registration month", 0);
        int reg_year = sharedPreferences.getInt("Registration year", 0);
        Calendar c = Calendar.getInstance();
        c.set(reg_year, reg_month, reg_day);
        datePicker.setMinDate(c.getTimeInMillis());
        */

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

    public void loadBodyPartsDatabase() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String body_part = (String) dataSnapshot1.getValue();
                    body_parts.add(body_part);
                }
                loadData();
                for(int i = 0; i < body_parts.size(); i++) {
                    entries.add(new ArrayList<Weight>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Body_Parts").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    private void loadData() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();

                //body parts
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    String body_part = dataSnapshot1.getKey();
                    int idx = body_parts.indexOf(body_part);
                    //entries
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> water_entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = water_entries.get(key);
                            Weight weight = new Weight(key, weight_value);
                            entries.get(idx).add(weight);
                            //list.add(weight);
                        } catch (Exception e) {
                            Map<String, Long> water_entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = (double) water_entries.get(key);
                            Weight weight = new Weight(key, weight_value);
                            entries.get(idx).add(weight);
                            //list.add(weight);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Measurements").child(userId).addValueEventListener(eventListener);

    }

    private HashMap<String, Double> getMeasurementItems() {
        //ArrayList<MeasurementItem> newItems = new ArrayList<>();
        new_entries = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        date = Long.toString(calendar.getTimeInMillis() / 1000);

        for (int i = 0; i < body_parts.size(); i++) {
            double value = 0;
            try {
                value = Double.parseDouble(editTexts.get(i).getText().toString());
            } catch (NumberFormatException f) {
                value = -1;
            }

            if (value != -1)
                new_entries.put(body_parts.get(i), value);

        }

        return new_entries;
    }

    public boolean alreadyExists(HashMap<String, Double> items) {
        boolean exists = false;

        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String key = entry.getKey();
            int idx = body_parts.indexOf(key);
            for(Weight weight: entries.get(idx)) {
                if(weight.date.equals(date)) {
                    if (!alreadyExists.equals(""))
                        alreadyExists = alreadyExists + ", " + key;
                    else
                        alreadyExists = key;
                    exists = true;
                }
            }

        }

        /*
        for (int j = 0; j < items.size(); j++) {
            for (int i = 0; i < list.size(); i++) {
                if ((items.get(j).measurement_calculated == list.get(i).measurement_calculated) && items.get(j).body_part.equals(list.get(i).body_part)) {
                    if (!alreadyExists.equals(""))
                        alreadyExists = alreadyExists + ", " + items.get(j).body_part;
                    else
                        alreadyExists = items.get(j).body_part;
                    exists = true;
                }
            }
        }*/


        return exists;
    }
}
