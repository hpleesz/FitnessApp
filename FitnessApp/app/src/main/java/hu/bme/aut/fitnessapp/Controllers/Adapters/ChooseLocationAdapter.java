package hu.bme.aut.fitnessapp.Controllers.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.AdapterModels.LocationAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Entities.Location;

public class ChooseLocationAdapter extends RecyclerView.Adapter<ChooseLocationAdapter.LocationViewHolder> {

    private LocationAdapterModel model;

    private ChooseLocationAdapter.LocationItemSelectedListener select_listener;

    public ChooseLocationAdapter(ChooseLocationAdapter.LocationItemSelectedListener select_listener, ArrayList<Location> items) {
        this.select_listener = select_listener;
        this.model = new LocationAdapterModel(items);

    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_location_list, parent, false);
        return new ChooseLocationAdapter.LocationViewHolder(itemView);
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

    public interface LocationItemSelectedListener{
        void onItemSelected(Location item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        transient Location item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    select_listener.onItemSelected(item, getAdapterPosition());
                }
            });

        }

    }

}