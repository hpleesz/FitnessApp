package hu.bme.aut.fitnessapp.data.location;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface LocationItemDao {
    @Query("SELECT * FROM locationitem")
    List<LocationItem> getAll();

    @Insert
    long insert(LocationItem locationItem);

    @Update
    void update(LocationItem locationItem);

    @Delete
    void deleteItem(LocationItem locationItem);


}