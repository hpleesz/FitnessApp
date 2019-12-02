package hu.bme.aut.fitnessapp.Controllers.User.Measurements;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
/*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

 */
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels.NewMeasurementItemModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Entities.Measurement;

public class NewMeasurementItemDialogFragment extends DialogFragment {

    private DatePicker datePicker;

    private String date;

    private NewMeasurementItemModel newMeasurementItemModel;

    private ArrayList<EditText> editTexts;

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
                        } else if (newMeasurementItemModel.alreadyExists(getMeasurementItems())) {
                            dismiss();
                            Toast toast = Toast.makeText(getActivity().getApplication().getApplicationContext(), getString(R.string.measurements_for) + " " + newMeasurementItemModel.getAlreadyExists() + " " + getString(R.string.already_entered), Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            listener.onMeasurementItemsCreated(getMeasurementItems(), date);

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    public void onStart() {
        super.onStart();
        newMeasurementItemModel = new NewMeasurementItemModel();
        newMeasurementItemModel.loadBodyParts();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_measurement, null);
        TextView title = (TextView) contentView.findViewById(R.id.measurementFragmentTitle);
        title.setText(R.string.new_entry);
        //newMeasurementItemModel = new NewMeasurementItemModel();
        //newMeasurementItemModel.loadBodyParts();

        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());

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

    private HashMap<String, Double> getMeasurementItems() {
        HashMap<String, Double> new_entries;
        ArrayList<String> measurements = new ArrayList<>();

        for(int i = 0; i < editTexts.size(); i++) {
            measurements.add(editTexts.get(i).getText().toString());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        date = Long.toString(calendar.getTimeInMillis() / 1000);

        new_entries = newMeasurementItemModel.getMeasurementItems(measurements, date);

        return new_entries;
    }

    @Override
    public void onStop() {
        super.onStop();
        newMeasurementItemModel.removeListeners();
    }
}
