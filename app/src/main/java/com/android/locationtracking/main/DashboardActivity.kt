package com.android.locationtracking.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.android.locationtracking.R
import com.android.locationtracking.data.db.Converter
import com.android.locationtracking.data.db.EntriesDao
import com.android.locationtracking.data.db.EntriesDatabase
import com.android.locationtracking.data.model.Entry
import com.android.locationtracking.data.model.Logs
import com.android.locationtracking.databinding.ActivityDashboardBinding
import com.android.locationtracking.helpers.LocationActivity
import com.android.locationtracking.logs.LogsActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DashboardActivity : LocationActivity(), DashboardViewModel.DashboardNavigator,
    OnMapReadyCallback {

    lateinit var binding: ActivityDashboardBinding
    lateinit var viewModel: DashboardViewModel
    private val reqCode = 101
    private lateinit var entriesDao: EntriesDao
    private var locationLogs = mutableListOf<Logs>()
    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var mMap: GoogleMap? = null

    companion object {
        var instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.navigator = this

        setupScreen()
    }

    private fun setupScreen() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.gMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initDb()
    }

    private fun initDb() {
        val db: EntriesDatabase =
            Room.databaseBuilder(application, EntriesDatabase::class.java, "appDB")
                .addTypeConverter(Converter()).build()
        entriesDao = db.entriesDao()
    }

    override fun startStopFetchingLocation() {
        if (binding.btnStartStop.text.equals(getString(R.string.start))) {
            binding.btnLogs.visibility = View.INVISIBLE
            locationLogs.clear()
            startTime = System.currentTimeMillis()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    reqCode
                )
            } else requestLoc()
        } else {
            binding.btnLogs.visibility = View.VISIBLE
            stopLocUpdates()
        }
    }

    override fun showLogs() {
        val intent = Intent(this, LogsActivity::class.java)
        startActivity(intent)
    }

    private fun stopLocUpdates() {
        stopTime = System.currentTimeMillis()
        stopLocationUpdates()
        binding.btnStartStop.text = getString(R.string.start)
        addLogsToDB()
    }

    private fun addLogsToDB() {
        if (locationLogs.isNotEmpty()) {
            val newEntry = Entry(startTime, stopTime, locationLogs)
            viewModel.insertData(entriesDao, newEntry)
        }
    }


    private fun requestLoc() {
        binding.btnStartStop.text = getString(R.string.stop)
        requestLocation({ location ->
            run {
                addLocationToList(location)
            }
        }, true)
    }

    private fun locateOnMap(location: Location) {
        val currLoc = LatLng(location.latitude, location.longitude)
        mMap?.addMarker(
            MarkerOptions()
                .position(currLoc)
                .title("Current Location")
        )
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 15f))
    }

    private fun addLocationToList(location: Location): Unit {
        Log.d("locationParam", location.latitude.toString())
        val log = Logs(System.currentTimeMillis(), location.latitude, location.longitude)
        locationLogs.add(log)
        locateOnMap(location)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.uiSettings.isCompassEnabled = false
        mMap!!.uiSettings.isMapToolbarEnabled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == reqCode) {
            requestLoc()
        }
    }

}