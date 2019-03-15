package hu.bme.aut.fitnessapp.data.location;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;

@Entity(tableName = "locationitem")
public class LocationItem implements Serializable{

    public static class Converters implements Serializable{
        @TypeConverter
        public ArrayList<EquipmentItem> equipmentListFromString(String value) {
            Type listType = new TypeToken<ArrayList<EquipmentItem>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public String stringFromEquipmetList(ArrayList<EquipmentItem> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }

    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public int location_id;

    @ColumnInfo(name = "name")
    public String location_name;

    @ColumnInfo(name="equipment")
    public ArrayList<EquipmentItem> location_equipmentItems;



}
