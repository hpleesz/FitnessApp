package hu.bme.aut.fitnessapp.data.exercise;


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


public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseListViewHolder>{

    private final ArrayList<ExerciseItem> items;


    public ExerciseAdapter() {
        items = new ArrayList<>();

    }

    @NonNull
    @Override
    public ExerciseAdapter.ExerciseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_exercise_list, parent, false);
        return new ExerciseAdapter.ExerciseListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ExerciseListViewHolder holder, final int position) {
        ExerciseItem item = items.get(position);
        holder.nameTextView.setText(item.exercise_name);

        holder.item = item;
    }

    public void update(List<ExerciseItem> exerciseItemList) {
        items.clear();
        items.addAll(exerciseItemList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ExerciseListViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ExerciseItem item;

        ExerciseListViewHolder(final View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.ExerciseItemNameTextView);
        }
    }

}
