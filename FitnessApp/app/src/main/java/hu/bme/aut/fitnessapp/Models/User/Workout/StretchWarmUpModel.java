package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;
import android.net.Uri;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class StretchWarmUpModel {

    private ArrayList<String> items;
    private VideoView videoView;
    private TextView titleTextView;
    private int idx = 0;

    private Context activity;

    public interface ExerciseListener {
        void onExerciseLoaded();
        void onVideoReady(Uri uri);
    }

    public interface ExercisesEndListener {
        void onExercisesFinished();
    }

    public interface ExerciseListLoadedListener {
        void onExerciseListLoaded();
    }

    private StretchWarmUpModel.ExerciseListener exerciseListener;
    private StretchWarmUpModel.ExercisesEndListener exercisesEndListener;
    private StretchWarmUpModel.ExerciseListLoadedListener exerciseListLoadedListener;

    public StretchWarmUpModel(Context activity) {
        exerciseListener = (StretchWarmUpModel.ExerciseListener)activity;
        exercisesEndListener = (StretchWarmUpModel.ExercisesEndListener)activity;
        exerciseListLoadedListener = (StretchWarmUpModel.ExerciseListLoadedListener)activity;
        this.activity = activity;
    }


    public void navigateRight() {
        if (items.size() > idx + 1) {
            idx = idx + 1;
            exerciseListener.onExerciseLoaded();
            //((StretchWarmUpActivity)activity).setExercise();
        } else {
            exercisesEndListener.onExercisesFinished();
            //((StretchWarmUpActivity)activity).returnToMain();
        }
    }

    public void navigateLeft() {
        if (idx > 0) {
            idx = idx -1;
            exerciseListener.onExerciseLoaded();
            //((StretchWarmUpActivity)activity).setExercise();
        } else {
            exercisesEndListener.onExercisesFinished();
            //((StretchWarmUpActivity)activity).returnToMain();
        }
    }


    public String getExerciseItem() {
        return items.get(idx);
    }

    public void setVideo(String name) {
        name = transformName(name);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(name + ".mp4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                exerciseListener.onVideoReady(uri);
                //((StretchWarmUpActivity)activity).startVideo(uri);
            }
        });
    }

    public String transformName(String name) {
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        return name;
    }

    public abstract void loadItems();

    public ExerciseListLoadedListener getExerciseListLoadedListener() {
        return exerciseListLoadedListener;
    }


    public ArrayList<String> getItems() {
        return items;
    }

    public int getIdx() {
        return idx;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Context getActivity() {
        return activity;
    }

}
