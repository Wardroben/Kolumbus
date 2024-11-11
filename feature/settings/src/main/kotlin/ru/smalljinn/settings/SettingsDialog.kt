package ru.smalljinn.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsDialog(onDismiss: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsDialog(
        onDismiss = onDismiss,
        settingsUiState = settings,
        onChangeCardStyle = viewModel::updateCardStyle,
        onImportBackupClicked = { TODO() },
        onCreateBackupClicked = { TODO() }
    )
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    settingsUiState: SettingsUiState,
    onChangeCardStyle: (Boolean) -> Unit,
    onCreateBackupClicked: () -> Unit,
    onImportBackupClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 60.dp),
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_title)) },
        text = {
            HorizontalDivider()
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (settingsUiState) {
                    SettingsUiState.Loading -> Text(stringResource(R.string.settings_loading))
                    is SettingsUiState.Success -> {
                        SettingsPanel(
                            settingsUiState.settings,
                            onChangeCardStyle = onChangeCardStyle,
                            onImportBackupClicked = onImportBackupClicked,
                            onCreateBackupClicked = onCreateBackupClicked
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: SettingsEditable,
    onChangeCardStyle: (Boolean) -> Unit,
    onImportBackupClicked: () -> Unit,
    onCreateBackupClicked: () -> Unit
) {
    SettingSectionTitle(stringResource(R.string.place_card_style_setting))
    Column(Modifier.selectableGroup()) {
        SettingChooserRow(
            stringResource(R.string.full_placecard_style),
            selected = !settings.useCompactStyle
        ) { onChangeCardStyle(false) }
        SettingChooserRow(
            stringResource(R.string.compact_placecard_style),
            selected = settings.useCompactStyle
        ) { onChangeCardStyle(true) }
    }
    SettingSectionTitle("Places backup")
    BackupButtonRow(text = "Export backup as file", isExport = true) { onCreateBackupClicked() }
    BackupButtonRow(text = "Import backup as file", isExport = false) { onImportBackupClicked() }
}

@Composable
private fun BackupButtonsRow(
    modifier: Modifier = Modifier,
    onCreateBackupClicked: () -> Unit,
    onImportBackupClicked: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        OutlinedButton(onClick = onImportBackupClicked) {
            Text("Import")
        }
        Button(onClick = onCreateBackupClicked) {
            Text("Create")
        }
    }
}

@Composable
private fun BackupButtonRow(
    text: String,
    isExport: Boolean,
    onClick: () -> Unit
) {
    Box(Modifier
        .fillMaxWidth()
        .clickable(role = Role.Button) { onClick() },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(12.dp)) {
            if (isExport) Icon(
                painter = painterResource(R.drawable.baseline_upload_24),
                contentDescription = "Export backup"
            )
            else Icon(
                painter = painterResource(R.drawable.baseline_download_24),
                contentDescription = "Import backup"
            )
            Spacer(Modifier.width(12.dp))
            Text(text)
        }
    }
}

@Composable
private fun SettingSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(10.dp))
        Text(text)
    }
}

@Preview
@Composable
private fun SettingsDialogPreview() {
    SettingsDialog(
        onDismiss = {},
        settingsUiState = SettingsUiState.Success(SettingsEditable(useCompactStyle = true)),
        onImportBackupClicked = {},
        onCreateBackupClicked = {},
        onChangeCardStyle = {}
    )
}