package com.esey.gamezonetally.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Stations

val STATION_ID_PARAM = ActionParameters.Key<String>("station_id")

/** Updates every station widget plus the total widget. Call after any counter changes. */
suspend fun refreshAllWidgets(context: Context) {
    StationWidget.updateAll(context)
    TotalWidget().updateAll(context)
}

class IncrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).increment(Stations.byId(stationId))
        refreshAllWidgets(context)
    }
}

class DecrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).decrement(Stations.byId(stationId))
        refreshAllWidgets(context)
    }
}

class ResetStationAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).reset(Stations.byId(stationId))
        refreshAllWidgets(context)
    }
}

/** Used by the Total widget's reset button — resets all 4 stations, since Total is a live sum. */
class ResetAllAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        CounterRepository(context).resetAll()
        refreshAllWidgets(context)
    }
}
