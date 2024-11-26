package ru.smalljinn.ui.utils.pickers

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Stable
@Immutable
interface FilePiker {
    fun pickFile()
}

private data class FilePickerImpl(
    val context: Context,
    val mimeTypes: List<String>,
    val openDocument: ManagedActivityResultLauncher<Array<String>, Uri?>,
    val onFailure: (Throwable) -> Unit
) : FilePiker {
    override fun pickFile() {
        runCatching {
            openDocument.launch(mimeTypes.toTypedArray())
        }.onFailure(onFailure)
    }
}

@Composable
fun rememberFilePicker(
    mimeTypes: List<String> = listOf("application/octet-stream"),
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit
): FilePiker {
    val context = LocalContext.current

    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.takeIf { it != Uri.EMPTY }?.let(onSuccess) ?: onFailure()
    }

    return remember(mimeTypes, openFileLauncher) {
        derivedStateOf {
            FilePickerImpl(context, mimeTypes, openFileLauncher, onFailure = { onFailure() })
        }.value
    }
}