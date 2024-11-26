package ru.smalljinn.domain.backup

import ru.smalljinn.model.data.response.ImportError

interface BackupMessageProvider {
    fun showSuccessMessage(importedPlaces: Int, skippedDuplicatesPlaces: Int)
    fun showErrorMessage(importError: ImportError)
}