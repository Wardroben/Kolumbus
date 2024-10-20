package ru.smalljinn.place

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Position
import ru.smalljinn.permissions.CameraPermissionTextProvider
import ru.smalljinn.permissions.PermissionManager
import ru.smalljinn.ui.CreationDate
import ru.smalljinn.ui.KolumbusMap
import ru.smalljinn.ui.LoadingContent
import ru.smalljinn.ui.ObserveAsEvents
import ru.smalljinn.ui.RemovablePlaceImages
import ru.smalljinn.ui.TakeMediaButton
import ru.smalljinn.ui.TransparentTextField
import ru.smalljinn.ui.dialogs.PermissionExplanationDialog

@Composable
fun PlaceScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onPlaceDeleted: () -> Unit,
    onShowMessage: (Int) -> Unit,
    viewModel: PlaceViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    /*val placeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val isDataProcessing by viewModel.isDataProcessing.collectAsStateWithLifecycle()*/
    val placeUiState by viewModel.uiState1.collectAsStateWithLifecycle()
    val permissionState by viewModel.permissionState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.eventChannel) { event ->
        when (event) {
            is PlaceUiEvent.ShowMessage -> onShowMessage(event.messageId)
            PlaceUiEvent.NavigateBack -> onBackClick()
        }
    }

    var gpsRequestDenied by rememberSaveable { mutableStateOf(false) }
    val gpsSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gpsRequestDenied = false
        } else {
            gpsRequestDenied = true
            Log.d("GPS", "Location denied")
        }
    }

    PlaceScreen(
        showBackButton = showBackButton,
        isEditing = placeUiState.placeMode != PlaceMode.VIEW,
        isDataProcessing = placeUiState.isDataProcessing,
        onBackClick = onBackClick,
        placeUiState = placeUiState,
        onRemoveImage = viewModel::removeImage,
        onDeleteClick = {
            //TODO hoist delete logic to 2pane
            onPlaceDeleted()
            viewModel.deletePlace()
        },
        onEditClick = viewModel::startEditing,
        onSaveChanges = viewModel::saveChanges,
        onCancelEditing = viewModel::cancelChanges,
        onDescriptionChanged = viewModel::setDescription,
        onTitleChanged = viewModel::setTitle,
        onOpenSettings = { context.startActivity(viewModel.getSettingsIntent()) },
        onPhotoTaken = viewModel::addImage,
        permissionState = permissionState,
        onImagesTaken = viewModel::addImages,
        getUriForPhoto = { viewModel.getUriForPhoto() },
        onPlacePositionUpdated = viewModel::setPlacePosition,
        onUserPositionUpdated = viewModel::setUserPosition,
        onGpsUnavailableResolvable = { intentRequest ->
            gpsSettingsLauncher.launch(intentRequest)
        },
        onShareClick = {TODO("Share action")},
        isGpsRequestDenied = gpsRequestDenied
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlaceScreen(
    placeUiState: PlaceDetailState,
    permissionState: PermissionManager.State,
    isEditing: Boolean,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onRemoveImage: (Image) -> Unit,
    onSaveChanges: () -> Unit,
    onCancelEditing: () -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onPhotoTaken: (Uri) -> Unit,
    onOpenSettings: () -> Unit,
    onImagesTaken: (List<Uri>) -> Unit,
    isDataProcessing: Boolean,
    getUriForPhoto: () -> Uri,
    onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    onUserPositionUpdated: (Position) -> Unit,
    onPlacePositionUpdated: (Position) -> Unit,
    isGpsRequestDenied: Boolean,
    modifier: Modifier = Modifier
) {
    val placeLazyListState = rememberLazyListState()
    var columnScrollingEnabled by remember { mutableStateOf(true) }
    LazyColumn(
        modifier = modifier.imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = columnScrollingEnabled,
        state = placeLazyListState
    ) {
        /*item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }*/
        when {
            placeUiState.error -> Unit //TODO("error content with retry button")
            placeUiState.loading -> item { LoadingContent(stringResource(R.string.loading_place)) }
            else -> {
                stickyHeader {
                    PlaceToolbar(
                        isEditing = isEditing,
                        showBackButton = showBackButton,
                        onBackClick = onBackClick,
                        onDeleteClick = onDeleteClick,
                        onEditClick = onEditClick,
                        onShareClick = onShareClick,
                        onSaveChanges = onSaveChanges,
                        onCancelEditing = onCancelEditing,
                        isDataProcessing = isDataProcessing,
                        canSave = with(placeUiState) {
                            title.isNotBlank() && images.isNotEmpty()
                        }
                    )
                }
                placeDetailBody(
                    placeDetailState = placeUiState,
                    isEditing = isEditing,
                    onRemoveImage = onRemoveImage,
                    onDescriptionChanged = onDescriptionChanged,
                    onTitleChanged = onTitleChanged,
                    onPhotoTaken = onPhotoTaken,
                    onOpenSettings = onOpenSettings,
                    permissionState = permissionState,
                    onImagesTaken = onImagesTaken,
                    getUriForPhoto = getUriForPhoto,
                    onGpsUnavailableResolvable = onGpsUnavailableResolvable,
                    onUserPositionUpdated = onUserPositionUpdated,
                    onPlacePositionUpdated = onPlacePositionUpdated,
                    onMapCameraMoving = { isMapCameraMoving ->
                        columnScrollingEnabled = !isMapCameraMoving
                    },
                    isGpsRequestDenied = isGpsRequestDenied
                )
                item {
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.systemBars
                        )
                    )
                }
            }


        }
    }
}

