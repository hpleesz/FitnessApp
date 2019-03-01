package hu.bme.aut.fitnessapp.data.equipment;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(
        entities = {EquipmentItem.class},
        version = 1,
        exportSchema = false
)

public abstract class EquipmentListDatabase extends RoomDatabase {
    public abstract EquipmentItemDao equipmentItemDao();

}
