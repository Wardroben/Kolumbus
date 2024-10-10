package ru.smalljinn.place

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.smalljinn.kolumbus.data.repository.ImageRepository
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position
import ru.smalljinn.model.data.response.PlaceError
import ru.smalljinn.model.data.response.Result
import ru.smalljinn.permissions.PermissionManager
import ru.smalljinn.place.navigation.PlaceRoute
import javax.inject.Inject

private const val TAG = "PlaceVM"

@HiltViewModel
class CreatePlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository,
    private val imageRepository: ImageRepository,
    private val permissionManager: PermissionManager
) : ViewModel() {
    private val initialMode = when (val placeId = savedStateHandle.toRoute<PlaceRoute>().id) {
        null -> InitialMode.Creation
        else -> InitialMode.View(placeId)
    }

    private lateinit var initialPlace: Place

    val permissionState = permissionManager.state

    init {
        if (initialMode is InitialMode.View)
            viewModelScope.launch {
                val place = placesRepository.getPlace(initialMode.placeId).first()!!
                initialPlace = place
                title = place.title
                description = place.description
                _images.update { it.plus(place.images) }
                _placePosition.update { place.position }
                _headerImageId.update { place.headerImageId }
                _creationDate.update { place.creationDate }
            }
        else initialPlace = Place.getInitPlace()
    }

    private val _isEditing = MutableStateFlow(initialMode is InitialMode.Creation)
    val isEditing = _isEditing.asStateFlow()

    private var title by mutableStateOf("")
    private var description by mutableStateOf("")

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    private val _deletedImages = mutableListOf<Image>()
    private val _placePosition = MutableStateFlow<Position?>(null)

    //TODO make dataStore where save last user position
    private val _userPosition = MutableStateFlow<Position?>(null)
    private val _headerImageId = MutableStateFlow<Long?>(null)
    private val _creationDate = MutableStateFlow(Clock.System.now())

    private val _placePositionState = combine(
        _placePosition,
        _userPosition
    ) { placePosition, userPosition ->
        PlacePositionState(userPosition, placePosition)
    }

    internal val uiState: StateFlow<PlaceDetailUiState> = combine(
        snapshotFlow { title },
        snapshotFlow { description },
        _images,
        _placePositionState,
        _creationDate
    ) { title, description, images, placePositionState, creationDate ->
        PlaceDetailUiState.Success(
            PlaceDetailState(
                title = title,
                description = description,
                images = images,
                userPosition = placePositionState.userPosition,
                placePosition = placePositionState.placePosition,
                creationDate = creationDate
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PlaceDetailUiState.Loading
    )

    fun removeImage(image: Image) {
        _deletedImages.add(image)
        _images.update { it.minus(image) }
    }

    fun addImage(uri: Uri) {
        val newImage = Image(id = 0, url = uri.toString())
        _images.update { it.plus(newImage) }
    }

    @JvmName(name = "setPlaceTitle")
    fun setTitle(text: String) {
        title = text
    }

    @JvmName(name = "setPlaceDescription")
    fun setDescription(text: String) {
        description = text
    }

    fun setUserPosition(position: Position) {
        _userPosition.update { position }
    }

    fun setPlacePosition(position: Position) {
        _userPosition.update { position }
    }

    fun saveChanges() {
        //TODO save logic
        if (_images.value.isEmpty() || _placePosition.value == null) return
        //TODO show error when images empty
        val placeToInsert = initialPlace.copy(
            title = title,
            description = description,
            creationDate = _creationDate.value,
            images = _images.value.filter { it.id == 0L },
            position = _placePosition.value ?: Position(
                0.0,
                0.0
            ) //TODO show error when null position
        )
        val imagesToDelete = _deletedImages.toList()
        viewModelScope.launch {
            if (imagesToDelete.isNotEmpty()) imagesToDelete.forEach { image ->
                when(val result = imageRepository.deleteImage(image)) {
                    is Result.Error -> Log.e(TAG, result.error.toString())
                    is Result.Success -> Log.v(TAG, "Image successfully deletedf")
                }
            }
            placesRepository.upsertPlace(placeToInsert)
        }
        endEditing()
    }

    fun cancelChanges() {
        //TODO restore logic
        resetPlaceProperties()
        endEditing()
    }

    fun deletePlace() {
        if (initialMode is InitialMode.View) {
            viewModelScope.launch {
                placesRepository.deletePlaceById(initialMode.placeId)
            }
        }
    }

    fun startEditing() {
        _isEditing.update { true }
    }

    private fun endEditing() {
        _deletedImages.clear()
        _isEditing.update { false }
    }

    fun getSettingsIntent() = permissionManager.createSettingsIntent()
    /*
     TODO make deletedImagesList which contains deleted images.
       when pressed savePlace delete from repo images from deletedImagesList
       and save images from _images list with id == 0
       Maybe make saveImageUseCase
     */

    private fun resetPlaceProperties() {
        with(initialPlace) {
            this@CreatePlaceViewModel.title = title
            this@CreatePlaceViewModel.description = description
            _placePosition.update { position }
            _creationDate.update { creationDate }
            _images.update { images }
        }
    }
}

internal data class PlaceDetailState(
    val title: String = "",
    val description: String = "",
    val userPosition: Position? = null,
    val placePosition: Position? = null,
    val images: List<Image> = emptyList(),
    val creationDate: Instant = Clock.System.now()
)

internal data class PlacePositionState(
    val userPosition: Position?,
    val placePosition: Position?
)

internal sealed interface PlaceDetailUiState {
    data object Loading : PlaceDetailUiState
    data class Error(val error: PlaceError) : PlaceDetailUiState
    data class Success(
        val placeDetailState: PlaceDetailState,
    ) : PlaceDetailUiState
}

internal sealed interface InitialMode {
    data object Creation : InitialMode
    data class View(val placeId: Long) : InitialMode
}

internal sealed interface CreationEvent {
    data object CancelCreation : CreationEvent

}