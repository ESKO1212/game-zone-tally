package com.esey.gamezonetally.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Groups
import com.esey.gamezonetally.data.Stations

val STATION_ID_PARAM = ActionParameters.Key<String>("station_id")
val GROUP_ID_PARAM = ActionParameters.Key<String>("group_id")

/** Updates every station widget plus both total widgets. Call after any counter changes. */
suspend fun refreshAllWidgets(context: Context) {
    StationWidgetHost.updateAll(context)
    TotalWidgetHost.updateAll(context)
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

/** Used by a Total widget's reset button — resets all 4 stations in that group only. */
class ResetGroupAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val groupId = parameters[GROUP_ID_PARAM] ?: return
        CounterRepository(context).resetGroup(Groups.byId(groupId))
        refreshAllWidgets(context)
    }
}
