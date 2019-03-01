package hu.bme.aut.fitnessapp.data.location;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(
        entities = {LocationItem.class},
        version = 1,
        exportSchema = false
)

@TypeConverters(value = {LocationItem.Converters.class})
public abstract class LocationListDatabase extends RoomDatabase {
    public abstract LocationItemDao locationItemDao();

}
