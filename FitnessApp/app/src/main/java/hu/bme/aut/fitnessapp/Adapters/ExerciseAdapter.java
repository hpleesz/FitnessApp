package hu.bme.aut.fitnessapp.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.Exercise;


public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseListViewHolder> {

    private final ArrayList<Exercise> items;
    private Context context;
    private List<Equipment> equipmentItemList;
    private SharedPreferences sharedPreferences;
    private Uri link;


    public ExerciseAdapter(ArrayList<Exercise> items, List<Equipment> equipmentItems) {
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
        sharedPreferences = context.getSharedPreferences(MainActivity.WORKOUT, Context.MODE_PRIVATE);
        return new ExerciseAdapter.ExerciseListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ExerciseAdapter.ExerciseListViewHolder holder, final int position) {

        Exercise item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.videoView.setVisibility(View.GONE);
        holder.placeHolder.setVisibility(View.VISIBLE);

        if (item.rep_time == 0) holder.repsTextView.setText(R.string.reps);
        else holder.repsTextView.setText(R.string.time);

        String muscles = item.muscles[0];
        if (item.muscles.length > 1) {
            for (int i = 1; i < item.muscles.length; i++) {
                muscles = muscles + ", " + item.muscles[i];
            }
        }
        holder.musclesTextView.setText(muscles);

        String equipments = "";

        for (int i = 0; i < equipmentItemList.size(); i++) {
            for (int j = 0; j < equipmentItemList.size(); j++) {
                if (item.equipment1 == equipmentItemList.get(i).id && item.equipment2 == equipmentItemList.get(j).id) {
                    if (i == 0) equipments = equipmentItemList.get(j).name;
                    else if (j == 0) equipments = equipmentItemList.get(i).name;
                    else
                        equipments = equipmentItemList.get(i).name + ", " + equipmentItemList.get(j).name;

                }
            }
        }
        holder.equipmentTextView.setText(equipments);

        holder.item = item;


        /*
        int id = context.getResources().getIdentifier(name, "raw", ExerciseListActivity.PACKAGE_NAME);

        String videoPath = "android.resource://" + ExerciseListActivity.PACKAGE_NAME + "/" + id;
        Uri uri = Uri.parse(videoPath);
         */


        //holder.videoView.setVideoURI(uri);

        String name = item.name;
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(name + ".mp4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.videoView.setVideoURI(uri);

            }
        });


    }


    public void update(List<Exercise> exerciseItemList) {
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
        Exercise item;
        VideoView videoView;
        TextureView textureView;
        View placeHolder;

        ExerciseListViewHolder(final View itemView) {
            super(itemView);
            placeHolder = itemView.findViewById(R.id.placeHolder);
            nameTextView = itemView.findViewById(R.id.ExerciseItemNameTextView);
            equipmentTextView = itemView.findViewById(R.id.ExerciseItemEquipmentTextView);
            repsTextView = itemView.findViewById(R.id.ExerciseItemRepsTextView);
            musclesTextView = itemView.findViewById(R.id.ExerciseItemMusclesTextView);
            videoView = itemView.findViewById(R.id.videoView);
            videoView.start();

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
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
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

    /*
    private void replaceExercise(int position) {
        String workout_type = sharedPreferences.getString("Workout type", "Lower body");
        ArrayList<String> body_parts = new ArrayList<>();
        int idx = 0;
        String bodypart = "";
        switch (workout_type) {
            case "Upper body":
                idx = getRandomNumber(MainActivity.upper_body_parts.length);
                bodypart = MainActivity.upper_body_parts[idx];
                break;

            case "Lower body":
                idx = getRandomNumber(MainActivity.lower_body_parts.length);
                bodypart = MainActivity.lower_body_parts[idx];
                break;

            case "Cardio 1":
            case "Cardio 2":
                int upper_or_lower = getRandomNumber(2);
                if(upper_or_lower == 0) {
                    idx = getRandomNumber(MainActivity.upper_body_parts.length);
                    bodypart = MainActivity.upper_body_parts[idx];
                }
                else {
                    idx = getRandomNumber(MainActivity.lower_body_parts.length);
                    bodypart = MainActivity.lower_body_parts[idx];
                }
                break;
        }

    }

    public int getRandomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }
    */
}
