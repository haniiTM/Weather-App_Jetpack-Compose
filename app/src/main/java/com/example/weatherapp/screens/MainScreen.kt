package com.example.weatherapp.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.getCityData
import com.example.weatherapp.ui.theme.WeatherAppTheme

@Preview(showBackground = true)
@Composable
fun MainScreen(context: Context = MainActivity()) {
    val cityName = "London"
    val allDays = remember {
        mutableStateOf(listOf<WeatherModel>())
    }
    val curDay = remember {
        mutableStateOf(
            WeatherModel(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
            )
        )
    }

    WeatherAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            LaunchedEffect(key1 = Unit) {
                getCityData(context, cityName) {
                    allDays.value = it
                    curDay.value = it.first()
                }
            }

            Image(
                painter = painterResource(id = R.drawable.weather_bg),
                contentDescription = "background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (allDays.value.isNotEmpty()) {
                    MainCard(model = curDay)
                    TabLayout(allDays = allDays, curDay = curDay)
                }
            }
        }
    }
}