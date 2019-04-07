package hu.bme.aut.fitnessapp.data.exercise;


import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.ExerciseListActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;


public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseListViewHolder>{

    private final ArrayList<ExerciseItem> items;
    private Context context;
    private List<EquipmentItem> equipmentItemList;


    public ExerciseAdapter(ArrayList<ExerciseItem> items, List<EquipmentItem> equipmentItems) {
        this.items = items;
        equipmentItemList = equipmentItems;

    }

    @NonNull
    @Override
    public ExerciseAdapter.ExerciseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_exercise_list, parent, false);
        context = parent.getContext();
        return new ExerciseAdapter.ExerciseListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ExerciseListViewHolder holder, final int position) {

        ExerciseItem item = items.get(position);
        holder.nameTextView.setText(item.exercise_name);
        holder.videoView.setVisibility(View.GONE);
        holder.placeHolder.setVisibility(View.VISIBLE);

        if(item.reps_time == 0) holder.repsTextView.setText(R.string.reps);
        else holder.repsTextView.setText(R.string.time);

        String muscles = item.exercise_muscles.get(0);
        if(item.exercise_muscles.size() > 1) {
            for (int i = 1; i < item.exercise_muscles.size(); i++){
                muscles = muscles + ", " + item.exercise_muscles.get(i);
            }
        }
        holder.musclesTextView.setText(muscles);

        String equipments = "";

        for(int i = 0; i < equipmentItemList.size(); i++){
            for(int j = 0; j < equipmentItemList.size(); j++){
                if(item.equipment1 == equipmentItemList.get(i).equipment_id && item.equipment2 == equipmentItemList.get(j).equipment_id){
                    if(i == 0) equipments = equipmentItemList.get(j).equipment_name;
                    else if(j == 0) equipments = equipmentItemList.get(i).equipment_name;
                    else equipments = equipmentItemList.get(i).equipment_name + ", " + equipmentItemList.get(j).equipment_name;

                }
            }
        }
        holder.equipmentTextView.setText(equipments);

        holder.item = item;

        String name = item.exercise_name;
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        int id = context.getResources().getIdentifier(name, "raw", ExerciseListActivity.PACKAGE_NAME);

        String videoPath = "android.resource://" + ExerciseListActivity.PACKAGE_NAME + "/" + id;
        Uri uri = Uri.parse(videoPath);
        holder.videoView.setVideoURI(uri);


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

    public class ExerciseListViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView repsTextView;
        TextView musclesTextView;
        TextView equipmentTextView;
        ExerciseItem item;
        VideoView videoView;
        View placeHolder;

        ExerciseListViewHolder(final View itemView) {
            super(itemView);
            placeHolder = itemView.findViewById(R.id.placeHolder);
            nameTextView = itemView.findViewById(R.id.ExerciseItemNameTextView);
            equipmentTextView = itemView.findViewById(R.id.ExerciseItemEquipmentTextView);
            repsTextView = itemView.findViewById(R.id.ExerciseItemRepsTextView);
            musclesTextView = itemView.findViewById(R.id.ExerciseItemMusclesTextView);
            videoView = itemView.findViewById(R.id.videoView);
            videoView.setVisibility(View.GONE);


            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!videoView.isPlaying()) {
                        videoView.start();
                    } else {
                        videoView.pause();
                }
                }
            });


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                placeHolder.setVisibility(View.GONE);
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });

            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                    videoView.start();
                    videoView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    placeHolder.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
            });


        }
    }

}
