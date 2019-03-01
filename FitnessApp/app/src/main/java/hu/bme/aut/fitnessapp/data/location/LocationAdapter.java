package hu.bme.aut.fitnessapp.data.location;

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

import hu.bme.aut.fitnessapp.R;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final List<LocationItem> items;

    private LocationAdapter.LocationItemDeletedListener del_listener;
    private LocationAdapter.LocationItemSelectedListener select_listener;


    public LocationAdapter(LocationAdapter.LocationItemDeletedListener del_listener, LocationAdapter.LocationItemSelectedListener select_listener) {
        this.select_listener = select_listener;
        this.del_listener = del_listener;
        items = new ArrayList<>();

    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_location_list, parent, false);
        return new LocationAdapter.LocationViewHolder(itemView);
    }


    public void addItem(LocationItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void deleteItem(LocationItem item){
        items.remove(item);
        notifyDataSetChanged();
    }

    public void update(List<LocationItem> locationItems) {
        items.clear();
        items.addAll(locationItems);
        notifyDataSetChanged();
    }

    public void update(LocationItem locationItem) {
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).location_id.equals(locationItem.location_id))
                items.set(i, locationItem);
        }
        notifyDataSetChanged();

    }


    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationItem item = items.get(position);
        holder.nameTextView.setText(item.location_name);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface LocationItemDeletedListener{
        void onItemDeleted(LocationItem item);
    }

    public interface LocationItemSelectedListener{
        void onItemSelected(LocationItem item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageButton removeButton;

        transient LocationItem item;

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
        }

    }

}