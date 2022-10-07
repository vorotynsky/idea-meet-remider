package calendars

interface CalendarProvider {
    val isLoggedIn: Boolean

    suspend fun load() : List<CalendarItem>
}
