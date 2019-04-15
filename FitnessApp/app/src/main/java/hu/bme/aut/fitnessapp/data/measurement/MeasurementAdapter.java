package hu.bme.aut.fitnessapp.data.measurement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.MeasurementsGraphActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.WeightActivity;
import hu.bme.aut.fitnessapp.fragments.MeasurementsGraphFragment;
import hu.bme.aut.fitnessapp.fragments.NewWeightItemDialogFragment;


public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder> {


    private final ArrayList<MeasurementItem> items;

    private MeasurementAdapter.MeasurementItemDeletedListener del_listener;

    public MeasurementAdapter(MeasurementAdapter.MeasurementItemDeletedListener del_listener) {
        this.del_listener = del_listener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public MeasurementAdapter.MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weight_list, parent, false);
        return new MeasurementAdapter.MeasurementViewHolder(itemView);
    }

    public void addItem(MeasurementItem item) {
        int pos = insertItem(item);
        notifyItemInserted(pos);
    }

    public void deleteItem(MeasurementItem item){
        items.remove(item);
        notifyDataSetChanged();
    }

    public void update(List<MeasurementItem> measurementItems) {
        items.clear();
        for(int i = 0; i < measurementItems.size(); i++) {
            addItem(measurementItems.get(i));
        }
        //items.addAll(measurementItems);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementAdapter.MeasurementViewHolder holder, int position) {
        MeasurementItem item = items.get(position);
        Calendar c = Calendar.getInstance();
        c.set(item.measurement_year, item.measurement_month, item.measurement_day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY.MM.dd");
        holder.dateTextView.setText(dateFormat.format(c.getTimeInMillis()));
        String text = Double.toString(item.measurement_value) + " cm";
        holder.valueTextView.setText(text);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface MeasurementItemDeletedListener{
        void onItemDeleted(MeasurementItem item);
    }

    class MeasurementViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView valueTextView;

        transient MeasurementItem item;

        MeasurementViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.WeightItemDateTextView);
            valueTextView = itemView.findViewById(R.id.WeightItemValueTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder((MeasurementsGraphActivity)del_listener).create();
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

    public int insertItem(MeasurementItem item) {

        if (items.isEmpty() || items.get(items.size() - 1).measurement_calculated <= item.measurement_calculated) {
            items.add(item);
            return items.size()-1;
        }
        else if (items.get(0).measurement_calculated >= item.measurement_calculated){
            items.add(0, item);
            return 0;
        }
        else {
            for (int i = 1; i < items.size(); i++) {
                if (items.get(i - 1).measurement_calculated <= item.measurement_calculated && item.measurement_calculated <= items.get(i).measurement_calculated) {
                    items.add(i, item);
                    return i;
                }
            }
        }
        return 0;


    }

    public double getLastItemWeight() {
        if(items.isEmpty()) return -1;
        else return items.get(items.size()-1).measurement_value;
    }

    public ArrayList<MeasurementItem> getItems(String bodypart) {
        ArrayList<MeasurementItem> list = new ArrayList<>();
        for(int i = 0; i < items.size(); i++)
            if(items.get(i).body_part.equals(bodypart))
                list.add(items.get(i));
        return list;
    }

}
