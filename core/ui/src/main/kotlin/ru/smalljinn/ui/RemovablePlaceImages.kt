package ru.smalljinn.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RemovablePlaceImages(
    modifier: Modifier = Modifier,
    readyToDelete: Boolean,
    onRemoveClick: () -> Unit,
    image: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        image()
        AnimatedVisibility(
            visible = readyToDelete
        ) {
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = null
                )
            }
        }

    }
}