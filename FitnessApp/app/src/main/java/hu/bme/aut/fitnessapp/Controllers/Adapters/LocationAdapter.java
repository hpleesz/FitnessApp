package hu.bme.aut.fitnessapp.Controllers.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.AdapterModels.LocationAdapterModel;
import hu.bme.aut.fitnessapp.Controllers.User.Locations.LocationActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Entities.Location;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private LocationAdapterModel model;

    private LocationAdapter.LocationItemDeletedListener del_listener;
    private LocationAdapter.LocationItemSelectedListener select_listener;


    public LocationAdapter(LocationAdapter.LocationItemDeletedListener del_listener, LocationAdapter.LocationItemSelectedListener select_listener, ArrayList<Location> list) {
        this.select_listener = select_listener;
        this.del_listener = del_listener;
        model = new LocationAdapterModel(list);
    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_location_list, parent, false);
        return new LocationAdapter.LocationViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location item = model.getItems().get(position);
        holder.nameTextView.setText(item.name);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }


    public interface LocationItemDeletedListener{
        void onItemDeleted(Location item);
    }

    public interface LocationItemSelectedListener{
        void onItemSelected(Location item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageButton removeButton;

        transient Location item;

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
                    final AlertDialog alertDialog = new AlertDialog.Builder((LocationActivity) del_listener).create();
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