package hu.bme.aut.fitnessapp.fragments;

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
import android.widget.EditText;
import android.widget.TextView;

import hu.bme.aut.fitnessapp.R;

public class DeleteDialogFragment extends DialogFragment {

    public static final String TAG = "DeleteDialogFragment";

    public interface DeleteDialogListener {
        void onItemDeleted();
    }

    private DeleteDialogFragment.DeleteDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.addItemDialog);
        FragmentActivity activity = getActivity();
        if (activity instanceof DeleteDialogFragment.DeleteDialogListener) {
            listener = (DeleteDialogFragment.DeleteDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the DeleteDialogListener interface!");
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
                        listener.onItemDeleted();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_water, null);
        TextView title = (TextView)contentView.findViewById(R.id.waterFragmentTitle);
        title.setText("Delete item?");
        return contentView;
    }


}