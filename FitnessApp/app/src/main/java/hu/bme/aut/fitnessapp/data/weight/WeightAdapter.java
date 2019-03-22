package hu.bme.aut.fitnessapp.data.weight;

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

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.UserActivity;
import hu.bme.aut.fitnessapp.WeightActivity;
import hu.bme.aut.fitnessapp.fragments.NewWeightItemDialogFragment;


public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {


    private final ArrayList<WeightItem> items;

    private WeightAdapter.WeightItemDeletedListener del_listener;

    public WeightAdapter(WeightAdapter.WeightItemDeletedListener del_listener) {
        this.del_listener = del_listener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public WeightAdapter.WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_weight_list, parent, false);
        return new WeightAdapter.WeightViewHolder(itemView);
    }

    public void addItem(WeightItem item) {
        int pos = insertItem(item);
        notifyItemInserted(pos);
    }

    public void deleteItem(WeightItem item){
        items.remove(item);
        notifyDataSetChanged();
    }

    public void update(List<WeightItem> weightItems) {
        items.clear();
        items.addAll(weightItems);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull WeightAdapter.WeightViewHolder holder, int position) {
        WeightItem item = items.get(position);
        Calendar c = Calendar.getInstance();
        //c.set(item.weight_year, item.weight_month-1, item.weight_day);
        c.set(item.weight_year, item.weight_month, item.weight_day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY.MM.dd");
        holder.dateTextView.setText(dateFormat.format(c.getTimeInMillis()));
        String text = Double.toString(item.weight_value) + " kg";
        holder.valueTextView.setText(text);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface WeightItemDeletedListener{
        void onItemDeleted(WeightItem item);
    }

    class WeightViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView valueTextView;
        ImageButton removeButton;


        transient WeightItem item;

        WeightViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.WeightItemDateTextView);
            valueTextView = itemView.findViewById(R.id.WeightItemValueTextView);
            //removeButton = itemView.findViewById(R.id.WeightItemRemoveButton);

            /*
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    del_listener.onItemDeleted(item);
                }
            });
            */

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    //new DeleteDialogFragment().show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
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

    public int insertItem(WeightItem item) {

        if (items.isEmpty() || items.get(items.size() - 1).weight_calculated < item.weight_calculated) {
            items.add(item);
            return items.size()-1;
        }
        else if (items.get(0).weight_calculated > item.weight_calculated){
            items.add(0, item);
            return 0;
        }
        else {
            for (int i = 1; i < items.size(); i++) {
                if (items.get(i - 1).weight_calculated < item.weight_calculated && item.weight_calculated < items.get(i).weight_calculated) {
                    items.add(i, item);
                    return i;
                }
            }
        }
        return 0;

        /*
        if (items.isEmpty() || items.get(0).weight_calculated < item.weight_calculated) {
            items.add(0, item);
            return 0;
            //return items.size()-1;
        }
        else if (items.get(items.size()-1).weight_calculated > item.weight_calculated){
            items.add(item);
            return items.size()-1;
        }
        else {
            for (int i = 1; i < items.size(); i++) {
                if (items.get(i - 1).weight_calculated > item.weight_calculated && item.weight_calculated > items.get(i).weight_calculated) {
                    items.add(i, item);
                    return i;
                }
            }
        }
        return 0;
        */
    }

    public double getLastItemWeight() {
        if(items.isEmpty()) return -1;
        else return items.get(items.size()-1).weight_value;
    }

    public ArrayList<WeightItem> getItems() {
        return items;
    }

}
