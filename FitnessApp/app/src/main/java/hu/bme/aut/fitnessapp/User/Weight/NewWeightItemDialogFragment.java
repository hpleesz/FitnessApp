package hu.bme.aut.fitnessapp.User.Weight;

import android.app.Dialog;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Models.Measurement;

public class NewWeightItemDialogFragment extends DialogFragment {

    private EditText valueEditText;
    private DatePicker datePicker;
    private List<Measurement> list;

    public static final String TAG = "NewWeightDialogFragment";

    public interface NewWeightDialogListener {
        void onWeightItemCreated(Measurement item);
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
                        if (getWeightItem().value == -1) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), R.string.no_weight_entered, Toast.LENGTH_LONG);
                            toast.show();
                        } else if (alreadyExists(getWeightItem())) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), R.string.already_entered_weight, Toast.LENGTH_LONG);
                            toast.show();
                        } else
                            listener.onWeightItemCreated(getWeightItem());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_weight, null);
        TextView title = (TextView) contentView.findViewById(R.id.weightFragmentTitle);
        title.setText(R.string.new_entry);
        valueEditText = contentView.findViewById(R.id.weightValueEditText);
        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());

        return contentView;
    }

    private Measurement getWeightItem() {
        Measurement measurementItem = new Measurement();
        try {
            measurementItem.value = Double.parseDouble(valueEditText.getText().toString());
        } catch (NumberFormatException f) {
            measurementItem.value = -1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        measurementItem.date = Long.toString(calendar.getTimeInMillis() / 1000);
        return measurementItem;
    }

    public boolean alreadyExists(Measurement item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.date.equals(list.get(i).date))
                return true;
        }
        return false;
    }

    private void loadDatabase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Weight").child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> water_entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = water_entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        list.add(measurement);
                    }
                    catch (Exception e) {
                        Map<String, Long> water_entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = (double)water_entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        list.add(measurement);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.addValueEventListener(eventListener);

    }
}
