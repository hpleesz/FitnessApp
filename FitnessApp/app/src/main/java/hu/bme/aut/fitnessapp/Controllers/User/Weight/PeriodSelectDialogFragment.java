package hu.bme.aut.fitnessapp.Controllers.User.Weight;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import hu.bme.aut.fitnessapp.R;

public class PeriodSelectDialogFragment extends DialogFragment {

    public static final String TAG = "PeriodSelectDialogFragment";

    RadioButton allButton;
    RadioButton monthButton;
    RadioButton weekButton;

    public interface PeriodSelectDialogListener {
        void onPeriodSelected();
    }

    private PeriodSelectDialogFragment.PeriodSelectDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof PeriodSelectDialogFragment.PeriodSelectDialogListener) {
            listener = (PeriodSelectDialogFragment.PeriodSelectDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the PeriodSelectDialogListener interface!");
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
                        listener.onPeriodSelected();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_period_select, null);
        TextView title = (TextView) contentView.findViewById(R.id.periodFragmentTitle);
        title.setText(R.string.period_select);
        weekButton = contentView.findViewById(R.id.weekRadioButton);
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        monthButton = contentView.findViewById(R.id.monthRadioButton);
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        allButton = contentView.findViewById(R.id.allRadioButton);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        setCurrentlyChecked();
        return contentView;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WeightActivity.PERIOD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (view.getId()) {
            case R.id.allRadioButton:
                if (checked)
                    editor.putString("Period", "all");
                break;
            case R.id.monthRadioButton:
                if (checked)
                    editor.putString("Period", "month");
                break;
            case R.id.weekRadioButton:
                if (checked)
                    editor.putString("Period", "week");
                break;
        }
        editor.apply();
    }

    public void setCurrentlyChecked() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WeightActivity.PERIOD, Context.MODE_PRIVATE);
        String settings = sharedPreferences.getString("Period", "all");
        switch (settings) {
            case "all":
                allButton.setChecked(true);
                break;
            case "month":
                monthButton.setChecked(true);
                break;
            case "week":
                weekButton.setChecked(true);
                break;


        }
    }


}