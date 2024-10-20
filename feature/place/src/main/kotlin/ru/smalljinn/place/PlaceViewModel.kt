package ru.smalljinn.place

import android.net.Uri
import androidx.annotation.StringRes
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
import ru.smalljinn.place.usecase.DeletePlaceUseCase
import ru.smalljinn.place.usecase.InvalidPlaceException
import ru.smalljinn.place.usecase.SavePlaceUseCase
import javax.inject.Inject

private const val TAG = "PlaceVM"

@HiltViewModel
class PlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository,
    private val imagesRepository: ImageRepository,
    private val permissionManager: PermissionManager,
    private val photoManager: PhotoManager,
    private val savePlaceUseCase: SavePlaceUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase
) : ViewModel() {
    private val route = savedStateHandle.toRoute<PlaceRoute>()
    private val initialMode = when {
        route.isCreating -> InitialMode.Creation
        route.id == Place.CREATION_ID -> InitialMode.Editing(route.id)
        else -> InitialMode.View(route.id)
    }
    /*private val initialMode = when (val placeId = savedStateHandle.toRoute<PlaceRoute>().id) {
        Place.CREATION_ID -> InitialMode.Creation
        else -> InitialMode.View(placeId)
    }*/

    private lateinit var initialPlace: Place

    val permissionState = permissionManager.state

    private val _uiState = MutableStateFlow<PlaceDetailState>(PlaceDetailState())
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
                    placeMode = when(initialMode) {
                        is InitialMode.Editing -> PlaceMode.EDITING
                        is InitialMode.View -> PlaceMode.VIEW
                        InitialMode.Creation -> PlaceMode.CREATING
                    },
                    loading = false
                )
            }

            /*title = place.title
            description = place.description

            _images.update { it.plus(place.images) }
            _placePosition.update { place.position }
            _headerImageId.update { place.headerImageId }
            _creationDate.update { place.creationDate }*/
        }
    }

    private val _eventChannel = Channel<PlaceUiEvent>()
    internal val eventChannel = _eventChannel.receiveAsFlow()

    private val _deletedImages = mutableListOf<Image>()

    /*private val _isDataProcessing = MutableStateFlow(false)
    val isDataProcessing = _isDataProcessing.asStateFlow()

    private val _isEditing = MutableStateFlow(initialMode is InitialMode.Creation)
    val isEditing = _isEditing.asStateFlow()

    private var title by mutableStateOf("")
    private var description by mutableStateOf("")

    private val _images = MutableStateFlow<List<Image>>(emptyList())


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
    )*/

    fun removeImage(image: Image) {
        /*if (image.id != 0L) _deletedImages.add(image)
        _images.update { it.minus(image) }*/

        _uiState.update { it.copy(images = uiState1.value.images.minus(image)) }
    }

    fun addImage(uri: Uri) {
        /*val duplicate = _images.value.find { it.url == uri.toString() }
        if (duplicate != null) return
        val newImage = Image(id = 0, url = uri.toString())
        _images.update { it.plus(newImage) }*/

        val duplicate = uiState1.value.images.find { it.url == uri.toString() }
        if (duplicate != null) return
        val newImage = Image(id = 0, url = uri.toString())
        _uiState.update { it.copy(images = uiState1.value.images.plus(newImage)) }
    }

    fun addImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val newImages = uris.map { uri -> Image(id = 0, url = uri.toString()) }
        //_images.update { it.plus(newImages) }

        _uiState.update { it.copy(images = uiState1.value.images.plus(newImages)) }
    }

    @JvmName(name = "setPlaceTitle")
    fun setTitle(text: String) {
        //title = text

        _uiState.update { it.copy(title = text) }
    }

    @JvmName(name = "setPlaceDescription")
    fun setDescription(text: String) {
        //description = text

        _uiState.update { it.copy(description = text) }
    }

    fun setUserPosition(position: Position) {
        //_userPosition.update { position }

        _uiState.update { it.copy(userPosition = position) }
    }

    fun setPlacePosition(position: Position) {
        //_placePosition.update { position }

        _uiState.update { it.copy(placePosition = position) }
    }

    fun saveChanges() {
        var isCanceled = false
        /*_isDataProcessing.update { true }

        viewModelScope.launch {
            try {
                val insertPlaceResultId = savePlaceUseCase(
                    place = getPlaceToInsert(),
                    imagesToDelete = _deletedImages.toSet()
                )
                //insertPlaceResultId != -1L - if place updated
                if (insertPlaceResultId != -1L && initialPlace.id == Place.CREATION_ID)
                    initialPlace = initialPlace.copy(id = insertPlaceResultId)
                val newImages = imagesRepository.getPlaceImages(initialPlace.id)
                initialPlace = initialPlace.copy(images = newImages)
                _images.update { newImages }
            } catch (e: InvalidPlaceException) {
                isCanceled = true
                _eventChannel.send(PlaceUiEvent.ShowMessage(e.messageId))
            }
        }.invokeOnCompletion {
            _isDataProcessing.update { false }
        }
        if (!isCanceled) endEditing()*/

        //New logic
        setDataProcessing(true)
        viewModelScope.launch {
            try {
                val insertPlaceResultId = savePlaceUseCase(
                    place = uiState1.value.getPlaceToInsert(initialPlace.id, initialPlace.favorite),
                    imagesToDelete = _deletedImages.toSet()
                )
                if (insertPlaceResultId != -1L && initialPlace.id == Place.CREATION_ID)
                    initialPlace = initialPlace.copy(id = insertPlaceResultId)
                val newImages = imagesRepository.getPlaceImages(initialPlace.id)
                initialPlace = initialPlace.copy(images = newImages)
                _uiState.update { it.copy(images = newImages) }
            } catch (e: InvalidPlaceException) {
                isCanceled = true
                _eventChannel.send(PlaceUiEvent.ShowMessage(e.messageId))
            }
        }.invokeOnCompletion {
            setDataProcessing(false)
        }
        if (!isCanceled) endEditing()
    }

    private fun setDataProcessing(processing: Boolean) = _uiState.update { it.copy(isDataProcessing = processing) }

    /*private fun getPlaceToInsert(): Place = initialPlace.copy(
        title = title,
        description = description,
        creationDate = _creationDate.value,
        images = _images.value,
        position = _placePosition.value
            ?: Position.initialPosition() //TODO show error when null position
    )*/

    fun cancelChanges() {
        if (initialPlace.id == Place.CREATION_ID) {
            _eventChannel.trySend(PlaceUiEvent.NavigateBack)
        } else {
            resetPlaceProperties()
            endEditing()
        }
    }

    fun deletePlace() {
        if (initialPlace.id > 0L) viewModelScope.launch { deletePlaceUseCase(initialPlace.id) }
    }

    fun getUriForPhoto(): Uri = photoManager.getUriForTakePhoto()

    fun startEditing() {
        //_isEditing.update { true }

        _uiState.update { it.copy(placeMode = PlaceMode.EDITING) }
    }

    private fun endEditing() {
        _deletedImages.clear()
        //_isEditing.update { false }

        _uiState.update { it.copy(placeMode = PlaceMode.VIEW) }
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
            /*_placePosition.update { position }
            _creationDate.update { creationDate }
            _images.update { images }*/
            _uiState.update { it.copy(
                placePosition = position,
                creationDate = creationDate,
                images = images
            ) }
        }
    }
}

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
