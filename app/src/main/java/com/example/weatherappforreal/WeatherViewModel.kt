package com.example.weatherappforreal

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappforreal.data.WeatherApiService
import com.example.weatherappforreal.data.WeatherResponse
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val weather: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(app: Application) : AndroidViewModel(app) {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
    var currentLocationQuery by mutableStateOf("Halifax")
        private set

    private val prefs = app.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val _savedLocations = mutableStateOf<List<String>>(loadSavedLocations())
    val savedLocations: List<String> get() = _savedLocations.value

    private fun loadSavedLocations(): List<String> {
        val set = prefs.getStringSet("saved_locations", setOf("Toronto", "Vancouver", "Montreal")) ?: emptySet()
        return set.toList()
    }

    fun addSavedLocation(city: String) {
        if (city.isNotBlank() && !_savedLocations.value.contains(city)) {
            val updated = _savedLocations.value + city.trim()
            _savedLocations.value = updated
            prefs.edit { putStringSet("saved_locations", updated.toSet()) }
        }
    }

    fun removeSavedLocation(city: String) {
        val updated = _savedLocations.value - city
        _savedLocations.value = updated
        prefs.edit { putStringSet("saved_locations", updated.toSet()) }
    }

    init {
        if (hasLocationPermission()) {
            refreshLocation()
        } else {
            fetchWeather("Halifax")
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun refreshLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val query = "${location.latitude},${location.longitude}"
                    currentLocationQuery = query
                    fetchWeather(query)
                    Log.i("WeatherViewModel", "Fetched lastLocation: $query")
                } else {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { currentResult ->
                            if (currentResult != null) {
                                val query = "${currentResult.latitude},${currentResult.longitude}"
                                currentLocationQuery = query
                                fetchWeather(query)
                                Log.i("WeatherViewModel", "Fetched getCurrentLocation: $query")
                            } else {
                                Log.w("WeatherViewModel", "Location result was null, defaulting to Halifax")
                                fetchWeather("Halifax")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("WeatherViewModel", "Failed to get current location", e)
                            fetchWeather("Halifax")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("WeatherViewModel", "Failed to get last location", e)
                fetchWeather("Halifax")
            }
    }

    fun fetchWeather(locationQuery: String = currentLocationQuery) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = WeatherApiService.instance.getWeather(location = locationQuery, days = 7)
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Failed to fetch weather data")
            }
        }
    }
}
