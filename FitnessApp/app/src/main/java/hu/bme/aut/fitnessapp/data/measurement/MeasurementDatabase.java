package hu.bme.aut.fitnessapp.data.measurement;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(
        entities = {MeasurementItem.class},
        version = 1,
        exportSchema = false
)

public abstract class MeasurementDatabase extends RoomDatabase {
    public abstract MeasurementItemDao measurementItemDao();

}