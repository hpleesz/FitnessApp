package hu.bme.aut.fitnessapp.data.exercise;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;

@Entity(tableName = "exerciseitem")
public class ExerciseItem implements Serializable {

    public static class Converters implements Serializable{
        @TypeConverter
        public ArrayList<String> listFromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public String stringFromList(ArrayList<String> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }

        @TypeConverter
        public EquipmentItem equipmentItemFromString(String value) {
            Type type = new TypeToken<EquipmentItem>() {}.getType();
            return new Gson().fromJson(value, type);
        }

        @TypeConverter
        public String stringFromEquipmentItem(EquipmentItem equipmentItem) {
            Gson gson = new Gson();
            String json = gson.toJson(equipmentItem);
            return json;
        }

    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public Long exercise_id;

    @ColumnInfo(name = "name")
    public String exercise_name;

    @ColumnInfo(name = "equipment1")
    public int equipment1;

    @ColumnInfo(name = "equipment2")
    public int equipment2;

    @ColumnInfo(name = "muscles")
    public ArrayList<String> exercise_muscles;

    @ColumnInfo(name = "reps / time")
    public int reps_time;

}