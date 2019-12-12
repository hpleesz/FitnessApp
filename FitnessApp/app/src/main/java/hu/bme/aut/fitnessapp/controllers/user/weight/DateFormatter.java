package hu.bme.aut.fitnessapp.controllers.user.weight;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter implements IAxisValueFormatter {

    private SimpleDateFormat dateFormat;

    public DateFormatter() {

        dateFormat = new SimpleDateFormat("MM.dd");

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date date = new Date((long) value);
        return dateFormat.format(date);
    }
}