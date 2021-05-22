package com.android.locationtracking.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.locationtracking.data.model.Entry

@Database(entities = [Entry::class], version = 1)
@TypeConverters(Converter::class)
public abstract class EntriesDatabase : RoomDatabase() {

    abstract fun entriesDao():EntriesDao


}