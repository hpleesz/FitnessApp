package hu.bme.aut.fitnessapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.WaterActivity;

import static android.content.Context.MODE_PRIVATE;

public class EditWaterDialogFragment extends DialogFragment {

    private EditText amountEditText;
    private SharedPreferences water_consumed;

    public static final String TAG = "EditWaterDialogFragment";

    public interface EditWaterDialogListener {
        void onWaterEdited(float newItem);
    }

    private EditWaterDialogFragment.EditWaterDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof EditWaterDialogFragment.EditWaterDialogListener) {
            listener = (EditWaterDialogFragment.EditWaterDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the EditWaterDialogListener interface!");
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
                        listener.onWaterEdited(getWater());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_water, null);
        TextView title = (TextView)contentView.findViewById(R.id.waterFragmentTitle);
        title.setText(R.string.edit);
        amountEditText = contentView.findViewById(R.id.waterAmountEditText);
        water_consumed = getActivity().getSharedPreferences(WaterActivity.WATER, MODE_PRIVATE);
        float water = water_consumed.getFloat("Consumed", 0);
        amountEditText.setText(Float.toString(water));
        return contentView;
    }


    private float getWater() {
        float water = 0;
        try {
            water = Float.parseFloat(amountEditText.getText().toString());
        } catch (NumberFormatException f) {
            water = water_consumed.getFloat("Consumed", 0);
        }
        return water;
    }


}