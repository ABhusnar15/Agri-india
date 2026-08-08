package com.agriindia.app.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val stateName: String,
    val displayLocation: String
)

class LocationRepository(private val context: Context) {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentGpsLocation(): GpsLocation? = withContext(Dispatchers.IO) {
        try {
            val cancellationTokenSource = CancellationTokenSource()
            val location: Location? = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await() ?: fusedLocationClient.lastLocation.await()

            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = try {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                } catch (e: Exception) {
                    null
                }

                val city = addresses?.firstOrNull()?.locality
                    ?: addresses?.firstOrNull()?.subAdminArea
                    ?: addresses?.firstOrNull()?.adminArea
                    ?: "Farm Site"
                val state = addresses?.firstOrNull()?.adminArea ?: "India"
                val formatted = "$city, $state"

                return@withContext GpsLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = city,
                    stateName = state,
                    displayLocation = formatted
                )
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
