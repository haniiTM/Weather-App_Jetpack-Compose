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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.weatherapp.ui.theme.BlueLight
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun MainScreen(context: Context = MainActivity()) {
    val cityName = "London"
    var cityData by remember {
        mutableStateOf(listOf<WeatherModel>())
    }

    WeatherAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            LaunchedEffect(key1 = Unit) {
                getCityData(context, cityName) {
                    cityData = it
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
                if (cityData.isNotEmpty()) {
                    MainCard(model = cityData.first())
                    TabLayout(models = cityData)
                }
            }
        }
    }
}

@Composable
fun MainCard(model: WeatherModel) {
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
                Text(text = model.time, color = Color.White)

                AsyncImage(
                    model = "https:${model.icon}",
                    contentDescription = "weatherIcon",
                    modifier = Modifier.size(35.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = model.city, color = Color.White, fontSize = 24.sp)
                Text(text = model.curTemp, fontSize = 65.sp, color = Color.White)
                Text(text = model.condition, color = Color.White)
                Text(text = "?/?C", color = Color.White)
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
fun TabLayout(models: List<WeatherModel>) {
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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(models) {
                    ListItem(item = it)
                }
            }
        }
    }
}

@Composable
fun ListItem(item: WeatherModel) {
    Card(colors = CardDefaults.cardColors(BlueLight)) {
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