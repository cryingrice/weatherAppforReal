package com.example.weatherappforreal.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherappforreal.R
import com.example.weatherappforreal.WeatherUiState
import com.example.weatherappforreal.WeatherViewModel
import com.example.weatherappforreal.cardColor
import com.example.weatherappforreal.skyGradient

/* ---------- Screen 1: Current weather ---------- */
@Composable
fun CurrentWeatherScreen(
    onSeeForecast: () -> Unit = {},
    onOpenLocations: () -> Unit = {},
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is WeatherUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Failed to load weather data",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.fetchWeather() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Retry", color = Color(0xFF1E3C72))
                    }
                }
            }
            is WeatherUiState.Success -> {
                val location = state.weather.location
                val current = state.weather.current

                val city = location?.name ?: ""
                val region = location?.region ?: ""
                val temp = current?.tempC?.toInt() ?: 0
                val feelsLike = current?.feelsLikeC?.toInt() ?: 0
                val conditionText = current?.condition?.text ?: "Clear"
                val humidity = current?.humidity ?: 0
                val windKph = current?.windKph?.toInt() ?: 0
                val windDir = current?.windDir ?: ""
                val precipMm = current?.precipMm ?: 0.0
                val pressure = current?.pressureMb?.toInt() ?: 0

                val conditionCode = current?.condition?.code ?: 1000
                val iconRes = getWeatherIcon(conditionCode, conditionText)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .systemBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))
                    // Top Bar with Location and Locations Screen Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = " $city, $region",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = onOpenLocations,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Locations", color = Color.White, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = conditionText,
                        modifier = Modifier.size(140.dp)
                    )
                    Text("${temp}°C", color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
                    Text(conditionText, color = Color.White, fontSize = 22.sp)
                    Text("Feels like ${feelsLike}°C", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailCard("Humidity", "${humidity}%", Modifier.weight(1f))
                        DetailCard("Wind", "$windDir $windKph km/h", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailCard("Precipitation", "$precipMm mm", Modifier.weight(1f))
                        DetailCard("Pressure", "$pressure hPa", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onSeeForecast,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1E3C72)
                        )
                    ) {
                        Text("3-Day Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(16.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            context.startActivity(Intent(context, MapActivity::class.java))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.9f),
                            contentColor = Color(0xFF1E3C72)
                        )
                    ) {
                        Text("View Weather Map", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

fun getWeatherIcon(code: Int, conditionText: String? = null): Int {
    return when (code) {
        1000 -> R.drawable.sunny                                  // Clear / Sunny
        1003, 1006, 1009 -> R.drawable.ic_cloudy                 // Partly cloudy, Cloudy, Overcast
        1063, 1150, 1153, 1180, 1183, 1186, 1189, 1192, 1195, 1240, 1243 -> R.drawable.ic_rain // Drizzle / Rain
        else -> {
            val text = conditionText?.lowercase() ?: ""
            when {
                text.contains("rain") || text.contains("drizzle") -> R.drawable.ic_rain
                text.contains("cloud") || text.contains("overcast") -> R.drawable.ic_cloudy
                else -> R.drawable.sunny
            }
        }
    }
}

@Composable
fun DetailCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}
