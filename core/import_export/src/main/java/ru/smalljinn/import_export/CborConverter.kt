package ru.smalljinn.import_export

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import ru.smalljinn.model.data.response.ImportError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

private const val TAG = "CborConverter"

class CborConverter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cborFileManager: CborFileManager,
) {
    /**
     * Encodes places to [CborPlaceModel] and writes it to [outputFileUri] file.
     * Returns false if data not saved.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createBackupFile(places: Set<Place>, outputFileUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            val placesCbor = places.map { place ->
                val imagesBytes: List<ByteArray> = readImages(place.images)
                place.toCbor(imagesBytes)
            }
            try {
                val cborData = Cbor.encodeToByteArray<BackupData>(BackupData(placesCbor))
                cborFileManager.saveBackupFile(outputFileUri = outputFileUri, cborData = cborData)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating backup file:", e)
                false
            }
        }
    }

    /**
     * Returns list of [CborPlaceModel].
     * Images represents by [ByteArray] which needs to imported to filesDir
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun importBackupFile(uri: Uri): Result<List<CborPlaceModel>, ImportError> {
        return withContext(Dispatchers.IO) {
            when(val readResult = cborFileManager.readBackupFile(uri)) {
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
            }
        }
    }

    private suspend fun readImages(images: List<Image>): List<ByteArray> {
        return images.map { readImageBytes(it) }
    }

    private suspend fun readImageBytes(image: Image): ByteArray {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(image.url.toUri())!!.use {
                it.readBytes()
            }
        }
    }
}