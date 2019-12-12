package hu.bme.aut.fitnessapp.controllers.user.water;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import hu.bme.aut.fitnessapp.models.user_models.water_models.EditWaterModel;
import hu.bme.aut.fitnessapp.R;

public class EditWaterDialogFragment extends DialogFragment implements EditWaterModel.WaterLoadListener{

    private EditText amountEditText;

    private EditWaterModel editWaterModel;

    public static final String TAG = "EditWaterDialogFragment";

    @Override
    public void onWaterLoaded(String water) {
        amountEditText.setText(water);
    }

    public interface EditWaterDialogListener {
        void onWaterEdited(double newItem);
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
                        listener.onWaterEdited(editWaterModel.getWater(amountEditText.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_water, null);
        TextView title = contentView.findViewById(R.id.waterFragmentTitle);
        title.setText(R.string.edit);
        amountEditText = contentView.findViewById(R.id.waterAmountEditText);

        editWaterModel = new EditWaterModel(this);
        editWaterModel.loadWaterEntry();
        return contentView;
    }



}