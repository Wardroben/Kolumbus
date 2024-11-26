package ru.smalljinn.kolumbus.data.share

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.domain.share.ShareProvider
import ru.smalljinn.model.data.Place
import javax.inject.Inject

class AndroidShareProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ShareProvider {
    override suspend fun sharePlace(place: Place) {
        val text = buildString {
            with(place) {
                append(place.title)
                if (place.description.isNotBlank()) append("\n\n${place.description}")
                append("\n\nhttps://www.google.com/maps/place/${position.latitude},${position.longitude}")
            }
        }
        val headerImageUri =
            place.images.find { it.id == place.headerImageId }?.url?.toUri()
                ?: place.images.firstOrNull()?.url?.toUri()
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, headerImageUri ?: return)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "image/*"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}