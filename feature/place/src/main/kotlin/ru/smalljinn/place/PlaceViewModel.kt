package ru.smalljinn.place

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.smalljinn.core.photo_store.PhotoManager
import ru.smalljinn.kolumbus.data.repository.ImageRepository
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.Position
import ru.smalljinn.model.data.response.PlaceError
import ru.smalljinn.permissions.PermissionManager
import ru.smalljinn.place.navigation.PlaceRoute
import ru.smalljinn.place.usecase.InvalidPlaceException
import ru.smalljinn.place.usecase.SavePlaceUseCase
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository,
    private val imagesRepository: ImageRepository,
    private val permissionManager: PermissionManager,
    private val photoManager: PhotoManager,
    private val savePlaceUseCase: SavePlaceUseCase,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<PlaceRoute>()
    private val initialMode = when {
        route.isCreating -> InitialMode.Creation
        route.id == Place.CREATION_ID -> InitialMode.Editing(route.id)
        else -> InitialMode.View(route.id)
    }

    private lateinit var initialPlace: Place

    val permissionState = permissionManager.state

    private val _uiState = MutableStateFlow(PlaceDetailState())
    internal val uiState1 = _uiState.asStateFlow()

    init {
        if (initialMode is InitialMode.Creation) {
            initialPlace = Place.getInitPlace()
            _uiState.update { it.copy(placeMode = PlaceMode.CREATING, loading = false) }
        } else viewModelScope.launch {
            val place = placesRepository.getPlace(route.id)
            initialPlace = place

            _uiState.update {
                PlaceDetailState(
                    title = place.title,
                    description = place.description,
                    placePosition = place.position,
                    images = place.images,
                    creationDate = place.creationDate,
                    headerImageId = place.headerImageId,
                    placeMode = when (initialMode) {
                        is InitialMode.Editing -> PlaceMode.EDITING
                        is InitialMode.View -> PlaceMode.VIEW
                        InitialMode.Creation -> PlaceMode.CREATING
                    },
                    loading = false
                )
            }
        }
    }

    private val _eventChannel = Channel<PlaceUiEvent>()
    internal val eventChannel = _eventChannel.receiveAsFlow()

    private val _deletedImages = mutableListOf<Image>()

    fun getPlaceInfoToDelete(): Pair<Long, String> =
        with(uiState1.value) { Pair(initialPlace.id, title) }


    fun removeImage(image: Image) {
        _uiState.update { it.copy(images = uiState1.value.images.minus(image)) }
        _deletedImages.add(image)
    }

    fun addImage(uri: Uri) {
        val duplicate = uiState1.value.images.find { it.url == uri.toString() }
        if (duplicate != null) return
        val newImage = Image(id = 0, url = uri.toString())
        _uiState.update { it.copy(images = uiState1.value.images.plus(newImage)) }
    }

    fun addImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val newImages = uris.map { uri -> Image(id = 0, url = uri.toString()) }

        _uiState.update { it.copy(images = uiState1.value.images.plus(newImages)) }
    }

    @JvmName(name = "setPlaceTitle")
    fun setTitle(text: String) {
        _uiState.update { it.copy(title = text) }
    }

    @JvmName(name = "setPlaceDescription")
    fun setDescription(text: String) {
        _uiState.update { it.copy(description = text) }
    }

    fun setUserPosition(position: Position) {
        _uiState.update { it.copy(userPosition = position) }
    }

    fun setPlacePosition(position: Position) {
        _uiState.update { it.copy(placePosition = position) }
    }

    fun saveChanges() {
        var isCanceled = false

        setDataProcessing(true)
        viewModelScope.launch {
            try {
                val placeToInsert =
                    uiState1.value.getPlaceToInsert(initialPlace.id, initialPlace.favorite)
                val insertPlaceResultId = savePlaceUseCase(
                    place = placeToInsert,
                    imagesToDelete = _deletedImages.toSet()
                )
                if (insertPlaceResultId != -1L && initialPlace.id == Place.CREATION_ID)
                    initialPlace = initialPlace.copy(id = insertPlaceResultId)
                val newImages = imagesRepository.getPlaceImages(initialPlace.id)
                initialPlace =
                    initialPlace.copy(
                        images = newImages,
                        position = placeToInsert.position,
                        title = placeToInsert.title,
                        description = placeToInsert.description,
                        headerImageId = placeToInsert.headerImageId,
                    )
                _uiState.update { it.copy(images = newImages) }
            } catch (e: InvalidPlaceException) {
                isCanceled = true
                _eventChannel.send(PlaceUiEvent.ShowMessage(e.messageId))
            }
        }.invokeOnCompletion {
            clearTempImages()
            setDataProcessing(false)
        }
        if (!isCanceled) endEditing()
    }

    fun cancelChanges() {
        if (initialPlace.id == Place.CREATION_ID) {
            _eventChannel.trySend(PlaceUiEvent.NavigateBack)
        } else {
            resetPlaceProperties()
            endEditing()
        }
        clearTempImages()
    }

    //TODO move to intent manager or something
    fun createShareIntent(): Intent {
        with(uiState1.value) {
            val imageUris: ArrayList<Uri> = ArrayList(images.map { it.url.toUri() })
            val headerImageUri = images.find { it.id == headerImageId }?.url?.toUri() ?: imageUris.first()
            val intentText = "$title\n\n" +
                    "$description\n\n" +
                    "https://www.google.com/maps/place/${placePosition?.latitude},${placePosition?.longitude}"
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, "Sharing place")
                putExtra(Intent.EXTRA_TEXT, intentText)
                putExtra(Intent.EXTRA_STREAM, headerImageUri)
                setDataAndType(headerImageUri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            return shareIntent
        }
    }

    fun getUriForPhoto(): Uri = photoManager.getUriForTakePhoto()

    fun startEditing() = _uiState.update { it.copy(placeMode = PlaceMode.EDITING) }

    fun updatePermissions() = viewModelScope.launch { permissionManager.checkPermissions() }

    fun getSettingsIntent() = permissionManager.createSettingsIntent()

    fun setHeaderImage(id: Long) {
        if (id == 0L) return
        _uiState.update { it.copy(headerImageId = id) }
        viewModelScope.launch {
            savePlaceUseCase(
                place = uiState1.value.getPlaceToInsert(
                    initialPlace.id,
                    initialPlace.favorite
                ),
                imagesToDelete = emptySet()
            )
        }
    }

    private fun clearTempImages() = viewModelScope.launch { photoManager.clearTemporaryImages() }
    private fun setDataProcessing(processing: Boolean) =
        _uiState.update { it.copy(isDataProcessing = processing) }

    private fun endEditing() {
        _deletedImages.clear()
        _uiState.update { it.copy(placeMode = PlaceMode.VIEW) }
    }

    private fun resetPlaceProperties() {
        with(initialPlace) {
            _uiState.update {
                it.copy(
                    placePosition = position,
                    creationDate = creationDate,
                    images = images,
                    title = title,
                    description = description
                )
            }
        }
    }
}

private const val TAG = "PlaceVM"


internal data class PlaceDetailState(
    val title: String = "",
    val description: String = "",
    val userPosition: Position? = null,
    val headerImageId: Long? = null,
    val placePosition: Position? = null,
    val images: List<Image> = emptyList(),
    val creationDate: Instant = Clock.System.now(),
    val isDataProcessing: Boolean = false,
    val placeMode: PlaceMode = PlaceMode.VIEW,
    val error: Boolean = false,
    val loading: Boolean = true
) {
    val editable: Boolean
        get() = placeMode != PlaceMode.VIEW
    fun getPlaceToInsert(id: Long, favorite: Boolean) = Place(
        id = id,
        title = title,
        description = description,
        images = images,
        creationDate = creationDate,
        position = placePosition ?: Position.initialPosition(),
        headerImageId = headerImageId,
        favorite = favorite
    )
}

internal enum class PlaceMode {
    CREATING, EDITING, VIEW
}

internal sealed interface PlaceUiEvent {
    data class ShowMessage(@StringRes val messageId: Int) : PlaceUiEvent
    data object NavigateBack : PlaceUiEvent
}

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
    data class Editing(val placeId: Long) : InitialMode
    data class View(val placeId: Long) : InitialMode
}
