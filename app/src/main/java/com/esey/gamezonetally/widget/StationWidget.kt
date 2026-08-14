package com.esey.gamezonetally.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.dp
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
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Groups
import com.esey.gamezonetally.data.Station

/**
 * IMPORTANT: Glance tracks widget instances by CLASS, not by constructor
 * arguments. Each physical home-screen widget must have its own distinct
 * GlanceAppWidget subclass — reusing one class with different constructor
 * args causes all widgets of that class to collapse onto whichever data
 * was rendered last. That's why there are 8 concrete subclasses below
 * instead of one class instantiated 8 times.
 */
abstract class BaseStationWidget(private val station: Station) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = CounterRepository(context)
        provideContent {
            val count by repo.counterFlow(station).collectAsState(initial = 0)
            StationContent(station = station, count = count)
        }
    }
}

class Ps5Station1Widget : BaseStationWidget(Groups.PS5.stations[0])
class Ps5Station2Widget : BaseStationWidget(Groups.PS5.stations[1])
class Ps5Station3Widget : BaseStationWidget(Groups.PS5.stations[2])
class Ps5Station4Widget : BaseStationWidget(Groups.PS5.stations[3])

class XboxStation1Widget : BaseStationWidget(Groups.XBOX.stations[0])
class XboxStation2Widget : BaseStationWidget(Groups.XBOX.stations[1])
class XboxStation3Widget : BaseStationWidget(Groups.XBOX.stations[2])
class XboxStation4Widget : BaseStationWidget(Groups.XBOX.stations[3])

/** Refreshes every station widget on the home screen after a data change. */
object StationWidgetHost {
    suspend fun updateAll(context: Context) {
        Ps5Station1Widget().updateAll(context)
        Ps5Station2Widget().updateAll(context)
        Ps5Station3Widget().updateAll(context)
        Ps5Station4Widget().updateAll(context)
        XboxStation1Widget().updateAll(context)
        XboxStation2Widget().updateAll(context)
        XboxStation3Widget().updateAll(context)
        XboxStation4Widget().updateAll(context)
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
                .fillMaxWidth()
                .defaultWeight()
                .clickable(actionRunCallback<IncrementAction>(params)),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = count.toString(),
                style = TextStyle(
                    fontSize = 24.sp,
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

class Ps5Station1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Ps5Station1Widget()
}
class Ps5Station2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Ps5Station2Widget()
}
class Ps5Station3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Ps5Station3Widget()
}
class Ps5Station4WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = Ps5Station4Widget()
}

class XboxStation1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = XboxStation1Widget()
}
class XboxStation2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = XboxStation2Widget()
}
class XboxStation3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = XboxStation3Widget()
}
class XboxStation4WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = XboxStation4Widget()
}
