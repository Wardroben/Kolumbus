package ru.smalljinn.domain.image

interface ImageGetter<Image> {
    suspend fun getImage(uri: String): Image?
    fun getImageFormat(uri: String): ImageFormat
}