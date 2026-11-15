package com.example.weatherappforreal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherappforreal.WeatherUiState
import com.example.weatherappforreal.WeatherViewModel
import com.example.weatherappforreal.cardColor
import com.example.weatherappforreal.data.ForecastDay
import com.example.weatherappforreal.skyGradient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ForecastScreen(
    onBack: () -> Unit = {},
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top Bar with Back Button and Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "‹ Back",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "3-Day Forecast",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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
                            text = "Failed to load forecast data",
                            color = Color.White,
                            fontSize = 18.sp,
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
                    val city = location?.name ?: "Halifax"
                    val region = location?.region ?: "NS"
                    val forecastDays = state.weather.forecast?.forecastday ?: emptyList()

                    // Display selected location subtitle
                    Text(
                        text = "$city, $region",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    if (forecastDays.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No forecast data available",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(forecastDays) { forecastDay ->
                                ForecastItemCard(forecastDay = forecastDay)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItemCard(forecastDay: ForecastDay) {
    val dateText = formatDate(forecastDay.date)
    val dayData = forecastDay.day
    val conditionText = dayData?.condition?.text ?: "Clear"
    val maxTemp = dayData?.maxTempC?.toInt() ?: 0
    val minTemp = dayData?.minTempC?.toInt() ?: 0
    val maxWind = dayData?.maxWindKph?.toInt() ?: 0
    val humidity = dayData?.avgHumidity?.toInt() ?: 0
    val rainChance = dayData?.dailyChanceOfRain ?: 0
    val precipMm = dayData?.totalPrecipMm ?: 0.0

    val conditionCode = dayData?.condition?.code ?: 1000
    val iconRes = getWeatherIcon(conditionCode, conditionText)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = conditionText,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Image(
                    painter = painterResource(iconRes),
                    contentDescription = conditionText,
                    modifier = Modifier
                        .size(44.dp)
                        .padding(horizontal = 4.dp)
                )

                Spacer(Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${maxTemp}°",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${minTemp}°",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            // Details: Max Wind, Precipitation, Humidity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💨 Max Wind: $maxWind km/h",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    text = "🌧️ Rain: $rainChance% ($precipMm mm)",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    text = "💧 Hum: $humidity%",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val parsedDate = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())
        parsedDate.format(formatter)
    } catch (_: Exception) {
        dateString
    }
}
