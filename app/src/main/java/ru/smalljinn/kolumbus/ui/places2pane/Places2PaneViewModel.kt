package ru.smalljinn.kolumbus.ui.places2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.smalljinn.places.navigation.PlacesRoute
import javax.inject.Inject

const val PLACE_ID_KEY = "selectedPlaceId"

@HiltViewModel
class Places2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val route = savedStateHandle.toRoute<PlacesRoute>()
    val selectedPlaceId: StateFlow<Long?> = savedStateHandle.getStateFlow(
        key = PLACE_ID_KEY,
        initialValue = route.initialPlaceId
    )

    fun selectPlace(placeId: Long) {
        savedStateHandle[PLACE_ID_KEY] = placeId
    }

    fun unselectPlace() {
        savedStateHandle[PLACE_ID_KEY] = null
    }
}