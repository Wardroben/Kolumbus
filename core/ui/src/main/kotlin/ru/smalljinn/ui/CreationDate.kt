package ru.smalljinn.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun CreationDate(modifier: Modifier = Modifier, creationDate: Instant) {
    val formattedDate = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(TimeZone.currentSystemDefault().toJavaZoneId())
        .format(creationDate.toJavaInstant())
    Text(formattedDate, style = MaterialTheme.typography.labelSmall, modifier = modifier)
}