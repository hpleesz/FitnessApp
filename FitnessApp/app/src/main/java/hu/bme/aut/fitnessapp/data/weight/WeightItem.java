package hu.bme.aut.fitnessapp.data.weight;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "weightitem")
public class WeightItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public Long weight_id;

    @ColumnInfo(name = "day")
    public int weight_day;

    @ColumnInfo(name = "month")
    public int weight_month;

    @ColumnInfo(name = "year")
    public int weight_year;

    @ColumnInfo(name = "value")
    public double weight_value;

    @ColumnInfo(name = "calculated")
    public int weight_calculated;

}
