package com.hackfrenzy.partify.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hackfrenzy.partify.network.ApiClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BackgroundService: Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var orderId: String = "121"

    private fun update() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        checkSettingForGPS()
    }

    @SuppressLint("MissingPermission")
    private fun checkSettingForGPS() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(applicationContext)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {

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
                getCurrentLocationAddress(location)
            }
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    private fun getCurrentLocationAddress(location: Location) {
        val params: HashMap<String, String> = HashMap()
        params["latitude"] = location.latitude.toString()
        params["longitude"] = location.longitude.toString()
        GlobalScope.launch {
            ApiClient.getClient.update(orderId, params)
        }
//        Handler().postDelayed(Runnable { update() },500)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        update()
    }

}