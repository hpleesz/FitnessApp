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


public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentListViewHolder>{

    private final ArrayList<EquipmentItem> items;
    private EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked;
    private ArrayList<Integer> clicked;

    public interface OnCheckBoxClicked{
        void onChecked(int pos);
        void onUnchecked(int pos);
    }

    public EquipmentAdapter(EquipmentAdapter.OnCheckBoxClicked onCheckBoxClicked) {
        items = new ArrayList<>();
        clicked = new ArrayList<>();
        this.onCheckBoxClicked = onCheckBoxClicked;

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
        EquipmentItem item = items.get(position);
        holder.nameTextView.setText(item.equipment_name);
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

    public void update(List<EquipmentItem> equipmentItemList) {
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
        EquipmentItem item;

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

    public ArrayList<EquipmentItem> getCheckedEquipmentList() {
        ArrayList<EquipmentItem> selected = new ArrayList<>();
        for(int i = 0; i < clicked.size(); i++){
            if(clicked.get(i) == 1){
                selected.add(items.get(i));
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

    public void setCheckedEquipmentList(ArrayList<EquipmentItem> equipments) {
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < equipments.size(); j++) {
                if (items.get(i).equipment_name.equals(equipments.get(j).equipment_name))
                    onChecked(i);
            }
        }
    }
}
