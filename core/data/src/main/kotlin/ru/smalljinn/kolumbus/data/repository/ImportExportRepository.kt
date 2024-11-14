package ru.smalljinn.kolumbus.data.repository

import android.net.Uri
import ru.smalljinn.model.data.response.ExportError
import ru.smalljinn.model.data.response.ImportError
import ru.smalljinn.model.data.response.Result

interface ImportExportRepository {
    suspend fun exportPlaces(placeIds: Set<Long>, fileUri: Uri): Result<Unit, ExportError>
    suspend fun importPlaces(fileUri: Uri): Result<Unit, ImportError>
}