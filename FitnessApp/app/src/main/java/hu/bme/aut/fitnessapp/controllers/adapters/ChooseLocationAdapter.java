package hu.bme.aut.fitnessapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.LocationAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.Location;

public class ChooseLocationAdapter extends RecyclerView.Adapter<ChooseLocationAdapter.LocationViewHolder> {

    private LocationAdapterModel model;

    private ChooseLocationAdapter.LocationItemSelectedListener selectListener;

    public ChooseLocationAdapter(ChooseLocationAdapter.LocationItemSelectedListener selectListener, List<Location> items) {
        this.selectListener = selectListener;
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
        holder.nameTextView.setText(item.getName());
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

        Location item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectListener.onItemSelected(item, getAdapterPosition());
                }
            });

        }

    }

}