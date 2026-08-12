package com.esey.gamezonetally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Station
import com.esey.gamezonetally.data.Stations
import com.esey.gamezonetally.widget.refreshAllWidgets
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = CounterRepository(applicationContext)

        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    GameZoneScreen(repo = repo)
                }
            }
        }
    }
}

@Composable
fun GameZoneScreen(repo: CounterRepository) {
    val total by repo.totalFlow().collectAsState(initial = 0)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Game Zone Tally",
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(Stations.ALL) { station ->
                StationRow(repo = repo, station = station)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        TotalRow(total = total, repo = repo)
    }
}

@Composable
private fun StationRow(repo: CounterRepository, station: Station) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val count by repo.counterFlow(station).collectAsState(initial = 0)
    val stationColor = Color(station.colorHex)

    fun change(block: suspend () -> Unit) {
        scope.launch {
            block()
            refreshAllWidgets(context)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = station.label, fontSize = 16.sp, color = stationColor)
            Text(text = count.toString(), fontSize = 26.sp, color = Color.Black)
        }
        Button(onClick = { change { repo.decrement(station) } }) { Text("-") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { change { repo.increment(station) } }) { Text("+") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { change { repo.reset(station) } }) { Text("Reset") }
    }
}

@Composable
private fun TotalRow(total: Int, repo: CounterRepository) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Total", fontSize = 16.sp, color = Color.White)
            Text(text = total.toString(), fontSize = 30.sp, color = Color.White)
        }
        Button(onClick = {
            scope.launch {
                repo.resetAll()
                refreshAllWidgets(context)
            }
        }) { Text("Reset All") }
    }
}
