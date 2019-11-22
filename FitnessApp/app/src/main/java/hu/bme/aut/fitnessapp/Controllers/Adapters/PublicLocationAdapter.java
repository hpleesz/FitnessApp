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

import hu.bme.aut.fitnessapp.Models.AdapterModels.PublicLocationAdapterModel;
import hu.bme.aut.fitnessapp.Controllers.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class PublicLocationAdapter extends RecyclerView.Adapter<PublicLocationAdapter.LocationViewHolder> {

    private PublicLocationAdapterModel model;

    private PublicLocationAdapter.LocationItemDeletedListener del_listener;
    private PublicLocationAdapter.LocationItemSelectedListener select_listener;

    public PublicLocationAdapter(PublicLocationAdapter.LocationItemDeletedListener del_listener, PublicLocationAdapter.LocationItemSelectedListener select_listener, ArrayList<PublicLocation> list) {
        this.select_listener = select_listener;
        this.del_listener = del_listener;
        model = new PublicLocationAdapterModel(list);

    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_public_location_list, parent, false);
        return new PublicLocationAdapter.LocationViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        PublicLocation item = model.getItems().get(position);
        holder.nameTextView.setText(item.name);
        String addressText = item.country + ", " + item.city;
        holder.addressTextView.setText(addressText);
        String addressText2 = item.zip + ", " + item.address;
        holder.address2TextView.setText(addressText2);
        String open = model.gymOpenText(position);
        holder.openTextView.setText(open);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }


    public interface LocationItemDeletedListener{
        void onItemDeleted(PublicLocation item);
    }

    public interface LocationItemSelectedListener{
        void onItemSelected(PublicLocation item, int position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;
        TextView address2TextView;
        TextView openTextView;
        ImageButton removeButton;

        transient PublicLocation item;

        LocationViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.LocationItemNameTextView);
            addressTextView = itemView.findViewById(R.id.LocationItemAddressTextView);
            address2TextView = itemView.findViewById(R.id.LocationItemAddress2TextView);
            openTextView = itemView.findViewById(R.id.LocationItemOpenTextView);
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