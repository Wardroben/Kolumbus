package ru.smalljinn.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import ru.smalljinn.model.data.UserSettingsData
import javax.inject.Inject

private const val TAG = "KolumbusPreferences"

class KolumbusPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {
    private val USE_COMPACT_PLACE_CARD_MODE = booleanPreferencesKey("place_card_compact_mode")
    private val SHOW_ONLY_FAVORITE_PLACES = booleanPreferencesKey("show_only_favorite_places")

    val userData = userPreferences.data.map { preferences ->
        UserSettingsData(
            useCompactPlaceCardMode = preferences[USE_COMPACT_PLACE_CARD_MODE] ?: false,
            showOnlyFavoritePlaces = preferences[SHOW_ONLY_FAVORITE_PLACES] ?: false
        )
    }

    suspend fun setPlaceCardMode(useCompact: Boolean) {
        try {
            userPreferences.edit {
                it[USE_COMPACT_PLACE_CARD_MODE] = useCompact
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update place card mode", e)
        }
    }

    suspend fun setFavoriteDisplay(onlyFavorite: Boolean) {
        try {
            userPreferences.edit {
                it[SHOW_ONLY_FAVORITE_PLACES] = onlyFavorite
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to update favorite display card mode", e)
        }
    }
}