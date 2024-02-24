package com.example.weatherapp.data

data class WeatherModel(
    val city: String,
    val time: String,
    val curTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String
)