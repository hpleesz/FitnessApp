package hu.bme.aut.fitnessapp.data.warmup;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import hu.bme.aut.fitnessapp.data.location.LocationItem;

    @Dao
    public interface WarmUpItemDao {
        @Query("SELECT * FROM warmupitem")
        List<WarmUpItem> getAll();

        @Insert
        long insert(WarmUpItem warmUpItem);

        @Update
        void update(WarmUpItem warmUpItem);

        @Delete
        void deleteItem(WarmUpItem warmUpItem);

        @Query("SELECT * FROM warmupitem WHERE upper_body = 1")
        List<WarmUpItem> getUpperBodyItems();

        @Query("SELECT * FROM warmupitem WHERE lower_body = 1")
        List<WarmUpItem> getLowerBodyItems();

    }
