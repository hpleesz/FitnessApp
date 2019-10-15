package hu.bme.aut.fitnessapp.data.equipment;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.models.Equipment;


public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentListViewHolder>{

    private final ArrayList<Equipment> items;
    private EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked;
    private ArrayList<Integer> clicked;

    public interface OnCheckBoxClicked{
        void onChecked(int pos);
        void onUnchecked(int pos);
    }

    public EquipmentAdapter(EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked, ArrayList<Equipment> list) {
        items = list;
        clicked = new ArrayList<>();
        this.onCheckBoxClicked = onCheckBoxClicked;
        for(int i = 0; i < items.size(); i++) {
            clicked.add(0);
        }

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
        Equipment item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.item = item;

        //for(int j = 0; j < selected.size(); j++) {
            //if (item.equipment_name.equals(selected.get(j).equipment_name))
        for(int i = 0; i<clicked.size(); i++){
            if(clicked.get(position) == 1)
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);
        }

    }

    public void update(List<Equipment> equipmentItemList) {
        items.clear();
        items.addAll(equipmentItemList);
        for(int i = 0; i < items.size(); i++) {
            clicked.add(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    public ArrayList<Integer> getCheckedEquipmentList() {
        ArrayList<Integer> selected = new ArrayList<>();
        for(int i = 0; i < clicked.size(); i++){
            if(clicked.get(i) == 1){
                selected.add(items.get(i).id);
            }
        }
        return selected;
    }

    public void onChecked(int pos) {
        clicked.set(pos, 1);
    }

    public void onUnchecked(int pos){
        clicked.set(pos, 0);
    }

    public void setCheckedEquipmentList(ArrayList<Integer> equipments) {
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < equipments.size(); j++) {
                if (items.get(i).id == (equipments.get(j)))
                    onChecked(i);
            }
        }
    }
}
