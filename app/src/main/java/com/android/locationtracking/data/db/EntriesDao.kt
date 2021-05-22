package com.android.locationtracking.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.locationtracking.data.model.Entry

@Dao
interface EntriesDao {

    @Query("SELECT * FROM Entry ORDER BY id DESC")
    fun getAll():List<Entry>

    @Insert
    fun insert(entry: Entry)

}