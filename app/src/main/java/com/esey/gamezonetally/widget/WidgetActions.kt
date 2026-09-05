package com.esey.gamezonetally.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.esey.gamezonetally.data.CounterRepository
import com.esey.gamezonetally.data.Groups
import com.esey.gamezonetally.data.Stations

val STATION_ID_PARAM = ActionParameters.Key<String>("station_id")
val GROUP_ID_PARAM = ActionParameters.Key<String>("group_id")

/**
 * Maps a station id to its own dedicated widget class instance.
 * Only the specific station widget + its group's total need updating
 * after a tap — never all 10 widgets, which is slow for no reason.
 */
private fun stationWidget(stationId: String): GlanceAppWidget = when (stationId) {
    "ps5_station_1" -> Ps5Station1Widget()
    "ps5_station_2" -> Ps5Station2Widget()
    "ps5_station_3" -> Ps5Station3Widget()
    "ps5_station_4" -> Ps5Station4Widget()
    "xbox_station_1" -> XboxStation1Widget()
    "xbox_station_2" -> XboxStation2Widget()
    "xbox_station_3" -> XboxStation3Widget()
    "xbox_station_4" -> XboxStation4Widget()
    else -> error("Unknown station id: $stationId")
}

private fun totalWidget(groupId: String): GlanceAppWidget = when (groupId) {
    "ps5" -> Ps5TotalWidget()
    "xbox" -> XboxTotalWidget()
    else -> error("Unknown group id: $groupId")
}

/** Full refresh — used only by the in-app buttons, where speed isn't the concern. */
suspend fun refreshAllWidgets(context: Context) {
    StationWidgetHost.updateAll(context)
    TotalWidgetHost.updateAll(context)
}

/** Fast path for widget taps: update only the one station widget + its group's total. */
private suspend fun refreshStationAndItsTotal(context: Context, stationId: String) {
    stationWidget(stationId).updateAll(context)
    val groupId = Groups.groupOf(stationId).id
    totalWidget(groupId).updateAll(context)
}

/** Fast path for a group's "reset all": update that group's 4 stations + its total only. */
private suspend fun refreshGroupWidgets(context: Context, groupId: String) {
    Groups.byId(groupId).stations.forEach { station -> stationWidget(station.id).updateAll(context) }
    totalWidget(groupId).updateAll(context)
}

class IncrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).increment(Stations.byId(stationId))
        refreshStationAndItsTotal(context, stationId)
    }
}

class DecrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).decrement(Stations.byId(stationId))
        refreshStationAndItsTotal(context, stationId)
    }
}

class ResetStationAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val stationId = parameters[STATION_ID_PARAM] ?: return
        CounterRepository(context).reset(Stations.byId(stationId))
        refreshStationAndItsTotal(context, stationId)
    }
}

/** Used by a Total widget's reset button — resets all 4 stations in that group only. */
class ResetGroupAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val groupId = parameters[GROUP_ID_PARAM] ?: return
        CounterRepository(context).resetGroup(Groups.byId(groupId))
        refreshGroupWidgets(context, groupId)
    }
}
