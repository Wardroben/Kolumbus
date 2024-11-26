package ru.smalljinn.kolumbus.data.image

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.smalljinn.di.Dispatcher
import ru.smalljinn.di.KolumbusDispatcher
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.domain.saving.FileNameGenerator
import javax.inject.Inject

class AndroidImageSaver @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(KolumbusDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
    private val nameGenerator: FileNameGenerator,
    private val fileController: FileController
) {
    suspend fun saveImageToFilesDir(imageData: ByteArray): Uri = withContext(ioDispatcher) {
        val imgFile = ImageFileProvider.createFileForImage(context, nameGenerator.constructImageFileName())
        val imageUri = ImageFileProvider.getUriForFile(imgFile, context)
        fileController.writeBytes(imageUri.toString(), imageData)
        imageUri
    }
}