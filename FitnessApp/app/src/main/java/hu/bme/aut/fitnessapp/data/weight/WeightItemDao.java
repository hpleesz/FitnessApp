package hu.bme.aut.fitnessapp.data.weight;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface WeightItemDao {
    @Query("SELECT * FROM weightitem")
    List<WeightItem> getAll();

    @Insert
    long insert(WeightItem weightItem);

    @Update
    void update(WeightItem weightItem);

    @Delete
    void deleteItem(WeightItem weightItem);


}