package hu.bme.aut.fitnessapp.data.measurement;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MeasurementItemDao {
    @Query("SELECT * FROM measurementitem")
    List<MeasurementItem> getAll();

    @Query("SELECT * FROM measurementitem WHERE body like :bodypart")
    List<MeasurementItem> getMeasurementsWithBodyPart(String bodypart);

    @Insert
    long insert(MeasurementItem measurementItem);

    @Update
    void update(MeasurementItem measurementItem);

    @Delete
    void deleteItem(MeasurementItem measurementItem);


}