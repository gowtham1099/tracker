package com.hackfrenzy.tracker.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hackfrenzy.tracker.R
import com.hackfrenzy.tracker.Tracking
import com.hackfrenzy.tracker.network.ApiClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BackgroundService: Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var orderId: String = "11"

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

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationAddress(location: Location) {
        val params: HashMap<String, String> = HashMap()
        params["latitude"] = location.latitude.toString()
        params["longitude"] = location.longitude.toString()
        GlobalScope.launch {
            ApiClient.getClient.update(orderId, params)
        }
        Handler().postDelayed(Runnable {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                getCurrentLocationAddress(it.result)
            }
        },1000)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, Tracking::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("location", "partify", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Partify location access")
            .setContentText("accessing your location for tracking")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        update()
    }

}