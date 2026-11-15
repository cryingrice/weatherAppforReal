package com.example.weatherappforreal.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: LocationData?,
    val current: CurrentWeatherData?,
    val forecast: ForecastData?
)

data class LocationData(
    val name: String?,
    val region: String?
)

data class CurrentWeatherData(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    val humidity: Int,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_dir") val windDir: String?,
    @SerializedName("precip_mm") val precipMm: Double?,
    @SerializedName("pressure_mb") val pressureMb: Double,
    val condition: ConditionData?
)

data class ConditionData(
    val text: String,
    val icon: String,
    val code: Int
)

data class ForecastData(
    val forecastday: List<ForecastDay>?
)

data class ForecastDay(
    val date: String,
    val day: DayData?
)

data class DayData(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("maxwind_kph") val maxWindKph: Double?,
    @SerializedName("avghumidity") val avgHumidity: Double?,
    @SerializedName("totalprecip_mm") val totalPrecipMm: Double?,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int?,
    val condition: ConditionData?
)