@Composable
private fun PlaceImagesRow(
    modifier: Modifier = Modifier,
    images: List<Image>,
    isEditing: Boolean,
    onPhotoTaken: (Uri) -> Unit,
    onOpenSettings: () -> Unit,
    hasCameraAccess: Boolean,
    onImagesTaken: (List<Uri>) -> Unit,
    onRemoveImage: (Image) -> Unit,
    getUriForPhoto: () -> Uri
) {
    val lazyRowState = rememberLazyListState()
    var lastImageListSize by remember { mutableIntStateOf(images.size) }
    LaunchedEffect(images) {
        if (images.lastIndex != -1 && isEditing && images.size > lastImageListSize) {
            lazyRowState.animateScrollToItem(images.lastIndex)
        }
        lastImageListSize = images.size
    }
    Column(modifier = modifier) {
        AnimatedVisibility(
            images.isEmpty() && isEditing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            NoImagesPlaceholder()
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            state = lazyRowState
        ) {
            items(items = images, key = { it.url }, contentType = { "placeImage" }) { image ->
                RemovablePlaceImages(
                    modifier = Modifier
                        .sizeIn(
                            minWidth = 135.dp,
                            minHeight = 180.dp,
                            maxWidth = 240.dp,
                            maxHeight = 180.dp
                        )
                        .animateItem(),
                    onRemoveClick = { onRemoveImage(image) },
                    readyToDelete = isEditing,
                    url = image.url
                )
            }
        }

        AnimatedVisibility(isEditing) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TakeCameraPhotoButton(
                    onPhotoTaken = onPhotoTaken,
                    onOpenSettings = onOpenSettings,
                    hasCameraAccess = hasCameraAccess,
                    getUriForPhoto = getUriForPhoto,
                    modifier = Modifier.weight(1f)
                )
                //TODO change button
                TakeMediaImagesButton(
                    onImagesTaken = onImagesTaken,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NoImagesPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.no_images_label))
    }
}

const val CAMERA_PERMISSION = Manifest.permission.CAMERA

@Composable
private fun TakeCameraPhotoButton(
    modifier: Modifier = Modifier,
    onPhotoTaken: (Uri) -> Unit,
    onOpenSettings: () -> Unit,
    hasCameraAccess: Boolean,
    getUriForPhoto: () -> Uri
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showExplanationDialogForCameraPermission by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { photoTaken ->
        if (photoTaken) {
            photoUri?.let(onPhotoTaken)
        }
    }

    val getUriAndLaunchCamera = {
        photoUri = getUriForPhoto()
        cameraLauncher.launch(photoUri)
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUriAndLaunchCamera()
        } else {
            showExplanationDialogForCameraPermission = true
        }
    }

    val requestPermission = { requestCameraPermission.launch(CAMERA_PERMISSION) }

    if (showExplanationDialogForCameraPermission) {
        PermissionExplanationDialog(
            onOpenSettings = {
                onOpenSettings()
                showExplanationDialogForCameraPermission = false
            },
            onDismiss = { showExplanationDialogForCameraPermission = false },
            textProvider = CameraPermissionTextProvider(),
            onConfirm = {
                showExplanationDialogForCameraPermission = false
                requestPermission()
            },
            isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                CAMERA_PERMISSION
            )
        )
    }

    TakeMediaButton(
        modifier = modifier,
        onClick = {
            when {
                hasCameraAccess -> {
                    getUriAndLaunchCamera()
                }

                else -> requestPermission()
            }
        },
        iconResId = R.drawable.baseline_camera_alt_24,
        contentDescriptionResId = R.string.take_a_photo_cd
    )
}

