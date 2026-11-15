package com.example.weatherappforreal.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.weatherappforreal.R
import com.google.android.gms.location.LocationServices
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

class MapActivity : ComponentActivity() {

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private var deviceLocation by mutableStateOf<Point?>(null)
    private var isLoadingLocation by mutableStateOf(true)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            fetchDeviceLocation()
        } else {
            isLoadingLocation = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)

        checkAndRequestLocationPermission()

        setContent {
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(12.0)
                    deviceLocation?.let { center(it) } ?: center(Point.fromLngLat(-98.0, 39.5))
                }
            }

            // Update camera when location changes
            LaunchedEffect(deviceLocation) {
                deviceLocation?.let { loc ->
                    mapViewportState.setCameraOptions {
                        center(loc)
                        zoom(14.0)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    scaleBar = { ScaleBar(Modifier.padding(top = 60.dp)) },
                    logo = { Logo(Modifier.padding(bottom = 40.dp)) },
                    attribution = { Attribution(Modifier.padding(bottom = 40.dp)) }
                ) {
                    deviceLocation?.let { point ->
                        PointAnnotation(point = point)
                    }
                }

                if (isLoadingLocation) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    private fun checkAndRequestLocationPermission() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchDeviceLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchDeviceLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            isLoadingLocation = false
            if (location != null) {
                deviceLocation = Point.fromLngLat(location.longitude, location.latitude)
            }
        }.addOnFailureListener {
            isLoadingLocation = false
        }
    }
}
