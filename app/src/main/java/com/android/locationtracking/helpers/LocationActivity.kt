package com.android.locationtracking.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.OnSuccessListener

abstract class LocationActivity : FragmentActivity() {
    private var mLocationRequest: LocationRequest? = null
    private val MY_LOCATION_REQUEST_CODE = 1
    private val REQUEST_CHECK_SETTINGS = 2
    private var context: Context? = null
    var currentLocationListener: LocationListener? = null
    private var isLooping = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /* Uncomment this if you want to enable location on Activity start*/

        // requestLocation();
    }

    fun getLastLocation(
        currentLocationListener: LocationListener?,
        isLooping: Boolean
    ) {
        this.currentLocationListener = currentLocationListener
        //        permissionsUtil = new PermissionsUtil(LocationActivity.this);
        this.isLooping = isLooping

    }

    fun requestLocation(
        currentLocationListener: LocationListener?,
        isLooping: Boolean
    ) {
        this.isLooping = isLooping
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 30000
        mLocationRequest!!.fastestInterval = 30000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task =
            client.checkLocationSettings(builder.build())
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        if (!isLooping) stopLocationUpdates()
                        currentLocationListener?.onLocationChanged(
                            location
                        )
                        break
                    }
                }
            }
        }
        task.addOnSuccessListener(this, OnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    this@LocationActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@LocationActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@OnSuccessListener
            }
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                null
            )
        })
        task.addOnFailureListener(this) { e ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->                         // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@LocationActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: SendIntentException) {
                        // Ignore the error.
                    }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    public fun stopLocationUpdates() {
        if (mLocationCallback != null) mFusedLocationClient!!.removeLocationUpdates(
            mLocationCallback
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                requestLocation(currentLocationListener, isLooping)
//                DashboardActivity.permissionRequested = true
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                finish()
                Toast.makeText(this, "Location access denied.", Toast.LENGTH_LONG).show()
            }
        }
    } //    @Override
    //    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //    }
}
