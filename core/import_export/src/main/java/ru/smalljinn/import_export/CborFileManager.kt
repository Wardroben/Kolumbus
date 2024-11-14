package ru.smalljinn.import_export

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.model.data.response.ImportError
import ru.smalljinn.model.data.response.Result
import java.io.FileNotFoundException

private const val TAG = "CborFileManager"
class CborFileManager(
    @ApplicationContext private val context: Context
) {
    suspend fun saveBackupFile(cborData: ByteArray, outputFileUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(outputFileUri)?.use { outputStream ->
                    outputStream.write(cborData)
                } ?: return@withContext false
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error writing data:", e)
                false
            }
        }
    }

    suspend fun readBackupFile(inputFileUri: Uri): Result<ByteArray, ImportError> {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(inputFileUri).use { inputStream ->
                    inputStream?.let {
                        val data = inputStream.readBytes()
                        Result.Success(data)
                    } ?: Result.Error(ImportError.UNKNOWN)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading data:", e)
                Result.Error(ImportError.UNKNOWN)
            } catch (e: FileNotFoundException) {
                Result.Error(ImportError.FILE_NOT_FOUND)
            }
        }
    }
}