package com.android.locationtracking.data.model

import androidx.room.Entity

data class Logs(
    var time:Long,
    var lat:Double,
    var lng:Double
)
