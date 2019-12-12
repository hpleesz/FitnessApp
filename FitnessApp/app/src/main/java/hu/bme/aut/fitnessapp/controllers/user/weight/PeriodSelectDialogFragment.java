package hu.bme.aut.fitnessapp.controllers.user.weight;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.R;

public class PeriodSelectDialogFragment extends DialogFragment {

    public static final String TAG = "PeriodSelectDialogFragment";

    private RadioButton allButton;
    private RadioButton monthButton;
    private RadioButton weekButton;

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
        TextView title = contentView.findViewById(R.id.periodFragmentTitle);
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

    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WeightActivity.PERIOD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (view.getId()) {
            case R.id.allRadioButton:
            default:
                if (checked)
                    editor.putString(userID, "all");
                break;
            case R.id.monthRadioButton:
                if (checked)
                    editor.putString(userID, "month");
                break;
            case R.id.weekRadioButton:
                if (checked)
                    editor.putString(userID, "week");
                break;
        }
        editor.apply();
    }

    private void setCurrentlyChecked() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WeightActivity.PERIOD, Context.MODE_PRIVATE);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String settings = sharedPreferences.getString(userID, "all");
        switch (settings) {
            case "all":
            default:
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