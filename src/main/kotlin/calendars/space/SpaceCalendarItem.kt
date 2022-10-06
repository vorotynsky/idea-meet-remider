package calendars.space

import calendars.CalendarItem
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

class SpaceCalendarItem(override val title: String, override val dateTime: LocalDateTime, override val url: String?) : CalendarItem {
    override fun join() {
        TODO("Not yet implemented")
    }
}

@Serializable
data class SpaceCalendarItemDto(val id: String, val summary: String, val occurrenceRule: OccurrenceRule)

@Serializable
data class OccurrenceRule(val allDay: Boolean, val start: OccurrenceRuleDate, val end: OccurrenceRuleDate, val timezone: OccurrenceTimeZone)

@Serializable
data class OccurrenceRuleDate(val iso: String)

@Serializable
data class OccurrenceTimeZone(val id: String)

@Serializable
data class SpaceCalendarItemsDto(val data: List<SpaceCalendarItemDto>)