package ru.smalljinn.domain.image

sealed class ImageFormat(val mimeType: String) {
    data object Png: ImageFormat(mimeType = "image/png")
    data object Jpeg: ImageFormat(mimeType = "image/jpeg")
    data object Webp: ImageFormat(mimeType = "image/webp")

    companion object {
        val Default: ImageFormat by lazy { Jpeg }
        operator fun get(typeString: String?): ImageFormat = when (typeString) {
            null -> Default
            "image/png" -> Png
            "image/jpeg" -> Jpeg
            "image/webp" -> Webp
            else -> Default
        }
    }
}