@Composable
private fun TakeMediaImagesButton(
    modifier: Modifier = Modifier,
    onImagesTaken: (List<Uri>) -> Unit
) {
    val mediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImagesTaken(uris)
        }
    }
    TakeMediaButton(
        modifier = modifier,
        onClick = {
            mediaLauncher.launch(
                PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        iconResId = R.drawable.baseline_photo_library_24,
        contentDescriptionResId = R.string.take_image_from_gallery_cd
    )
}

@OptIn(ExperimentalComposeUiApi::class)
private fun LazyListScope.placeDetailBody(
    placeDetailState: PlaceDetailState,
    isEditing: Boolean,
    onDescriptionChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onRemoveImage: (Image) -> Unit,
    onOpenSettings: () -> Unit,
    onPhotoTaken: (Uri) -> Unit,
    onImagesTaken: (List<Uri>) -> Unit,
    permissionState: PermissionManager.State,
    onUserPositionUpdated: (Position) -> Unit,
    onPlacePositionUpdated: (Position) -> Unit,
    onGpsUnavailableResolvable: (IntentSenderRequest) -> Unit,
    getUriForPhoto: () -> Uri,
    isGpsRequestDenied: Boolean,
    onMapCameraMoving: (Boolean) -> Unit
) {
    item {
        //images lazy row
        PlaceImagesRow(
            images = placeDetailState.images,
            isEditing = isEditing,
            onRemoveImage = onRemoveImage,
            onOpenSettings = onOpenSettings,
            onPhotoTaken = onPhotoTaken,
            hasCameraAccess = permissionState.hasCameraAccess,
            onImagesTaken = onImagesTaken,
            getUriForPhoto = getUriForPhoto
        )
    }
    item {
        KolumbusMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .motionEventSpy {
                    if (isEditing) {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> onMapCameraMoving(true)
                            MotionEvent.ACTION_UP -> onMapCameraMoving(false)
                        }
                    }
                },
            //TODO if placePosition null and userPosition null
            placePosition = placeDetailState.placePosition ?: Position(53.7222971, 91.4157491),
            userPosition = placeDetailState.userPosition ?: Position.initialPosition(),
            onUserPositionUpdated = onUserPositionUpdated,
            onPlacePositionUpdated = onPlacePositionUpdated,
            onGpsUnavailableResolvable = onGpsUnavailableResolvable,
            hasLocationPermission = permissionState.hasAtLeastOneLocationAccess,
            usePreciseLocation = permissionState.hasFineLocationAccess,
            isPlaceEditing = isEditing,
            isPlaceCreating = placeDetailState.placeMode == PlaceMode.CREATING,
            isGpsRequestDenied = isGpsRequestDenied
        )
    }
    item {
        //title
        TransparentTextField(
            text = placeDetailState.title,
            readOnly = !isEditing,
            onTextChanged = onTitleChanged,
            style = MaterialTheme.typography.titleLarge,
            hintText = stringResource(R.string.title_cd),
            shouldShowHint = isEditing && placeDetailState.title.isBlank(),
            imeAction = ImeAction.Next,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        //TODO favorite button
    }
    item {
        CreationDate(
            creationDate = placeDetailState.creationDate,
            longFormat = true,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
    item {
        //Description
        AnimatedVisibility(
            isEditing || placeDetailState.description.isNotBlank(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TransparentTextField(
                text = placeDetailState.description,
                readOnly = !isEditing,
                onTextChanged = onDescriptionChanged,
                style = MaterialTheme.typography.bodyLarge,
                hintText = stringResource(R.string.description_cd),
                shouldShowHint = isEditing && placeDetailState.description.isBlank(),
                modifier = Modifier
                    .heightIn(100.dp, max = if (!isEditing) Int.MAX_VALUE.dp else 300.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun PlaceToolbar(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelEditing: () -> Unit,
    onSaveChanges: () -> Unit,
    isDataProcessing: Boolean,
    canSave: Boolean
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        AnimatedContent(targetState = isEditing, label = "AnimatedPlaceToolbar") { editing ->
            if (editing) PlaceEditingButtons(
                cancelEditing = onCancelEditing,
                saveChanges = onSaveChanges,
                canSave = canSave
            )
            else PlaceControlButtons(
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                onEditClick = onEditClick,
                onShareClick = onShareClick,
                showLoading = isDataProcessing
            )
        }
    }
}

@Composable
private fun PlaceEditingButtons(
    cancelEditing: () -> Unit,
    saveChanges: () -> Unit,
    canSave: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = cancelEditing) {
            Text(stringResource(R.string.cancel_action))
        }
        //Text(stringResource(R.string.editing_mode_label), style = MaterialTheme.typography.bodySmall)
        Button(onClick = saveChanges, enabled = canSave) {
            Text(stringResource(R.string.save_action))
        }
    }
}

@Composable
private fun PlaceControlButtons(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    showLoading: Boolean
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        if (showBackButton) {
            AnimatedContent(showLoading, label = "LoadingBackButton") { isLoading ->
                if (isLoading) CircularProgressIndicator()
                else FilledTonalIconButton(onClick = { onBackClick() }) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_cd)
                    )
                }
            }
        }

        FilledTonalIconButton(onClick = onDeleteClick) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_place_cd)
            )
        }

        FilledTonalIconButton(onClick = onEditClick) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_place_cd)
            )
        }

        FilledTonalIconButton(onClick = { onShareClick() }) {
            Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_place_cd))
        }
    }
}