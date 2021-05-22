package com.android.locationtracking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.android.locationtracking.data.db.Converter

@Entity
data class Entry(
    var startTime: Long,
    var stopTime: Long,
    @TypeConverters(Converter::class)
    var logs: List<Logs>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
