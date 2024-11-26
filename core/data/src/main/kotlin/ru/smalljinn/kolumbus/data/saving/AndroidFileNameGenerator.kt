package ru.smalljinn.kolumbus.data.saving

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.format.format
import kotlinx.datetime.toLocalDateTime
import ru.smalljinn.domain.saving.FileNameGenerator
import javax.inject.Inject

private const val PREFIX = "image_"
private const val POSTFIX = ".img"
class AndroidFileNameGenerator @Inject constructor(): FileNameGenerator {
    override fun constructImageFileName(): String {
        val nameBuilder = StringBuilder()
        val compoundFormat = DateTimeComponents.Format {
            date(LocalDate.Formats.ISO); char('_')
            hour(); char(':'); minute(); char(':'); second(); char(':'); secondFraction(4)
        }
        val time = Clock.System.now()
        val formattedData = compoundFormat.format {
            setDateTime(time.toLocalDateTime(TimeZone.currentSystemDefault()))
        }
        nameBuilder.append(PREFIX)
        nameBuilder.append(formattedData)
        nameBuilder.append(POSTFIX)
        return nameBuilder.toString()
    }
}