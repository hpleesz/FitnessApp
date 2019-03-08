package hu.bme.aut.fitnessapp.data.weight;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(
        entities = {WeightItem.class},
        version = 1,
        exportSchema = false
)

public abstract class WeightListDatabase extends RoomDatabase {
    public abstract WeightItemDao weightItemDao();

}