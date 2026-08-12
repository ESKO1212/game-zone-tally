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

object Stations {
    val STATION_1 = Station("station_1", "Station 1", 0xFFE53935, intPreferencesKey("counter_1")) // red
    val STATION_2 = Station("station_2", "Station 2", 0xFF1E88E5, intPreferencesKey("counter_2")) // blue
    val STATION_3 = Station("station_3", "Station 3", 0xFF43A047, intPreferencesKey("counter_3")) // green
    val STATION_4 = Station("station_4", "Station 4", 0xFFFB8C00, intPreferencesKey("counter_4")) // orange

    val ALL = listOf(STATION_1, STATION_2, STATION_3, STATION_4)

    fun byId(id: String): Station = ALL.first { it.id == id }
}

class CounterRepository(private val context: Context) {

    fun counterFlow(station: Station): Flow<Int> =
        context.dataStore.data.map { it[station.key] ?: 0 }

    fun totalFlow(): Flow<Int> =
        context.dataStore.data.map { prefs ->
            Stations.ALL.sumOf { s -> prefs[s.key] ?: 0 }
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

    suspend fun resetAll() {
        context.dataStore.edit { prefs ->
            Stations.ALL.forEach { s -> prefs[s.key] = 0 }
        }
    }
}
