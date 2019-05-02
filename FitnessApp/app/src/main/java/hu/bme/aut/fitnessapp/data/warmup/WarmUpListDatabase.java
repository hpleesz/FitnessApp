package hu.bme.aut.fitnessapp.data.warmup;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationItemDao;

@Database(
        entities = {WarmUpItem.class},
        version = 1,
        exportSchema = false
)

public abstract class WarmUpListDatabase extends RoomDatabase {
    public abstract WarmUpItemDao warmUpItemDao();

}
