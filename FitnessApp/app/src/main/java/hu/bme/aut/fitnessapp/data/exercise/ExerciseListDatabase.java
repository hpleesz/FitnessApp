package hu.bme.aut.fitnessapp.data.exercise;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationItemDao;

@Database(
        entities = {ExerciseItem.class},
        version = 1,
        exportSchema = false
)

@TypeConverters(value = {ExerciseItem.Converters.class})
public abstract class ExerciseListDatabase extends RoomDatabase {
    public abstract ExerciseItemDao exerciseItemDao();

}
