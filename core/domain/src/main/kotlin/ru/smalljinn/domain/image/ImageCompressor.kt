package ru.smalljinn.domain.image

interface ImageCompressor<Image> {
    suspend fun compressImage(
        image: Image,
        imageFormat: ImageFormat
    ): ByteArray
}