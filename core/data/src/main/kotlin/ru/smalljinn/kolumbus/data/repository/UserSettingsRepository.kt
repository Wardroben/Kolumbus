package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.datastore.KolumbusPreferencesDataSource
import javax.inject.Inject

class UserSettingsRepository @Inject constructor(
    private val kolumbusPreferencesDataSource: KolumbusPreferencesDataSource
) {
    val settings = kolumbusPreferencesDataSource.userData
    suspend fun setPlaceCardMode(useCompact: Boolean) = withContext(Dispatchers.IO) {
        kolumbusPreferencesDataSource.setPlaceCardMode(useCompact)
    }
}