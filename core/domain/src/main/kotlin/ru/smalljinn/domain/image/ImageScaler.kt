package ru.smalljinn.domain.image

interface ImageScaler<Image> {
    suspend fun scaleImage(image: Image, imageSize: Int): Image
}