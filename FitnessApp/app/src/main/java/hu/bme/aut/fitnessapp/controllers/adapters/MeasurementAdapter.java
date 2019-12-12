package hu.bme.aut.fitnessapp.controllers.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.MeasurementAdapterModel;
import hu.bme.aut.fitnessapp.controllers.user.measurements.MeasurementsGraphActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.Measurement;


public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder> {

    private MeasurementAdapterModel model;

    private MeasurementAdapter.MeasurementItemDeletedListener delListener;

    public MeasurementAdapter(MeasurementAdapter.MeasurementItemDeletedListener delListener, List<Measurement> list, String bodyPart) {
        this.delListener = delListener;
        model = new MeasurementAdapterModel(list, bodyPart);
    }

    @NonNull
    @Override
    public MeasurementAdapter.MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weight_list, parent, false);
        return new MeasurementAdapter.MeasurementViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MeasurementAdapter.MeasurementViewHolder holder, int position) {
        Measurement item = model.getItems().get(position);
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(Long.parseLong(item.getDate()) * 1000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy.MM.dd");
        holder.dateTextView.setText(dateFormat.format(c.getTimeInMillis()));
        String text = Double.toString(item.getValue()) + " cm";
        holder.valueTextView.setText(text);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }


    public interface MeasurementItemDeletedListener{
        void onItemDeleted(Measurement item, String bodyPart);
    }

    class MeasurementViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView valueTextView;

        Measurement item;

        MeasurementViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.WeightItemDateTextView);
            valueTextView = itemView.findViewById(R.id.WeightItemValueTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder((MeasurementsGraphActivity)delListener).create();
                    alertDialog.setTitle("Delete item?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delListener.onItemDeleted(item, model.getBodyPart());
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return true;
                }
            });

        }
    }

}