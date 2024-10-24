package ru.smalljinn.kolumbus.ui.places2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.smalljinn.place.usecase.DeletePlaceUseCase
import ru.smalljinn.places.navigation.PlacesRoute
import javax.inject.Inject

const val PLACE_ID_KEY = "selectedPlaceId"

@HiltViewModel
class Places2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deletePlaceUseCase: DeletePlaceUseCase
) : ViewModel() {
    private val route = savedStateHandle.toRoute<PlacesRoute>()

    val selectedPlaceId: StateFlow<Long?> = savedStateHandle.getStateFlow(
        key = PLACE_ID_KEY,
        initialValue = route.initialPlaceId
    )

    private val placeToDelete = MutableStateFlow<Place2PaneState.PlaceToDelete?>(null)

    val state = combine(selectedPlaceId, placeToDelete) { selectedPlaceId, placeToDelete ->
        Place2PaneState(
            selectedPlaceId = selectedPlaceId,
            placeToDelete = placeToDelete
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Place2PaneState()
    )

    fun clearPlaceToDelete() = placeToDelete.update { null }

    fun selectPlace(placeId: Long) {
        savedStateHandle[PLACE_ID_KEY] = placeId
    }

    fun setToDeletePlace(id: Long, title: String) {
        placeToDelete.update { Place2PaneState.PlaceToDelete(id, title) }
    }

    fun deletePlace() {
        if (state.value.placeToDelete == null) return
        viewModelScope.launch {
            deletePlaceUseCase(state.value.placeToDelete?.id ?: return@launch)
            savedStateHandle[PLACE_ID_KEY] = null
            clearPlaceToDelete()
        }
    }
}

data class Place2PaneState(
    val selectedPlaceId: Long? = null,
    val placeToDelete: PlaceToDelete? = null,
    val showDeletePlaceDialog: Boolean = selectedPlaceId != null
) {
    data class PlaceToDelete(val id: Long, val title: String)
}
