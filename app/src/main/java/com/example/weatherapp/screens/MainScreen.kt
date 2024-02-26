package com.example.weatherapp.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.getCityData
import com.example.weatherapp.getWeatherByHours
import com.example.weatherapp.ui.theme.BlueLight
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun MainScreen(context: Context = MainActivity()) {
    val cityName = "London"
    val isAlertShowed = remember {
        mutableStateOf(false)
    }
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
                    MainCard(
                        curDay = curDay,
                        onSyncTapped = {
                            getCityData(context, cityName) {
                                allDays.value = it
                                curDay.value = it.first()
                            }
                        },
                        onSearchTapped = { isAlertShowed.value = true }
                    )
                    TabLayout(allDays = allDays, curDay = curDay)
                }
            }

            if (isAlertShowed.value) {
                Alert(isShowed = isAlertShowed) { textField ->
                    getCityData(context, textField) {
                        allDays.value = it
                        curDay.value = it.first()
                    }
                }
            }
        }
    }
}

@Composable
fun MainCard(
    curDay: MutableState<WeatherModel>,
    onSyncTapped: () -> Unit,
    onSearchTapped: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(BlueLight),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = curDay.value.time, color = Color.White)

                AsyncImage(
                    model = "https:${curDay.value.icon}",
                    contentDescription = "weatherIcon",
                    modifier = Modifier.size(35.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = curDay.value.city, color = Color.White, fontSize = 24.sp)
                Text(
                    text = if (curDay.value.curTemp.isNotEmpty()) {
                        curDay.value.curTemp
                    } else {
                        ((curDay.value.maxTemp.toInt() + curDay.value.minTemp.toInt()) / 2).toString()
                    } + "C", fontSize = 65.sp, color = Color.White
                )
                Text(text = curDay.value.condition, color = Color.White)
                Text(text = "${curDay.value.minTemp}/${curDay.value.maxTemp}C", color = Color.White)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onSearchTapped.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "search",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        onSyncTapped.invoke()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sync),
                        contentDescription = "sync",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(allDays: MutableState<List<WeatherModel>>, curDay: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState {
        tabList.size
    }
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = BlueLight,
            contentColor = Color.White,
            indicator = { pos ->
                TabRowDefaults.Indicator(
                    color = Color.White,
                    modifier = Modifier.tabIndicatorOffset(pos[tabIndex])
                )
            }
        ) {
            tabList.forEachIndexed { index, s ->
                Tab(selected = false, text = { Text(text = s) }, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                })
            }
        }

        HorizontalPager(state = pagerState) {
            val list = when (it) {
                0 -> getWeatherByHours(curDay.value.hours)
                else -> allDays.value
            }

            MainList(allDays = list, curDay = curDay)
        }
    }
}