package hu.bme.aut.fitnessapp.controllers.adapters;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import hu.bme.aut.fitnessapp.models.adapter_models.ExerciseAdapterModel;
import hu.bme.aut.fitnessapp.models.user_models.workout_models.VideoModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Exercise;


public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseListViewHolder> {

    private ExerciseAdapterModel model;


    public ExerciseAdapter(List<Exercise> items, List<Equipment> equipmentItems) {
        model = new ExerciseAdapterModel(items, equipmentItems);
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
    public void onBindViewHolder(@NonNull final ExerciseAdapter.ExerciseListViewHolder holder, final int position) {

        Exercise item = model.getItems().get(position);
        holder.nameTextView.setText(item.getName());
        holder.videoView.setVisibility(View.GONE);
        holder.placeHolder.setVisibility(View.VISIBLE);

        if (item.getRepTime() == 0) holder.repsTextView.setText(R.string.reps);
        else holder.repsTextView.setText(R.string.time);

        String muscles = model.makeMusclesText(position);
        holder.musclesTextView.setText(muscles);

        String equipments = model.makeEquipmentText(position);
        holder.equipmentTextView.setText(equipments);

        holder.item = item;

        String name = VideoModel.transformName(item.getName());
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


    @Override
    public int getItemCount() {
        return model.getItems().size();
    }

    public class ExerciseListViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView repsTextView;
        TextView musclesTextView;
        TextView equipmentTextView;
        Exercise item;
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

}