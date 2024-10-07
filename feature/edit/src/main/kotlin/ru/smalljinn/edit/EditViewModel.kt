package ru.smalljinn.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import ru.smalljinn.edit.navigation.EditPlaceRoute
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import javax.inject.Inject

class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
): ViewModel() {
    val placeId = savedStateHandle.toRoute<EditPlaceRoute>().placeId
}