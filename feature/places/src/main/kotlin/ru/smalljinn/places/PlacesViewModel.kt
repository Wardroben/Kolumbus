package ru.smalljinn.places

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.kolumbus.data.repository.UserSettingsRepository
import ru.smalljinn.kolumbus.data.util.SyncManager
import ru.smalljinn.model.data.Place
import ru.smalljinn.ui.PlacesUiState
import javax.inject.Inject

const val PLACE_ID_KEY = "selectedPlaceId"

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository,
    syncManager: SyncManager,
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {
    private val selectedPlaceId: StateFlow<Long?> = savedStateHandle.getStateFlow(
        key = PLACE_ID_KEY,
        initialValue = null
    )
    private val userSettings = userSettingsRepository.settings

    private val filteredPlaces = combine(
        placesRepository.getPlacesStream(),
        userSettings.map { it.showOnlyFavoritePlaces }
    ) { places, onlyFavorite ->
        if (onlyFavorite) places.filter { place -> place.favorite }
        else places
    }

    val placesState: StateFlow<PlacesUiState> = combine(
        selectedPlaceId,
        filteredPlaces,
        userSettings,
        syncManager.isSyncing
    ) { selectedPlaceId, places, userSettings, isSyncing ->
        when {
            !userSettings.showOnlyFavoritePlaces && places.isEmpty() -> PlacesUiState.Empty
            else -> PlacesUiState.Success(
                selectedPlaceId = selectedPlaceId,
                places = places,
                useCompactMode = userSettings.useCompactPlaceCardMode,
                isDataSyncing = isSyncing,
                showOnlyFavorite = userSettings.showOnlyFavoritePlaces
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PlacesUiState.Loading
    )

    fun obtainEvent(event: PlaceEvent) {
        when (event) {
            is PlaceEvent.DeletePlace -> viewModelScope.launch {
                placesRepository.deletePlace(event.place)
            }

            is PlaceEvent.MakeFavorite -> with(event) {
                viewModelScope.launch {
                    placesRepository.upsertPlace(
                        place.copy(
                            favorite = favorite,
                            images = emptyList()
                        )
                    )
                }
            }

            is PlaceEvent.SelectPlace -> savedStateHandle[PLACE_ID_KEY] = event.placeId
            is PlaceEvent.DisplayFavorite -> viewModelScope.launch {
                userSettingsRepository.setPlaceFavoriteDisplay(event.onlyFavorite)
            }
        }
    }
}

sealed interface PlaceEvent {
    data class MakeFavorite(val place: Place, val favorite: Boolean) : PlaceEvent
    data class DeletePlace(val place: Place) : PlaceEvent
    data class SelectPlace(val placeId: Long) : PlaceEvent
    data class DisplayFavorite(val onlyFavorite: Boolean) : PlaceEvent
}