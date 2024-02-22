package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.screens.MainScreen
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

fun getCityData(context: Context, city: String, data: (String) -> Unit) {
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY" +
            "&q=$city" +
            "&aqi=no"

    val request = StringRequest(Method.GET, url,
        {
            val json = JSONObject(it)
            val currentJSON = json.getJSONObject("current")
            val temp = currentJSON.getString("temp_c")

            data(temp)
        },
        {
            data("Error!")
        })

    val queue = Volley.newRequestQueue(context)
    queue.add(request)
}