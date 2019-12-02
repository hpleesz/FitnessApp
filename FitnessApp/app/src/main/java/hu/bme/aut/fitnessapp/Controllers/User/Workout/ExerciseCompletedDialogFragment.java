package hu.bme.aut.fitnessapp.Controllers.User.Workout;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import hu.bme.aut.fitnessapp.R;

public class ExerciseCompletedDialogFragment extends DialogFragment {
    public static final String TAG = "ExerciseCompletedDialogFragment";

    private ExerciseCompletedListener listener;

    public interface ExerciseCompletedListener {
        void onExerciseCompleted();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof ExerciseCompletedListener) {
            listener = (ExerciseCompletedListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the ExerciseCompletedListener interface!");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onExerciseCompleted();
                    }
                })
                .setNegativeButton(R.string.go_back, null)
                .create();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_goal_reached, null);
        TextView title = contentView.findViewById(R.id.goalReachedTitle);
        title.setText(R.string.workout_completed);
        return contentView;
    }
}

