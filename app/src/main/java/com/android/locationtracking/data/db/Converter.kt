package com.android.locationtracking.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.android.locationtracking.data.model.Logs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converter {

    @TypeConverter
    fun fromLogs(logs: List<Logs>): String {
        val type = object : TypeToken<List<Logs>>() {}.type
        return Gson().toJson(logs, type)
    }

    @TypeConverter
    fun toLogs(logs: String): List<Logs> {
        val type = object : TypeToken<List<Logs>>() {}.type
        return Gson().fromJson(logs, type)
    }

}
