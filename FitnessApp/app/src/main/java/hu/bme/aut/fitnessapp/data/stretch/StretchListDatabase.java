package hu.bme.aut.fitnessapp.data.stretch;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationItemDao;

@Database(
        entities = {StretchItem.class},
        version = 1,
        exportSchema = false
)

public abstract class StretchListDatabase extends RoomDatabase {
    public abstract StretchItemDao stretchItemDao();

}