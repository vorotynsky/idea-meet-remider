package calendars

import kotlinx.datetime.LocalDateTime
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

interface CalendarItem {
    val title: String
    val startDateTime: LocalDateTime
    val finishDateTime: LocalDateTime

    val url: String?

}

fun CalendarItem.join() {
    url?.let { Desktop.getDesktop().browse(URI(it)) }
}

fun CalendarItem.copyToClipboard() {
    if (url != null) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(url), null)
    }
}
