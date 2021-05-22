package com.android.locationtracking.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.locationtracking.data.db.EntriesDao
import com.android.locationtracking.data.model.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel : ViewModel() {

    lateinit var navigator: DashboardNavigator

    fun onStartClick() {
        navigator.startStopFetchingLocation()
    }

    interface DashboardNavigator {
        fun startStopFetchingLocation()
        fun showLogs()
    }

    fun onLogsClick() {
        navigator.showLogs()
    }

    fun insertData(entriesDao: EntriesDao, newEntry: Entry) {
        viewModelScope.launch {
            insertDB(entriesDao, newEntry)
        }
    }

    private suspend fun insertDB(entriesDao: EntriesDao, newEntry: Entry) =
        withContext(Dispatchers.Default) {
            entriesDao.insert(newEntry)
        }

}