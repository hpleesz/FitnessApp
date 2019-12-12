package hu.bme.aut.fitnessapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.EquipmentAdapterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.Equipment;


public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentListViewHolder>{

    private EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked;

    private EquipmentAdapterModel model;

    public interface OnCheckBoxClicked{
        void onChecked(int pos);
        void onUnchecked(int pos);
    }

    public EquipmentAdapter(EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked, List<Equipment> list) {
        this.onCheckBoxClicked = onCheckBoxClicked;
        model = new EquipmentAdapterModel(list);

    }

    @NonNull
    @Override
    public EquipmentAdapter.EquipmentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_equipment_list, parent, false);
        return new EquipmentAdapter.EquipmentListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull EquipmentAdapter.EquipmentListViewHolder holder, final int position) {
        Equipment item = model.getItems().get(position);
        holder.nameTextView.setText(item.getName());
        holder.item = item;

        for(int i = 0; i<model.getCheckedItems().size(); i++){
            if(model.getCheckedItems().get(position) == 1)
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return model.getItems().size();
    }

    class EquipmentListViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        CheckBox checkBox;
        Equipment item;

        EquipmentListViewHolder(final View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.EquipmentItemName);
            checkBox = itemView.findViewById(R.id.CheckBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        onCheckBoxClicked.onChecked(getAdapterPosition());
                    }
                    else {
                        onCheckBoxClicked.onUnchecked(getAdapterPosition());
                    }
                }

            });
        }
    }

    public List<Integer> getCheckedEquipmentList() {
        return model.getCheckedEquipmentList();
    }

    public void onChecked(int pos) {
        model.check(pos);
    }

    public void onUnchecked(int pos){
        model.uncheck(pos);
    }

    public void setCheckedEquipmentList(List<Integer> equipments) {
        model.setCheckedEquipmentList(equipments);
    }
}
