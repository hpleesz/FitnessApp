package hu.bme.aut.fitnessapp.Controllers.User.Water;

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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import hu.bme.aut.fitnessapp.Models.User.Water.NewWaterModel;
import hu.bme.aut.fitnessapp.R;

public class NewWaterDialogFragment extends DialogFragment {

    private EditText amountEditText;

    public static final String TAG = "NewWaterDialogFragment";

    private NewWaterModel newWaterModel;

    public interface NewWaterDialogListener {
        void onWaterAdded(double newItem);
    }

    private NewWaterDialogFragment.NewWaterDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof NewWaterDialogFragment.NewWaterDialogListener) {
            listener = (NewWaterDialogFragment.NewWaterDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewWaterDialogListener interface!");
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
                        listener.onWaterAdded(newWaterModel.getWater(amountEditText.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_water, null);
        TextView title = (TextView) contentView.findViewById(R.id.waterFragmentTitle);
        title.setText(R.string.new_entry);
        amountEditText = contentView.findViewById(R.id.waterAmountEditText);

        newWaterModel = new NewWaterModel();

        return contentView;
    }




}