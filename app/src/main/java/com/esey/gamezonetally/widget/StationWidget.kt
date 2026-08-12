package com.esey.gamezonetally.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider as UnitColorProvider
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Station
import com.esey.gamezonetally.data.Stations
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.sp

/**
 * One GlanceAppWidget class, reused by all 4 station receivers.
 * Each receiver tells us which station it is via [StationWidget.forStation].
 */
class StationWidget(private val station: Station) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 90.dp),
            DpSize(180.dp, 110.dp),
            DpSize(250.dp, 180.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = CounterRepository(context)
        provideContent {
            val count by repo.counterFlow(station).collectAsState(initial = 0)
            StationContent(station = station, count = count)
        }
    }

    companion object {
        /** Call after a data change to refresh every station widget on the home screen. */
        suspend fun updateAll(context: Context) {
            Stations.ALL.forEach { station ->
                StationWidget(station).updateAll(context)
            }
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
        // Top row: label + reset
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

        // Middle: big tappable count (tap anywhere here to +1)
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(actionRunCallback<IncrementAction>(params)),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
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

        // Bottom row: minus / plus
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

class Station1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(Stations.STATION_1)
}

class Station2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(Stations.STATION_2)
}

class Station3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(Stations.STATION_3)
}

class Station4WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StationWidget(Stations.STATION_4)
}
