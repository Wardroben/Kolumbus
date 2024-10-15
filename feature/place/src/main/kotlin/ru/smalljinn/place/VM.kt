package ru.smalljinn.place

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Position
import ru.smalljinn.place.navigation.PlaceRoute
import javax.inject.Inject

@HiltViewModel
class VM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
): ViewModel() {
    private val placeId = savedStateHandle.toRoute<PlaceRoute>().id

    val isEditing = savedStateHandle.getStateFlow("isEditing", placeId == null)

    val title = savedStateHandle.getStateFlow("title", "")
    val description = savedStateHandle.getStateFlow("description", "")
    val headerImageId = savedStateHandle.getStateFlow("headerImageId", null)
    val creationTimestamp = savedStateHandle.getStateFlow("creationTimestamp", Clock.System.now().toEpochMilliseconds())

    val imageUrlsToDelete = savedStateHandle.getStateFlow("imagesToDelete", arrayListOf<String>())
    val imageUrls = savedStateHandle.getStateFlow("imageUrls", arrayListOf<String>())

    val userPosition = MutableStateFlow<Position?>(null)
    val placePosition = MutableStateFlow<Position?>(null)

    fun changeTitle(text: String) {
        savedStateHandle["title"] = text
    }

    fun changeDescription(text: String) {
        savedStateHandle["description"] = text
    }

    fun setHeaderImageId(id: Long?) {
        savedStateHandle["headerImageId"] = id
    }

    fun addImage(image: Image) {
        if (imageUrls.value.contains(image.url)) return
        savedStateHandle["imageUrls"] = imageUrls.value.plus(image.url)
    }

    fun removeImage(image: Image) {
        val url = image.url
        if (imageUrls.value.contains(url)) {
            savedStateHandle["imagesToDelete"] = imageUrlsToDelete.value.plus(url)
            savedStateHandle["imageUrls"] = imageUrls.value.minus(url)
        }
    }

    fun cancelEditing() {
        savedStateHandle["isEditing"] = false
    }

    fun startEditing() {
        savedStateHandle["isEditing"] = true
    }
}