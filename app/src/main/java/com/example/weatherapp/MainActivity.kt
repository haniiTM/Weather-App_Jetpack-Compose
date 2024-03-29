package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.screens.MainScreen
import org.json.JSONArray
import org.json.JSONObject

const val API_KEY = "8062d461d3d74c9dafe144513242002"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(this)
        }
    }
}

fun getCityData(context: Context, city: String, data: (List<WeatherModel>) -> Unit) {
    val url = "https://api.weatherapi.com/v1/forecast.json" +
            "?key=$API_KEY" +
            "&q=$city" +
            "&days=2" +
            "&aqi=no" +
            "&alerts=no"

    val request = StringRequest(Method.GET, url,
        {
            val days = getWeatherByDays(it)
            data(days)
        },
        {
            Log.d("TAG", "getCityData: Error!")
        })

    val queue = Volley.newRequestQueue(context)
    queue.add(request)
}

fun getWeatherByDays(data: String): List<WeatherModel> {
    data.ifEmpty { return listOf() }

    val obj = JSONObject(data)
    val models = arrayListOf<WeatherModel>()

//    Current Weather
//    location
    val location = obj.getJSONObject("location")
    val name = location.getString("name")

//    current
    val current = obj.getJSONObject("current")
    val last_updated = current.getString("last_updated")
    val temp_c = current.getInt("temp_c")

//    forecast
    val forecast = obj.getJSONObject("forecast")
    val forecastdays = forecast.getJSONArray("forecastday")

    for (i in 0 until forecastdays.length()) {
        val forecastday = forecastdays[i] as JSONObject
        val date = forecastday.getString("date")

//        day
        val day = forecastday.getJSONObject("day")
        val maxtemp_c = day.getInt("maxtemp_c")
        val mintemp_c = day.getInt("mintemp_c")

///        condition
        val condition = day.getJSONObject("condition")
        val text = condition.getString("text")
        val icon = condition.getString("icon")

//        hour
        val hours = forecastday.getJSONArray("hour").toString()

//        model
        val model = WeatherModel(
            name,
            date,
            "",
            text,
            icon,
            maxtemp_c.toString(),
            mintemp_c.toString(),
            hours
        )
        models.add(model)
    }

    models[0] = models[0].copy(time = last_updated, curTemp = temp_c.toString())
    return models
}

fun getWeatherByHours(data: String): List<WeatherModel> {
    data.ifEmpty { return listOf() }

    val models = ArrayList<WeatherModel>()
    val hours = JSONArray(data)

    for (i in 0 until hours.length()) {
        val hour = hours[i] as JSONObject

        val time = hour.getString("time")
        val temp_c = hour.getInt("temp_c")

//        condition
        val condition = hour.getJSONObject("condition")
        val text = condition.getString("text")
        val icon = condition.getString("icon")

//        model
        val model = WeatherModel("", time, temp_c.toString(), text, icon, "", "", "")
        models.add(model)
    }

    return models
}