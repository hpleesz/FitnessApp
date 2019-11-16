package hu.bme.aut.fitnessapp.Controllers.User.Weight;

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

import java.util.Calendar;

import hu.bme.aut.fitnessapp.Models.User.Weight.NewWeightItemModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Entities.Measurement;

public class NewWeightItemDialogFragment extends DialogFragment {

    private EditText valueEditText;
    private DatePicker datePicker;

    public static final String TAG = "NewWeightDialogFragment";

    public interface NewWeightDialogListener {
        void onWeightItemCreated(Measurement item);
    }

    private NewWeightItemDialogFragment.NewWeightDialogListener listener;

    private NewWeightItemModel newWeightItemModel;

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
                        } else if (newWeightItemModel.alreadyExists(getWeightItem())) {
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

        newWeightItemModel = new NewWeightItemModel();
        newWeightItemModel.loadWeight();


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


}
