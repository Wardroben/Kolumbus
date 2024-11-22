package ru.smalljinn.kolumbus.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.first
import ru.smalljinn.domain.backup.BackupMessageProvider
import ru.smalljinn.domain.repository.ImportExportRepository
import ru.smalljinn.domain.repository.PlacesRepository
import ru.smalljinn.domain.repository.SearchPlacesRepository
import ru.smalljinn.domain.usecase.place.SavePlaceUseCase
import ru.smalljinn.import_export.CborConverter
import ru.smalljinn.import_export.toModel
import ru.smalljinn.kolumbus.data.image.AndroidImageSaver
import ru.smalljinn.model.data.Position
import ru.smalljinn.model.data.response.ExportError
import ru.smalljinn.model.data.response.ImportError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

class OfflineImportExportRepository @Inject constructor(
    private val backupMessageProvider: BackupMessageProvider,
    private val cborConverter: CborConverter,
    private val placesRepository: PlacesRepository,
    private val searchPlacesRepository: SearchPlacesRepository,
    private val imageSaver: AndroidImageSaver,
    private val savePlaceUseCase: SavePlaceUseCase
) : ImportExportRepository {
    override suspend fun exportPlaces(
        placeIds: Set<Long>,
        fileUri: Uri
    ): Result<Unit, ExportError> {
        val places = placeIds.map { id -> placesRepository.getPlace(id) }

        val fileCreated = cborConverter.createBackupFile(places.toSet(), fileUri)

        return if (fileCreated) Result.Success(Unit)
        else Result.Error(ExportError.FILE_NOT_CREATED)
    }

    override suspend fun importPlaces(fileUri: Uri): Result<Int, ImportError> {
        return when (val importResult = cborConverter.importBackupFile(fileUri)) {
            is Result.Error -> {
                backupMessageProvider.showErrorMessage(importResult.error)
                Result.Error(importResult.error)
            }
            is Result.Success -> {
                val cborPlaces = importResult.data
                var importedPlacesCount = 0
                var skippedPlacesCount = 0
                for (cborPlace in cborPlaces) {
                    val similarPlace =
                        searchPlacesRepository
                            .searchPlaces("${cborPlace.title} ${cborPlace.description}")
                            .first()
                            .filter { place ->
                                place.creationDate.toEpochMilliseconds() == cborPlace.timestamp
                                        && place.position == Position(
                                    cborPlace.latitude,
                                    cborPlace.longitude
                                )
                            }
                    //if local db has similar place skip it
                    if (similarPlace.isNotEmpty() && similarPlace.size == 1) {
                        skippedPlacesCount++
                        continue
                    }

                    val placeImagesUris =
                        cborPlace.images.map { byteArray -> imageSaver.saveImageToFilesDir(byteArray) }
                    savePlaceUseCase(
                        place = cborPlace.toModel(imageUris = placeImagesUris),
                        imagesToDelete = emptySet(),
                        compressImages = false,
                        isImporting = true
                    )
                    importedPlacesCount++
                }
                backupMessageProvider.showSuccessMessage(importedPlacesCount, skippedPlacesCount)
                Result.Success(importedPlacesCount)
            }
        }
    }
    /*override suspend fun importPlaces(fileUri: Uri) {
        val backupData: ru.smalljinn.import_export.CborPlacesBackup =
            cborFileManager.readCborBackupFile(fileUri.toString())
        for (placeCbor in backupData.places) {
            val position = Position(latitude = placeCbor.latitude, longitude = placeCbor.longitude)
            val creationDate = Instant.fromEpochMilliseconds(placeCbor.timestamp)
            val search = searchPlacesRepository
                .searchPlaces(query = "${placeCbor.title} ${placeCbor.description}")
                .first()
                .filter { it.position == position && it.creationDate == creationDate }

            if (search.isNotEmpty()) continue

            val placeEntity = PlaceEntity(
                id = 0L,
                title = placeCbor.title,
                description = placeCbor.description,
                position = position,
                creationDate = creationDate,
                headerImageId = placeCbor.headerImageId,
                favorite = placeCbor.favorite
            )

            val placeId = placesDao.upsertPlace(place = placeEntity)
            val images = placeCbor.images
                .map { data -> imageFileManager.saveImage(data) }
                .map { uri -> ImageEntity(imageId = 0L, uri = uri.toString(), placeId = placeId) }

            imageDao.insertImages(images)
        }
    }*/
}