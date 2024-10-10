package ru.smalljinn.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun RemovablePlaceImages(
    modifier: Modifier = Modifier,
    readyToDelete: Boolean,
    onRemoveClick: () -> Unit,
    url: String,
) {
    Box(modifier = modifier) {
        KolumbusAsyncImage(
            imageUrl = url,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
        )
        AnimatedVisibility(
            visible = readyToDelete,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            FilledIconButton(
                onClick = onRemoveClick,
                modifier = Modifier.padding(10.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                )
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = null
                )
            }
        }

    }
}