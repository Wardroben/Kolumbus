package ru.smalljinn.place

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.ui.CreationDate
import ru.smalljinn.ui.KolumbusAsyncImage
import ru.smalljinn.ui.LoadingContent
import ru.smalljinn.ui.RemovablePlaceImages
import ru.smalljinn.ui.TransparentTextField

@Composable
fun PlaceScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onPlaceDeleted: () -> Unit,
    viewModel: PlaceViewModel = hiltViewModel()
) {
    val placeUiState by viewModel.placeState.collectAsStateWithLifecycle()
    PlaceScreen(
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        placeUiState = placeUiState,
        onRemoveImage = { TODO() },
        onDeleteClick = {
            onPlaceDeleted()
            viewModel.obtainEvent(PlaceEvent.DeletePlace)
        },
        onEditClick = { viewModel.obtainEvent(PlaceEvent.EditPlace) },
        onSaveChanges = {},
        onCancelEditing = { viewModel.obtainEvent(PlaceEvent.CancelEditing) }
    )
}

@Composable
internal fun PlaceScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    placeUiState: PlaceUiState,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveImage: (Image) -> Unit,
    onSaveChanges: () -> Unit,
    onCancelEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /*item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }*/
        when (placeUiState) {
            is PlaceUiState.Success -> {
                item {
                    PlaceToolbar(
                        isEditing = placeUiState.isEditing,
                        showBackButton = showBackButton,
                        onBackClick = onBackClick,
                        onDeleteClick = onDeleteClick,
                        onEditClick = onEditClick,
                        onShareClick = { TODO("Share action") },
                        onSaveChanges = onSaveChanges,
                        onCancelEditing = onCancelEditing
                    )
                }
                placeBody(
                    place = placeUiState.place,
                    isEditing = placeUiState.isEditing,
                    onRemoveImage = onRemoveImage,
                    onDescriptionChanged = {},
                    onTitleChanged = {}
                )
            }

            PlaceUiState.Error -> Unit //TODO("error content with retry button")
            PlaceUiState.Loading -> item { LoadingContent("Loading place...") }
        }
    }
}

@Composable
private fun PlaceImagesRow(
    modifier: Modifier = Modifier,
    images: List<Image>,
    isEditing: Boolean,
    onRemoveImage: (Image) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = images, key = { it.id }, contentType = { "placeImage" }) { image ->
            RemovablePlaceImages(
                modifier = Modifier.sizeIn(
                    minWidth = 135.dp,
                    minHeight = 180.dp,
                    maxWidth = 240.dp,
                    maxHeight = 180.dp
                ),
                onRemoveClick = { onRemoveImage(image) },
                readyToDelete = isEditing
            ) {
                KolumbusAsyncImage(
                    imageUrl = image.url,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

private fun LazyListScope.placeBody(
    place: Place,
    isEditing: Boolean,
    onDescriptionChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onRemoveImage: (Image) -> Unit
) {
    item {
        //images lazy row
        PlaceImagesRow(
            images = place.images,
            isEditing = isEditing,
            onRemoveImage = onRemoveImage
        )
    }
    item {
        //map

    }
    item {
        //title, description, date, other info
        TransparentTextField(
            text = place.title,
            readOnly = !isEditing,
            onTextChanged = onTitleChanged,
            style = MaterialTheme.typography.titleLarge,
            hintText = stringResource(R.string.title_optional_cd),
            shouldShowHint = isEditing && place.title.isBlank()
        )
        //TODO favorite button
    }
    item { CreationDate(creationDate = place.creationDate, longFormat = true) }
    item {
        TransparentTextField(
            text = place.description,
            readOnly = !isEditing,
            onTextChanged = onDescriptionChanged,
            style = MaterialTheme.typography.bodyLarge,
            hintText = stringResource(R.string.description_cd),
            shouldShowHint = isEditing && place.title.isBlank()
        )
    }
}

@Composable
private fun PlaceToolbar(
    isEditing: Boolean,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelEditing: () -> Unit,
    onSaveChanges: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AnimatedContent(targetState = isEditing, label = "AnimatedPlaceToolbar") { editing ->
            if (editing) PlaceEditingButtons(
                cancelEditing = onCancelEditing,
                saveChanges = onSaveChanges
            )
            else PlaceControlButtons(
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                onEditClick = onEditClick,
                onShareClick = onShareClick
            )
        }
    }
}

@Composable
private fun PlaceEditingButtons(
    cancelEditing: () -> Unit,
    saveChanges: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = cancelEditing) {
            Text("Cancel")
        }
        Text("Editing mode", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = saveChanges) {
            Text("Save")
        }
    }
}

@Composable
private fun PlaceControlButtons(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        if (showBackButton) {
            FilledTonalIconButton(onClick = { onBackClick() }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_cd)
                )
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