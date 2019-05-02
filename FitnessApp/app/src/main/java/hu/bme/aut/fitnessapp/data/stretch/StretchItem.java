package hu.bme.aut.fitnessapp.data.stretch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "stretchitem")
public class StretchItem implements Serializable{

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int stretch_id;

    @ColumnInfo(name = "name")
    public String stretch_name;

}