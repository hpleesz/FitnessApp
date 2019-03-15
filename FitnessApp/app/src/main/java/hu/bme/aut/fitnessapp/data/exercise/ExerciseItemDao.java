package hu.bme.aut.fitnessapp.data.exercise;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.location.LocationItem;

@Dao
public interface ExerciseItemDao {
    @Query("SELECT * FROM exerciseitem")
    List<ExerciseItem> getAll();

    @Insert
    long insert(ExerciseItem exerciseItem);

    @Update
    void update(ExerciseItem exerciseItem);

    @Delete
    void deleteItem(ExerciseItem exerciseItem);

    @Query("SELECT * FROM exerciseitem WHERE equipment1 = :e1 AND equipment2 = :e2")
    List<ExerciseItem> getExercisesWithEquipments(int e1, int e2);

}