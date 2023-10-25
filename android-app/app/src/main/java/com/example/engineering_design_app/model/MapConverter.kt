package com.example.engineering_design_app.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MapConverter {
    @TypeConverter
    fun fromMap(map: MutableMap<String, Any>?): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(data: String?): MutableMap<String, Any> {
        val gson = Gson()
        val type = object : TypeToken<MutableMap<String, Any>>() {}.type
        return gson.fromJson(data, type)
    }
}


