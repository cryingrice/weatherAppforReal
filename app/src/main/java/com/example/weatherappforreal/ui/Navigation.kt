package com.example.weatherappforreal.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherappforreal.WeatherViewModel
import com.example.weatherappforreal.ui.screens.CurrentWeatherScreen
import com.example.weatherappforreal.ui.screens.ForecastScreen
import com.example.weatherappforreal.ui.screens.LocationsScreen

@Composable
fun WeatherApp(
    viewModel: WeatherViewModel = viewModel()
) {
    val navController = rememberNavController()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            viewModel.refreshLocation() // Call ViewModel function when permission is granted
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    NavHost(navController = navController, startDestination = "current") {
        composable("current") {
            CurrentWeatherScreen(
                viewModel = viewModel,
                onSeeForecast = { navController.navigate("forecast") },
                onOpenLocations = { navController.navigate("locations") }
            )
        }
        composable("forecast") {
            ForecastScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("locations") {
            LocationsScreen(
                viewModel = viewModel,
                onSelectLocation = { locationQuery ->
                    viewModel.fetchWeather(locationQuery)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}