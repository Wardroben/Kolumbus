package ru.smalljinn.kolumbus

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.smalljinn.domain.repository.ImportExportRepository
import javax.inject.Inject

@HiltViewModel
class RootComponent @Inject constructor(
    private val importExportRepository: ImportExportRepository,
): ViewModel() {
    /*private val _uiEvent = Channel<ImportUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()*/

    fun importPlaces(uri: Uri) {
        viewModelScope.launch {
            val importResult = importExportRepository.importPlaces(uri)
           /* when(val importResult = importExportRepository.importPlaces(uri)) {
                is Result.Error -> backupMessageProvider.showErrorMessage(importResult.error)//processErrorMessage(importResult.error)
                is Result.Success -> backupMessageProvider.showSuccessMessage()//showSuccessMessage()
            }*/
        }
    }

    /*private fun processErrorMessage(importError: ImportError) {
        val message = when(importError) {
            ImportError.FILE_NOT_FOUND -> resourceManager.getString(R.string.file_not_found_import_error)
            ImportError.NOT_BACKUP_FILE -> resourceManager.getString(R.string.not_backup_file_import_error)
            ImportError.UNKNOWN -> resourceManager.getString(R.string.unknown_import_error)
        }
        showMessage(ImportUiEvent.Error(message))
    }

    private fun showSuccessMessage() {
        showMessage(ImportUiEvent.Imported(resourceManager.getString(R.string.import_success)))
    }

    private fun showMessage(event: ImportUiEvent) {
        _uiEvent.trySend(event)
    }*/
}

/*
sealed class ImportUiEvent {
    data class Imported(val message: String): ImportUiEvent()
    data class Error(val message: String): ImportUiEvent()
}*/
