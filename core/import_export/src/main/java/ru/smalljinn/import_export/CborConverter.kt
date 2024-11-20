package ru.smalljinn.import_export

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.response.ImportError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

private const val TAG = "CborConverter"

class CborConverter @Inject constructor(
    private val fileController: FileController
) {
    /**
     * Encodes places to [CborPlaceModel] and writes it to [outputFileUri] file.
     * Returns false if data not saved.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createBackupFile(places: Set<Place>, outputFileUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val placesCbor = places.map { place ->
                    val imagesBytes: List<ByteArray> = readImages(place.images)
                    place.toCbor(imagesBytes)
                }
                val cborData = Cbor.encodeToByteArray<BackupData>(BackupData(placesCbor))
                fileController.writeBytes(outputFileUri.toString(), cborData)

            }
                .onSuccess { return@withContext true }
                .onFailure { return@withContext false }

            return@withContext false
        }


    /**
     * Returns list of [CborPlaceModel].
     * Images represents by [ByteArray] which needs to imported to filesDir
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun importBackupFile(uri: Uri): Result<List<CborPlaceModel>, ImportError> =
         withContext(Dispatchers.IO) {
            runCatching {
                when (val readResult = fileController.readBytes(uri.toString())) {
                    is Result.Error -> return@withContext Result.Error(ImportError.UNKNOWN)
                    is Result.Success -> {
                        val backupData = Cbor.decodeFromByteArray<BackupData>(readResult.data)
                        return@withContext Result.Success(backupData.cborPlaces)
                    }
                }
            }
            Result.Error(ImportError.UNKNOWN)
            /*when (val readResult = cborFileManager.readBackupFile(uri)) {
                is Result.Error -> Result.Error(readResult.error)
                is Result.Success -> {
                    try {
                        val backupData = Cbor.decodeFromByteArray<BackupData>(readResult.data)
                        Result.Success(backupData.cborPlaces)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error reading backup file:", e)
                        Result.Error(ImportError.UNKNOWN)
                    } catch (e: IllegalArgumentException) {
                        Result.Error(ImportError.NOT_BACKUP_FILE)
                    }
                }
            }*/
        }

    private suspend fun readImages(images: List<Image>): List<ByteArray> {
        return images.map { image ->
            when(val result = fileController.readBytes(image.url)) {
                is Result.Error -> throw IllegalStateException()
                is Result.Success -> result.data
            }
        }
    }
}