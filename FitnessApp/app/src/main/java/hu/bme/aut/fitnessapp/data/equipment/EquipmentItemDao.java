package hu.bme.aut.fitnessapp.data.equipment;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EquipmentItemDao {
    @Query("SELECT * FROM equipmentitem")
    List<EquipmentItem> getAll();

    @Insert
    long insert(EquipmentItem equipmentItem);

    @Update
    void update(EquipmentItem equipmentItem);

    @Delete
    void deleteItem(EquipmentItem equipmentItem);


}