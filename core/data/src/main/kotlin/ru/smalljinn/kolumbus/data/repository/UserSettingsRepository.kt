package ru.smalljinn.kolumbus.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.datastore.KolumbusPreferencesDataSource
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import javax.inject.Inject

class UserSettingsRepository @Inject constructor(
    private val kolumbusPreferencesDataSource: KolumbusPreferencesDataSource,
    @Dispatcher(KolumbusDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
) {
    val settings = kolumbusPreferencesDataSource.userData
    suspend fun setPlaceCardMode(useCompact: Boolean) = withContext(ioDispatcher) {
        kolumbusPreferencesDataSource.setPlaceCardMode(useCompact)
    }

    suspend fun setPlaceFavoriteDisplay(onlyFavorite: Boolean) = withContext(ioDispatcher) {
        kolumbusPreferencesDataSource.setFavoriteDisplay(onlyFavorite)
    }
}