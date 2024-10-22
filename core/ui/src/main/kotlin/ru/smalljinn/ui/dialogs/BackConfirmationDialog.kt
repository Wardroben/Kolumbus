package ru.smalljinn.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.smalljinn.ui.R

@Composable
fun BackConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onDismiss) {
                Text(stringResource(R.string.stay_here_action))
            }
        },
        confirmButton = {
            TextButton(onConfirm) {
                Text(stringResource(R.string.cancel_editing_action))
            }
        },
        title = {
            Text(stringResource(R.string.cancel_editing_title))
        },
        text = {
            Text(stringResource(R.string.cancel_editing_text))
        },
        modifier = modifier
    )
}