package calendars

import kotlinx.datetime.LocalDateTime

interface CalendarItem {
    val title: String
    val dateTime: LocalDateTime

    val url: String?

    fun join()
}
