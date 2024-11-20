package ru.smalljinn.kolumbus.data.saving

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.kolumbus.data.image.ImageFileProvider
import ru.smalljinn.kolumbus.data.util.clearCache
import ru.smalljinn.model.data.response.FileError
import ru.smalljinn.model.data.response.Result
import javax.inject.Inject

class AndroidFileController @Inject constructor(
    @ApplicationContext private val context: Context,
) : FileController {
    override suspend fun writeBytes(uri: String, data: ByteArray): Result<Unit, FileError> {
        runCatching {
            context.contentResolver.openOutputStream(uri.toUri())?.use { outputStream ->
                outputStream.write(data)
            } ?: throw IllegalStateException()
        }.onSuccess { return Result.Success(Unit) }

        return Result.Error(FileError.UNKNOWN)
    }

    override suspend fun readBytes(uri: String): Result<ByteArray, FileError> {
        runCatching {
            context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
                return Result.Success(inputStream.readBytes())
            } ?: throw IllegalStateException()
        }.onFailure {
            return Result.Error(FileError.FILE_NOT_READIED)
        }

        return Result.Error(FileError.UNKNOWN)
    }

    override fun deleteFile(uri: String): Result<Unit, FileError> {
        runCatching {
            val deletedCount = context.contentResolver.delete(uri.toUri(), null, null)
            if (deletedCount == 1) return Result.Success(Unit)
            else throw IllegalStateException()
        }
            .onSuccess { return Result.Success(Unit) }
            .onFailure { return Result.Error(FileError.FILE_NOT_DELETED) }

        return Result.Error(FileError.UNKNOWN)
    }

    override fun createTemporaryFile(): String {
        val tempImageFile = ImageFileProvider.createTemporaryFileForImage(context)
        val uri = ImageFileProvider.getUriForFile(tempImageFile, context)
        return uri.toString()
    }

    override fun clearCache() {
        context.clearCache(Dispatchers.IO)
    }
}