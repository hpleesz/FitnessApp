package hu.bme.aut.fitnessapp.data.location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.GymMainActivity;
import hu.bme.aut.fitnessapp.LocationActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.WeightActivity;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;

public class PublicLocationAdapter extends RecyclerView.Adapter<PublicLocationAdapter.LocationViewHolder> {

    private final List<PublicLocation> items;

    private PublicLocationAdapter.LocationItemDeletedListener del_listener;
    private PublicLocationAdapter.LocationItemSelectedListener select_listener;

    public PublicLocationAdapter(PublicLocationAdapter.LocationItemDeletedListener del_listener, PublicLocationAdapter.LocationItemSelectedListener select_listener, ArrayList<PublicLocation> list) {
        this.select_listener = select_listener;
        this.del_listener = del_listener;
        items = list;

    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_location_list, parent, false);
        return new PublicLocationAdapter.LocationViewHolder(itemView);
    }


    public void addItem(PublicLocation item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void deleteItem(PublicLocation item){
        items.remove(item);
        notifyDataSetChanged();
    }

    public void update(List<PublicLocation> locationItems) {
        items.clear();
        items.addAll(locationItems);
        notifyDataSetChanged();
    }

    public void update(PublicLocation locationItem) {
        for(int i = 0; i < items.size(); i++){
            //if(items.get(i).location_id == Location.location_id)
            items.set(i, locationItem);
        }
        notifyDataSetChanged();

    }


    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        PublicLocation item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface LocationItemDeletedListener{
        void onItemDeleted(PublicLocation item);
    }

    public interface LocationItemSelectedListener{
        void onItemSelected(PublicLocation item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageButton removeButton;

        transient PublicLocation item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);
            removeButton = itemView.findViewById(R.id.LocationItemRemoveButton);


            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    del_listener.onItemDeleted(item);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    select_listener.onItemSelected(item, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    //new DeleteDialogFragment().show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
                    final AlertDialog alertDialog = new AlertDialog.Builder((GymMainActivity) del_listener).create();
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