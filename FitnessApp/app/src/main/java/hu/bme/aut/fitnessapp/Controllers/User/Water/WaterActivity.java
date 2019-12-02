package hu.bme.aut.fitnessapp.Controllers.User.Water;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import hu.bme.aut.fitnessapp.Models.UserModels.WaterModels.WaterModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;

public class WaterActivity extends NavigationActivity implements NewWaterDialogFragment.NewWaterDialogListener, EditWaterDialogFragment.EditWaterDialogListener, WaterModel.WaterListener {

    private double display;

    TextView consumedWaterTV;

    private WaterModel waterModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_water, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(4).setChecked(true);

        setFloatingActionButton();
        setConsumedWaterClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        waterModel = new WaterModel(this);
        waterModel.loadWeight();
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
        consumedWaterTV = (TextView) findViewById(R.id.consumedWaterTextView);
        consumedWaterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditWaterDialogFragment().show(getSupportFragmentManager(), EditWaterDialogFragment.TAG);
            }
        });
    }

    public void setRecommendedWaterText(double display) {
        String text =  display + " " + getString(R.string.litre);
        TextView recommendedWaterTV = (TextView) findViewById(R.id.recommendedWaterTextView);
        recommendedWaterTV.setText(text);
    }


    public void setConsumedWaterText(double water) {
        String text = Double.toString(water) + " " + getString(R.string.litre);
        consumedWaterTV.setText(text);
        setBottleImage();

    }

    public void setBottleImage() {
        ImageView bottle = (ImageView) findViewById(R.id.consumedWaterImage);
        int percent = waterModel.calculatePercent();
        setPercentText(percent);
        drawRecommendedCompleted(percent);
        for (int i = 100; i >= 0; i = i - 10) {
            if (percent >= i) {
                String name = "bottle" + i;
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                bottle.setImageResource(id);
                break;
            }
        }
    }

    public void setPercentText(int percent) {
        TextView percentTextView = (TextView) findViewById(R.id.percentTextView);
        String percentString = percent + "%" + " complete";
        percentTextView.setText(percentString);
    }

    public void drawRecommendedCompleted(int percent) {
        ImageView fireworkImageView = (ImageView) findViewById(R.id.fireWorkImageView);
        ImageView celebrationImageView = (ImageView) findViewById(R.id.celebrationImageView);
        if (percent >= 100) {
            fireworkImageView.setVisibility(View.VISIBLE);
            celebrationImageView.setVisibility(View.VISIBLE);
        } else {
            fireworkImageView.setVisibility(View.INVISIBLE);
            celebrationImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void onWaterAdded(double newItem) {
        waterModel.addWater(newItem);
    }

    public void onWaterEdited(double newItem) {
        waterModel.editWater(newItem);
    }






    //TEST
    public void setCurrent_weight(Double current_weight) {
    }

    public double getDisplay() {
        return display;
    }


    public void setDisplay(double display) {
        this.display = display;
    }

    public void setWater2(double water2) {
    }


    @Override
    public void onConsumedLoaded(double water) {
        setConsumedWaterText(water);
    }

    @Override
    public void onRecommendedLoaded(double water) {
        setRecommendedWaterText(water);
    }

    @Override
    public void onStop() {
        super.onStop();
        waterModel.removeListeners();
    }
}