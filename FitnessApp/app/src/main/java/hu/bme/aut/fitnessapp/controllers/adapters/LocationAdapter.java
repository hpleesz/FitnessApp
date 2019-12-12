package hu.bme.aut.fitnessapp.controllers.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.LocationAdapterModel;
import hu.bme.aut.fitnessapp.controllers.user.locations.LocationActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.Location;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private LocationAdapterModel model;

    private LocationAdapter.LocationItemDeletedListener delListener;
    private LocationAdapter.LocationItemSelectedListener selectListener;


    public LocationAdapter(LocationAdapter.LocationItemDeletedListener delListener, LocationAdapter.LocationItemSelectedListener selectListener, List<Location> list) {
        this.selectListener = selectListener;
        this.delListener = delListener;
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
        holder.nameTextView.setText(item.getName());
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

        Location item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);
            removeButton = itemView.findViewById(R.id.LocationItemRemoveButton);


            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delListener.onItemDeleted(item);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectListener.onItemSelected(item, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder((LocationActivity) delListener).create();
                    alertDialog.setTitle("Delete item?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delListener.onItemDeleted(item);
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