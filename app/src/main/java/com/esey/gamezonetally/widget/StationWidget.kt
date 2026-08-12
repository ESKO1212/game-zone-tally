package com.esey.gamezonetally.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider as UnitColorProvider
import androidx.compose.ui.unit.dp
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Station
import com.esey.gamezonetally.data.Stations

/**
 * One GlanceAppWidget class, reused by all 8 station receivers (4 PS5 + 4 Xbox).
 * Each receiver tells us which station it is.
 *
 * SizeMode.Exact makes the widget recompose to whatever exact size the launcher
 * gives it, so height resizes continuously instead of snapping to a fixed list.
 */
class StationWidget(private val station: Station) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = CounterRepository(context)
        provideContent {
            val count by repo.counterFlow(station).collectAsState(initial = 0)
            StationContent(station = station, count = count)
        }
    }
}

/** Helper to refresh every station widget on the home screen after a data change. */
object StationWidgetHost {
    suspend fun updateAll(context: Context) {
        Stations.ALL.forEach { station ->
            StationWidget(station).updateAll(context)
        }
    }
}

@Composable
private fun StationContent(station: Station, count: Int) {
    val stationColor = ComposeColor(station.colorHex)
    val params = actionParametersOf(STATION_ID_PARAM to station.id)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ComposeColor.White)
            .padding(8.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = station.label,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitColorProvider(stationColor)
                )
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = "reset",
                modifier = GlanceModifier.clickable(actionRunCallback<ResetStationAction>(params)),
                style = TextStyle(fontSize = 11.sp, color = UnitColorProvider(ComposeColor.Gray))
            )
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(vertical = 4.dp)
                .clickable(actionRunCallback<IncrementAction>(params)),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitColorProvider(ComposeColor.Black),
                    textAlign = TextAlign.Center
                )
            )
        }

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "−",
                modifier = GlanceModifier
                    .clickable(actionRunCallback<DecrementAction>(params))
                    .padding(horizontal = 16.dp),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = GlanceModifier.width(24.dp))
            Text(
                text = "+",
                modifier = GlanceModifier
                    .clickable(actionRunCallback<IncrementAction>(params))
                    .padding(horizontal = 16.dp),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

// PS5 station widget receivers
class Ps5Station1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.PS5.stations[0])
}
class Ps5Station2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.PS5.stations[1])
}
class Ps5Station3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.PS5.stations[2])
}
class Ps5Station4WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.PS5.stations[3])
}

// Xbox station widget receivers
class XboxStation1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.XBOX.stations[0])
}
class XboxStation2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.XBOX.stations[1])
}
class XboxStation3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.XBOX.stations[2])
}
class XboxStation4WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(com.esey.gamezonetally.data.Groups.XBOX.stations[3])
}
