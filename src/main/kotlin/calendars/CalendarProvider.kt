package calendars

interface CalendarProvider {
    val isLoggedIn: Boolean

    fun load()
}
