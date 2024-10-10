package ru.smalljinn.place

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.response.PlaceError
import ru.smalljinn.model.data.response.Result
import ru.smalljinn.place.navigation.PlaceRoute
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    private val placeId = savedStateHandle.toRoute<PlaceRoute>().id
    private val isEditing = MutableStateFlow(placeId == null)

    val placeState: StateFlow<PlaceUiState> = combine(
        placesRepository.getPlace(placeId),
        isEditing,
        ::Pair
    ).asResult()
        .map { placeResult ->
            when (placeResult) {
                is Result.Error -> PlaceUiState.Error
                is Result.Success -> PlaceUiState.Success(
                    place = placeResult.data.first ?: Place.getInitPlace(),
                    isEditing = placeResult.data.second
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            PlaceUiState.Loading
        )

    internal fun obtainEvent(event: PlaceEvent) {
        when (event) {
            PlaceEvent.CancelEditing -> {
                isEditing.update { false }
                //TODO
            }

            PlaceEvent.EditPlace -> isEditing.update { true }
            PlaceEvent.SavePlace -> {
                viewModelScope.launch {
                    TODO("save place")

                    //placesRepository.upsertPlace()
                    //isEditing.update { false }
                }
            }

            PlaceEvent.DeletePlace -> {
                if (placeId == null) return
                viewModelScope.launch {
                    placesRepository.deletePlaceById(placeId)
                }
            }
        }
    }
}

internal sealed interface PlaceEvent {
    data object SavePlace : PlaceEvent
    data object EditPlace : PlaceEvent
    data object DeletePlace : PlaceEvent
    data object CancelEditing : PlaceEvent

}

private fun <D> Flow<D>.asResult(): Flow<Result<D, PlaceError>> =
    map<D, Result<D, PlaceError>> { Result.Success(it) }
        .catch { emit(Result.Error(PlaceError.UNKNOWN)) }

sealed interface PlaceUiState {
    data class Success(val place: Place, val isEditing: Boolean) : PlaceUiState
    data object Loading : PlaceUiState
    data object Error : PlaceUiState
}