package ru.smalljinn.kolumbus.data.backup

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.domain.R
import ru.smalljinn.domain.backup.BackupMessageProvider
import ru.smalljinn.domain.resource.ResourceManager
import ru.smalljinn.model.data.response.ImportError
import javax.inject.Inject

class AndroidBackupMessageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resourceManager: ResourceManager
): BackupMessageProvider {
    override fun showSuccessMessage(importedPlaces: Int, skippedDuplicatesPlaces: Int) {
        showToastMessage(resourceManager.getString(R.string.import_success, importedPlaces, skippedDuplicatesPlaces))
    }

    override fun showErrorMessage(importError: ImportError) {
        val errorMessage = when(importError) {
            ImportError.FILE_NOT_FOUND -> resourceManager.getString(R.string.file_not_found_import_error)
            ImportError.NOT_BACKUP_FILE -> resourceManager.getString(R.string.not_backup_file_import_error)
            ImportError.UNKNOWN -> resourceManager.getString(R.string.unknown_import_error)
        }
        showToastMessage(errorMessage)
    }

    private fun showToastMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}