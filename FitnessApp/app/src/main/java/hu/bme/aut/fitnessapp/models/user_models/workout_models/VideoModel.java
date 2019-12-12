package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class VideoModel {

    private VideoModel.VideoLoadedListener videoLoadedListener;

    public interface VideoLoadedListener {
        void onVideoReady(Uri uri);
    }

    public VideoModel(Object object) {
        this.videoLoadedListener = (VideoModel.VideoLoadedListener)object;
    }

    public void setVideo(String name) {
        name = transformName(name);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(name + ".mp4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                videoLoadedListener.onVideoReady(uri);
            }
        });
    }

    public static String transformName(String name) {
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        return name;
    }

    public abstract void navigateLeft();

    public abstract void navigateRight();

}
