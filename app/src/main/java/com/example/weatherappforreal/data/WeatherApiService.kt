package com.example.weatherappforreal.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String = "3704d133f75d420d8ab05819263010",
        @Query("q") location: String = "Halifax",
        @Query("days") days: Int = 7
    ): WeatherResponse

    companion object {
        private const val BASE_URL = "https://api.weatherapi.com/"

        val instance: WeatherApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}
