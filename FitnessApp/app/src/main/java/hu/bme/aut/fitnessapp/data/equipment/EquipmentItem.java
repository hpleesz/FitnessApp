package hu.bme.aut.fitnessapp.data.equipment;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "equipmentitem")
public class EquipmentItem implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int equipment_id;

    @ColumnInfo(name = "name")
    public String equipment_name;

}
