package hu.bme.aut.fitnessapp.controllers.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.PublicLocationAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class PublicLocationAdapter extends RecyclerView.Adapter<PublicLocationAdapter.LocationViewHolder> {

    private PublicLocationAdapterModel model;

    private PublicLocationAdapter.LocationItemDeletedListener delListener;
    private PublicLocationAdapter.LocationItemSelectedListener selectListener;

    public PublicLocationAdapter(PublicLocationAdapter.LocationItemDeletedListener delListener, PublicLocationAdapter.LocationItemSelectedListener selectListener, List<PublicLocation> list) {
        this.selectListener = selectListener;
        this.delListener = delListener;
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

        PublicLocation item;

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
                    final AlertDialog alertDialog = new AlertDialog.Builder((AppCompatActivity) delListener).create();
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