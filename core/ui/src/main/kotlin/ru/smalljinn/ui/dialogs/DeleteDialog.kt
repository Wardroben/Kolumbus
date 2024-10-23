package ru.smalljinn.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.smalljinn.ui.R

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    confirmDeletion: () -> Unit,
    placeTitle: String
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = confirmDeletion) {
                Text(stringResource(R.string.delete_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.delete_place_title)) },
        text = { Text(stringResource(R.string.delete_place_text, placeTitle)) },
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_place_text, placeTitle)
            )
        }
    )
}