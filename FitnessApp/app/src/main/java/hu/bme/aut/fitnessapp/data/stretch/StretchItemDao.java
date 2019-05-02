package hu.bme.aut.fitnessapp.data.stretch;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface StretchItemDao {
    @Query("SELECT * FROM stretchitem")
    List<StretchItem> getAll();

    @Insert
    long insert(StretchItem stretchItem);

    @Update
    void update(StretchItem stretchItem);

    @Delete
    void deleteItem(StretchItem stretchItem);

}