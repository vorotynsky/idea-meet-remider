package calendars

import calendars.space.SpaceCalendarProvider

object CalendarStorage {
    val spaceCalendarProvider: SpaceCalendarProvider = SpaceCalendarProvider()
    private var myEvents: List<CalendarItem> = listOf()

    public var events: List<CalendarItem>
        get() = myEvents
        private set(value) { myEvents = value }

    fun loadAll() {
        myEvents = spaceCalendarProvider.load()
    }
}
