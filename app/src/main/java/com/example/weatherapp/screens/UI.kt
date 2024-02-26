package com.example.weatherapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.ui.theme.BlueLight

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

@Composable
fun Alert(isShowed: MutableState<Boolean>, onSearchTapped: (String) -> Unit) {
    val temp = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { isShowed.value = false },
        confirmButton = {
            IconButton(onClick = {
                onSearchTapped.invoke(temp.value)
                isShowed.value = false
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_confirm),
                    contentDescription = "confirmSearch"
                )
            }
        },
        dismissButton = {
            IconButton(onClick = { isShowed.value = false }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dismiss),
                    contentDescription = "dismissSearch"
                )
            }
        },
        title = {
            Column {
                Text(text = "Введите название города:")

                TextField(value = temp.value, onValueChange = {
                    temp.value = it
                })
            }
        }
    )
}