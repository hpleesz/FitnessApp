package hu.bme.aut.fitnessapp.data.measurement;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "measurementitem")
public class MeasurementItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long measurement_id;

    @ColumnInfo(name = "body")
    public String body_part;

    @ColumnInfo(name = "day")
    public int measurement_day;

    @ColumnInfo(name = "month")
    public int measurement_month;

    @ColumnInfo(name = "year")
    public int measurement_year;

    @ColumnInfo(name = "value")
    public double measurement_value;

    @ColumnInfo(name = "calculated")
    public int measurement_calculated;


}