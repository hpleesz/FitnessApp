package hu.bme.aut.fitnessapp.data.warmup;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "warmupitem")
public class WarmUpItem implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int warmup_id;

    @ColumnInfo(name = "name")
    public String warmup_name;

    @ColumnInfo(name = "upper_body")
    public boolean warmup_upper;

    @ColumnInfo(name = "lower_body")
    public boolean warmup_lower;

}