package com.example.weatherapp.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.getWeatherByHours
import com.example.weatherapp.ui.theme.BlueLight
import kotlinx.coroutines.launch

@Composable
fun MainCard(model: MutableState<WeatherModel>) {
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
                Text(text = model.value.time, color = Color.White)

                AsyncImage(
                    model = "https:${model.value.icon}",
                    contentDescription = "weatherIcon",
                    modifier = Modifier.size(35.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = model.value.city, color = Color.White, fontSize = 24.sp)
                Text(
                    text = if (model.value.curTemp.isNotEmpty()) {
                        model.value.curTemp
                    } else {
                        ((model.value.maxTemp.toInt() + model.value.minTemp.toInt()) / 2).toString()
                    } + "C", fontSize = 65.sp, color = Color.White
                )
                Text(text = model.value.condition, color = Color.White)
                Text(text = "${model.value.minTemp}/${model.value.maxTemp}C", color = Color.White)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "search",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sync),
                        contentDescription = "search",
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

@Composable
fun MainList(allDays: List<WeatherModel>, curDay: MutableState<WeatherModel>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(allDays) {
            ListItem(item = it, curDay = curDay)
        }
    }
}

@Composable
fun ListItem(item: WeatherModel, curDay: MutableState<WeatherModel>) {
    Card(
        colors = CardDefaults.cardColors(BlueLight),
        modifier = Modifier.clickable {
            if (curDay.value.hours == "" || curDay.value.hours.isEmpty()) return@clickable
            curDay.value = item

            Log.d("TAG", "ListItem: ${curDay.value.hours}")
        }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(text = item.time)
                Text(text = item.condition, color = Color.White)
            }

            Text(
                text = item.curTemp.ifEmpty { "${item.minTemp}/${item.maxTemp}C" },
                color = Color.White,
                fontSize = 25.sp
            )

            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "weatherIcon",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}