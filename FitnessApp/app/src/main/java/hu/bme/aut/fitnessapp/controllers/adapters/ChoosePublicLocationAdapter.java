package hu.bme.aut.fitnessapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.PublicLocationAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class ChoosePublicLocationAdapter extends RecyclerView.Adapter<ChoosePublicLocationAdapter.LocationViewHolder> {

    private PublicLocationAdapterModel model;

    private ChoosePublicLocationAdapter.LocationItemSelectedListener selectListener;

    public ChoosePublicLocationAdapter(ChoosePublicLocationAdapter.LocationItemSelectedListener selectListener, List<PublicLocation> list) {
        this.selectListener = selectListener;
        model = new PublicLocationAdapterModel(list);

    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_public_location_list, parent, false);
        return new ChoosePublicLocationAdapter.LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        PublicLocation item = model.getItems().get(position);
        holder.nameTextView.setText(item.getName());
        String addressText = item.getCountry() + ", " + item.getCity();
        holder.addressTextView.setText(addressText);
        String addressText2 = item.getZip() + ", " + item.getAddress();
        holder.address2TextView.setText(addressText2);
        String open = model.gymOpenText(position);
        holder.openTextView.setText(open);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }


    public interface LocationItemSelectedListener{
        void onItemSelected(PublicLocation item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;
        TextView address2TextView;
        TextView openTextView;

        PublicLocation item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);
            addressTextView = itemView.findViewById(R.id.LocationItemAddressTextView);
            address2TextView = itemView.findViewById(R.id.LocationItemAddress2TextView);
            openTextView = itemView.findViewById(R.id.LocationItemOpenTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectListener.onItemSelected(item, getAdapterPosition());
                }
            });
        }

    }

}