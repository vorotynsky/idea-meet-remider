package calendars

import calendars.space.SpaceCalendarProvider

object CalendarStorage {
    val spaceCalendarProvider: SpaceCalendarProvider = SpaceCalendarProvider()

    private val notifications = mutableListOf<(List<CalendarItem>) -> Unit>()

    var events: List<CalendarItem> = emptyList()
        private set

    suspend fun loadAll() {
        val events = spaceCalendarProvider.load()
        this.events = events
        notifyAllSubscribers(events)
    }

    fun subscribe(subscription: (List<CalendarItem>) -> Unit) {
        notifications.add(subscription)
    }

    private fun notifyAllSubscribers(events: List<CalendarItem>) {
        notifications.forEach {
            it.invoke(events)
        }
    }
}
