package calendars

import calendars.space.SpaceCalendarProvider

object CalendarStorage {
    val spaceCalendarProvider: SpaceCalendarProvider = SpaceCalendarProvider()

    var events: List<CalendarItem> = emptyList()
        private set

    suspend fun loadAll() {
        events = spaceCalendarProvider.load()
    }
}
