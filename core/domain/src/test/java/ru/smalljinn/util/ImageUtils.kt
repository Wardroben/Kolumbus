package ru.smalljinn.util

import ru.smalljinn.model.data.Image

internal class ImageUtils {
    companion object {
        fun getTestImage(
            id: Long = 0L,
            uri: String = "fooUri"
        ): Image = Image(id, uri)

        fun getTestImages(count: Int): List<Image> =
            List(count) { index ->
                getTestImage(id = index.toLong(), uri = "fooUri$index")
            }
        fun getTestImages(range: IntRange): List<Image> =
            range.map { number ->
                getTestImage(id = number.toLong(), uri = "fooUri$number")
            }
    }
}