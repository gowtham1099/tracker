package com.hackfrenzy.partify

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hackfrenzy.partify.service.BackgroundService
import com.hackfrenzy.partify.viewmodel.OrderTrackingViewModel
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

class Tracking : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var latitude: String = ""
    private var longitude: String = ""
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startTrip("121")
    }

    private fun startTrip(id: String) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.create()
            locationRequest.interval = 1000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            checkSettingForGPS()
            orderId = id
            getCurrentLocationAddress()
        }else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 123)
        }
    }

    fun stopTrip(orderId: String) {

    }


    @SuppressLint("MissingPermission")
    private fun checkSettingForGPS() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, 101)
                } catch (ex: IntentSender.SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations) {

            }
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    private fun getCurrentLocationAddress() {

        val params: HashMap<String, String> = HashMap()
        params["orderId"] = orderId
        params["latitude"] = "8793423432"
        params["longitude"] = "1354343532"

        val viewModel = ViewModelProvider(this).get(OrderTrackingViewModel::class.java)
        viewModel.setStartTrip(params)

        viewModel.getStartTripStatus().observe(this, {t->
            if (t.message.equals("success", true)){
                startTripTracking()
            }
        })
    }

    private fun startTripTracking() {
        startService(Intent(this, BackgroundService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123){

        }
    }

}