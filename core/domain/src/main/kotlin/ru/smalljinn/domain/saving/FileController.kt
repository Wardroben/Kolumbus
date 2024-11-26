package ru.smalljinn.domain.saving

import ru.smalljinn.model.data.response.FileError
import ru.smalljinn.model.data.response.Result

interface FileController {
    suspend fun writeBytes(uri: String, data: ByteArray): Result<Unit, FileError>
    suspend fun readBytes(uri: String): Result<ByteArray, FileError>
    fun deleteFile(uri: String): Result<Unit, FileError>

    /**
     * Should create temporary file which can deleted at any moment.
     */
    fun createTemporaryFile(): String
    fun clearCache()
}