package com.example.weatherappforreal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherappforreal.ui.theme.WeatherAppforRealTheme
data class DailyForecast(
    val day: String,
    @DrawableRes val icon: Int,
    val condition: String,
    val high: Int,
    val low: Int
)

val placeholderForecast = listOf(
    DailyForecast("Mon", R.drawable.sunny, "Sunny", 18, 9),
    DailyForecast("Tue", R.drawable.ic_sunny, "Partly Cloudy", 16, 8),
    DailyForecast("Wed", R.drawable.ic_rain, "Rain", 13, 7),
    DailyForecast("Thu", R.drawable.sunny, "Sunny", 12, 6),
    DailyForecast("Fri", R.drawable.ic_cloudy, "Mostly Sunny", 15, 5),
    DailyForecast("Sat", R.drawable.ic_rain, "Rain", 8, 1),
    DailyForecast("Sun", R.drawable.sunny, "Sunny", 14, 3)
)

private val skyGradient = Brush.verticalGradient(
    listOf(Color(0xFF4F0101), Color(0xFF7A0D0D), Color(0xFF77224E))
)
private val cardColor = Color.White.copy(alpha = 0.2f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppforRealTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "current") {
        composable("current") {
            CurrentWeatherScreen(onSeeForecast = { navController.navigate("forecast") })
        }
        composable("forecast") {
            ForecastScreen(onBack = { navController.popBackStack() })
        }
    }
}

// ---------- Screen 1: Current weather ----------
@Composable
fun CurrentWeatherScreen(onSeeForecast: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
            .systemBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),


    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text("Halifax, NS", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Text("Saturday, Oct 10", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)

        Spacer(Modifier.height(32.dp))
        Image(
            painter = painterResource(R.drawable.ic_cloudy),
            contentDescription = "Partly cloudy",
            modifier = Modifier.size(140.dp),
        )
        Text("14°C", color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
        Text("Partly Cloudy", color = Color.White, fontSize = 22.sp)
        Text("Feels like 12°C", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)

        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard("Humidity", "72%", Modifier.weight(1f))
            DetailCard("Wind", "15 km/h", Modifier.weight(1f))
            DetailCard("Pressure", "1015 hPa", Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))
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
            Text("7-Day Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
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

// ---------- Screen 2: Daily forecast ----------
@Composable
fun ForecastScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color.White, fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text("7-Day Forecast", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(placeholderForecast) { day -> ForecastRow(day) }
        }
    }
}

@Composable
fun ForecastRow(day: DailyForecast) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(day.day, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                modifier = Modifier.width(56.dp))
            Image(
                painter = painterResource(day.icon),
                contentDescription = day.condition,
                modifier = Modifier.size(40.dp),

            )
            Text(day.condition, color = Color.White, fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp))
            Text(stringResource(R.string.temperature_format, day.high),
                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(stringResource(R.string.temperature_format, day.low),
                color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp))
        }
    }
}