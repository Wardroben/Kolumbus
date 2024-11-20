package ru.smalljinn.kolumbus.data.image

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.domain.saving.FileNameGenerator
import javax.inject.Inject

class AndroidImageSaver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val nameGenerator: FileNameGenerator,
    private val fileController: FileController
) {
    suspend fun saveImageToFilesDir(imageData: ByteArray): Uri = withContext(Dispatchers.IO) {
        val imgFile = ImageFileProvider.createFileForImage(context, nameGenerator.constructImageFileName())
        val imageUri = ImageFileProvider.getUriForFile(imgFile, context)
        fileController.writeBytes(imageUri.toString(), imageData)
        imageUri
    }
}