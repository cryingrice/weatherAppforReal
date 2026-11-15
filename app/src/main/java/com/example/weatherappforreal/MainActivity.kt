package com.example.weatherappforreal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.weatherappforreal.ui.WeatherApp
import com.example.weatherappforreal.ui.theme.WeatherAppforRealTheme

val skyGradient = Brush.verticalGradient(
    listOf(Color(0xFF4F0101), Color(0xFF7A0D0D), Color(0xFF77224E))
)
val cardColor = Color.White.copy(alpha = 0.2f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppforRealTheme {
                WeatherApp()
            }
        }
    }
}
