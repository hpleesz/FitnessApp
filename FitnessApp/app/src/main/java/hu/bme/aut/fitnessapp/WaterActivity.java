package hu.bme.aut.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hu.bme.aut.fitnessapp.fragments.EditWaterDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWaterDialogFragment;

public class WaterActivity extends NavigationActivity implements NewWaterDialogFragment.NewWaterDialogListener, EditWaterDialogFragment.EditWaterDialogListener{

    private float recommended;
    private float water;

    TextView consumedWaterTV;

    private SharedPreferences water_consumption;

    public static final String WATER = "water consumption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_water, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(4).setChecked(true);

        setFloatingActionButton();
        setConsumedWaterClick();
        setRecommendedWaterText();
        setConsumedWaterText();
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewWaterDialogFragment().show(getSupportFragmentManager(), NewWaterDialogFragment.TAG);
            }
        });
    }

    public void setConsumedWaterClick() {
        consumedWaterTV = (TextView)findViewById(R.id.consumedWaterTextView);
        consumedWaterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditWaterDialogFragment().show(getSupportFragmentManager(), EditWaterDialogFragment.TAG);
            }
        });
    }

    public void setRecommendedWaterText() {
        TextView recommendedWaterTV = (TextView)findViewById(R.id.recommendedWaterTextView);
        SharedPreferences user = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        float current_weight = user.getFloat("Current weight", 0);
        editor.apply();

        recommended = (float)(current_weight * 0.033 + 1);
        double display = Math.round(recommended * 10d) / 10d;
        String text = Double.toString(display) + " " + getString(R.string.litre);
        recommendedWaterTV.setText(text);
        water_consumption = getSharedPreferences(WATER, MODE_PRIVATE);
        SharedPreferences.Editor water_editor = water_consumption.edit();
        water_editor.putFloat("Recommended", recommended);
        water_editor.apply();
    }

    public void setConsumedWaterText() {
        SharedPreferences.Editor editor = water_consumption.edit();
        water = water_consumption.getFloat("Consumed", 0);
        editor.apply();
        String text = Float.toString(water) + " " + getString(R.string.litre);
        consumedWaterTV.setText(text);
        setBottleImage();
    }

    public void setBottleImage() {
        ImageView bottle = (ImageView)findViewById(R.id.consumedWaterImage);
        int percent = (int)((water / recommended) * 100);
        setPercentText(percent);
        drawRecommendedCompleted(percent);
        for(int i = 100; i >= 0; i=i-10) {
            if(percent >= i) {
                String name = "bottle" + i;
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                bottle.setImageResource(id);
                break;
            }
        }
    }

    public void setPercentText(int percent) {
        TextView percentTextView = (TextView)findViewById(R.id.percentTextView);
        String percentString = percent + "%" + " complete";
        percentTextView.setText(percentString);
    }

    public void drawRecommendedCompleted(int percent) {
        ImageView fireworkImageView = (ImageView)findViewById(R.id.fireWorkImageView);
        ImageView celebrationImageView = (ImageView)findViewById(R.id.celebrationImageView);
        if(percent >= 100) {
            fireworkImageView.setVisibility(View.VISIBLE);
            celebrationImageView.setVisibility(View.VISIBLE);
        }
        else {
            fireworkImageView.setVisibility(View.INVISIBLE);
            celebrationImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void onWaterAdded(float newItem) {
        SharedPreferences.Editor editor = water_consumption.edit();
        water = water + newItem;
        editor.putFloat("Consumed", water);
        editor.apply();
        setConsumedWaterText();
    }

    public void onWaterEdited(float newItem) {
        SharedPreferences.Editor editor = water_consumption.edit();
        water = newItem;
        editor.putFloat("Consumed", water);
        editor.apply();
        setConsumedWaterText();
    }

}
