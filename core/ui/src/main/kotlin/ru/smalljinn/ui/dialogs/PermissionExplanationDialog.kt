package ru.smalljinn.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.smalljinn.permissions.PermissionTextProvider
import ru.smalljinn.ui.R

@Composable
fun PermissionExplanationDialog(
    modifier: Modifier = Modifier,
    textProvider: PermissionTextProvider,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isPermanentlyDeclined: Boolean,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = if (isPermanentlyDeclined) onOpenSettings else onConfirm) {
                Text(if (isPermanentlyDeclined) stringResource(R.string.open_settings) else "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.permission_required)) },
        text = { Text(stringResource(textProvider.getDescriptionResId(isPermanentlyDeclined))) }
    )
}