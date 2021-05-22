package com.android.locationtracking.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.locationtracking.data.db.EntriesDao
import com.android.locationtracking.data.model.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogsViewModel : ViewModel() {

    lateinit var navigator: LogsNavigator

    interface LogsNavigator {
        fun goBack()
        fun populateData(entries: List<Entry>)
    }

    fun getAllLogs(entriesDao: EntriesDao) {
        viewModelScope.launch {
            val entries = getLogs(entriesDao)
            navigator.populateData(entries)
        }
    }

    private suspend fun getLogs(entriesDao: EntriesDao): List<Entry> =
        withContext(Dispatchers.Default) {
            entriesDao.getAll()
        }

    public fun onBackClick(){
        navigator.goBack()
    }

}