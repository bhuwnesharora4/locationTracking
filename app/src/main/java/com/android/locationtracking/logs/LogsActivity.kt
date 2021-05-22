package com.android.locationtracking.logs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.android.locationtracking.R
import com.android.locationtracking.data.db.Converter
import com.android.locationtracking.data.db.EntriesDao
import com.android.locationtracking.data.db.EntriesDatabase
import com.android.locationtracking.data.model.Entry
import com.android.locationtracking.data.model.Logs
import com.android.locationtracking.databinding.ActivityLogsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LogsActivity : AppCompatActivity(), LogsViewModel.LogsNavigator, OnMapReadyCallback {

    lateinit var binding: ActivityLogsBinding
    lateinit var viewModel: LogsViewModel
    private lateinit var entriesDao: EntriesDao
    private lateinit var adapter: EntriesAdapter
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logs)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(LogsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.navigator = this

        setupScreen()
    }

    private fun setupScreen() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.gMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setAdapter()
        initDb()
        viewModel.getAllLogs(entriesDao)
    }

    private fun setAdapter() {
        binding.rlLogs.layoutManager = LinearLayoutManager(this)
        adapter = EntriesAdapter(this, object : EntriesAdapter.LogsClickListener {
            override fun logClick(log: Logs) {
                showOnMap(log)
            }

        })
        binding.rlLogs.adapter = adapter
    }

    private fun showOnMap(log: Logs) {
        mMap?.clear()
        binding.rlMap.visibility = View.VISIBLE
        val currLoc = LatLng(log.lat, log.lng)
        mMap?.addMarker(
            MarkerOptions()
                .position(currLoc)
                .title("" + log.lat + log.lng)
        )
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 15f))
    }

    private fun initDb() {
        val db: EntriesDatabase =
            Room.databaseBuilder(application, EntriesDatabase::class.java, "appDB")
                .addTypeConverter(Converter()).build()
        entriesDao = db.entriesDao()
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun populateData(entries: List<Entry>) {
        if (entries.isNotEmpty())
            adapter.addItemList(entries)
        else binding.tvNoLogs.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (binding.rlMap.visibility == View.VISIBLE)
            binding.rlMap.visibility = View.GONE
        else
            super.onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
    }

}