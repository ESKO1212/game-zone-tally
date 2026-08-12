package com.esey.gamezonetally.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "counters")

/**
 * Static station definitions. Names/colors are hardcoded for now —
 * creating/editing/deleting stations is a future feature.
 */
data class Station(
    val id: String,
    val label: String,
    val colorHex: Long, // ARGB, e.g. 0xFFE53935
    val key: Preferences.Key<Int>
)

data class Group(
    val id: String,
    val label: String,
    val stations: List<Station>
)

object Groups {
    val PS5 = Group(
        id = "ps5",
        label = "PS5",
        stations = listOf(
            Station("ps5_station_1", "Station 1", 0xFFE53935, intPreferencesKey("ps5_counter_1")), // red
            Station("ps5_station_2", "Station 2", 0xFF1E88E5, intPreferencesKey("ps5_counter_2")), // blue
            Station("ps5_station_3", "Station 3", 0xFF43A047, intPreferencesKey("ps5_counter_3")), // green
            Station("ps5_station_4", "Station 4", 0xFFFB8C00, intPreferencesKey("ps5_counter_4"))  // orange
        )
    )

    val XBOX = Group(
        id = "xbox",
        label = "Xbox",
        stations = listOf(
            Station("xbox_station_1", "Station 1", 0xFF8E24AA, intPreferencesKey("xbox_counter_1")), // purple
            Station("xbox_station_2", "Station 2", 0xFF00897B, intPreferencesKey("xbox_counter_2")), // teal
            Station("xbox_station_3", "Station 3", 0xFFC0CA33, intPreferencesKey("xbox_counter_3")), // lime
            Station("xbox_station_4", "Station 4", 0xFF6D4C41, intPreferencesKey("xbox_counter_4"))  // brown
        )
    )

    val ALL = listOf(PS5, XBOX)

    fun byId(id: String): Group = ALL.first { it.id == id }
}

object Stations {
    val ALL = Groups.ALL.flatMap { it.stations }

    fun byId(id: String): Station = ALL.first { it.id == id }
}

class CounterRepository(private val context: Context) {

    fun counterFlow(station: Station): Flow<Int> =
        context.dataStore.data.map { it[station.key] ?: 0 }

    fun totalFlow(group: Group): Flow<Int> =
        context.dataStore.data.map { prefs ->
            group.stations.sumOf { s -> prefs[s.key] ?: 0 }
        }

    suspend fun currentValue(station: Station): Int =
        context.dataStore.data.map { it[station.key] ?: 0 }.first()

    suspend fun increment(station: Station, step: Int = 1) {
        context.dataStore.edit { prefs ->
            prefs[station.key] = (prefs[station.key] ?: 0) + step
        }
    }

    suspend fun decrement(station: Station, step: Int = 1) {
        context.dataStore.edit { prefs ->
            val current = prefs[station.key] ?: 0
            prefs[station.key] = maxOf(0, current - step)
        }
    }

    suspend fun reset(station: Station) {
        context.dataStore.edit { prefs -> prefs[station.key] = 0 }
    }

    suspend fun resetGroup(group: Group) {
        context.dataStore.edit { prefs ->
            group.stations.forEach { s -> prefs[s.key] = 0 }
        }
    }
}
