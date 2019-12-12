package hu.bme.aut.fitnessapp.controllers.user.measurements;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import hu.bme.aut.fitnessapp.models.user_models.measurement_models.NewMeasurementItemModel;
import hu.bme.aut.fitnessapp.R;

public class NewMeasurementItemDialogFragment extends DialogFragment {

    private DatePicker datePicker;

    private String date;

    private NewMeasurementItemModel newMeasurementItemModel;

    private ArrayList<EditText> editTexts;

    public static final String TAG = "NewMeasurementDialogFragment";

    public interface NewMeasurementDialogListener {
        void onMeasurementItemsCreated(Map<String, Double> newEntries, String date);
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

    @Override
    public void onStart() {
        super.onStart();
        newMeasurementItemModel = new NewMeasurementItemModel();
        newMeasurementItemModel.loadBodyParts();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_measurement, null);
        TextView title = contentView.findViewById(R.id.measurementFragmentTitle);
        title.setText(R.string.new_entry);

        datePicker = contentView.findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());

        editTexts = new ArrayList<>();
        EditText shouldersEditText = contentView.findViewById(R.id.ShouldersEditText);
        editTexts.add(shouldersEditText);
        EditText chestEditText = contentView.findViewById(R.id.ChestEditText);
        editTexts.add(chestEditText);
        EditText waistEditText = contentView.findViewById(R.id.WaistEditText);
        editTexts.add(waistEditText);
        EditText hipsEditText = contentView.findViewById(R.id.HipsEditText);
        editTexts.add(hipsEditText);
        EditText rightUpperArmEditText = contentView.findViewById(R.id.RightUpperArmEditText);
        editTexts.add(rightUpperArmEditText);
        EditText leftUpperArmEditText = contentView.findViewById(R.id.LeftUpperArmEditText);
        editTexts.add(leftUpperArmEditText);
        EditText rightForearmEditText = contentView.findViewById(R.id.RightForearmEditText);
        editTexts.add(rightForearmEditText);
        EditText leftForearmEditText = contentView.findViewById(R.id.LeftForearmEditText);
        editTexts.add(leftForearmEditText);
        EditText rightThighEditText = contentView.findViewById(R.id.RightThighEditText);
        editTexts.add(rightThighEditText);
        EditText leftThighEditText = contentView.findViewById(R.id.LeftThighEditText);
        editTexts.add(leftThighEditText);
        EditText rightCalfEditText = contentView.findViewById(R.id.RightCalfEditText);
        editTexts.add(rightCalfEditText);
        EditText leftCalfEditText = contentView.findViewById(R.id.LeftCalfEditText);
        editTexts.add(leftCalfEditText);


        return contentView;
    }

    private Map<String, Double> getMeasurementItems() {
        Map<String, Double> newEntries;
        ArrayList<String> measurements = new ArrayList<>();

        for(int i = 0; i < editTexts.size(); i++) {
            measurements.add(editTexts.get(i).getText().toString());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        date = Long.toString(calendar.getTimeInMillis() / 1000);

        newEntries = newMeasurementItemModel.getMeasurementItems(measurements, date);

        return newEntries;
    }

    @Override
    public void onStop() {
        super.onStop();
        newMeasurementItemModel.removeListeners();
    }
}
