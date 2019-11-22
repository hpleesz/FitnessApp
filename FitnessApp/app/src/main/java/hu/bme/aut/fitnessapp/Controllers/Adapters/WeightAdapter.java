package hu.bme.aut.fitnessapp.Controllers.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hu.bme.aut.fitnessapp.Models.AdapterModels.WeightAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.Weight.WeightActivity;
import hu.bme.aut.fitnessapp.Entities.Measurement;


public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private WeightAdapterModel model;

    private WeightAdapter.WeightItemDeletedListener del_listener;

    public WeightAdapter(WeightAdapter.WeightItemDeletedListener del_listener,ArrayList<Measurement> list) {
        this.del_listener = del_listener;
        model = new WeightAdapterModel(list);
    }

    @NonNull
    @Override
    public WeightAdapter.WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weight_list, parent, false);
        return new WeightAdapter.WeightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightAdapter.WeightViewHolder holder, int position) {
        Measurement item = model.getItems().get(position);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(item.date) * 1000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY.MM.dd");
        holder.dateTextView.setText(dateFormat.format(c.getTimeInMillis()));
        String text = Double.toString(item.value) + " kg";
        holder.valueTextView.setText(text);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }


    public interface WeightItemDeletedListener{
        void onItemDeleted(Measurement item);
    }

    class WeightViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView valueTextView;


        transient Measurement item;

        WeightViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.WeightItemDateTextView);
            valueTextView = itemView.findViewById(R.id.WeightItemValueTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder((WeightActivity)del_listener).create();
                    alertDialog.setTitle("Delete item?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            del_listener.onItemDeleted(item);
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
