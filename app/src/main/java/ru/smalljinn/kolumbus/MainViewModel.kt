package ru.smalljinn.kolumbus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Place
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    fun createNewPlace() {
        viewModelScope.launch {
            placesRepository.upsertPlace(
                Place.getInitPlace().copy(
                    title = "Aboba drane",
                    description = "I bought Durov and his family!",
                    creationDate = kotlinx.datetime.Clock.System.now(),
                    images = emptyList()
                )
            )
        }
    }
}