package hu.bme.aut.fitnessapp.User.Workout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.R;

public class StretchActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private VideoView videoView;
    private TextView titleTextView;
    private int idx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warm_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadItems();

    }

    public void setLayoutElements() {
        videoView = findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        titleTextView = findViewById(R.id.ExerciseTitleTextView);
    }

    public void setFloatingActionButtons() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (items.size() > idx + 1) {
                    idx = idx + 1;
                    setExercise();
                } else {
                    Intent mainIntent = new Intent(StretchActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });

        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idx > 0) {
                    idx = idx -1;
                    setExercise();
                } else {
                    Intent mainIntent = new Intent(StretchActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void setExercise() {
        titleTextView.setText(items.get(idx));

        String name = items.get(idx);
        setVideo(name);

    }

    public void setVideo(String name) {
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
                videoView.setVideoURI(uri);

            }
        });

        videoView.start();

    }

    public void loadItems() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String item = dataSnapshot1.getValue(String.class);
                    items.add(item);
                }
                setLayoutElements();
                setExercise();
                setFloatingActionButtons();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Stretch").addValueEventListener(eventListener);
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

}
