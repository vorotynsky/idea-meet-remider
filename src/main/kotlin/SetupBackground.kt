import calendars.CalendarStorage
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration


@OptIn(ExperimentalTime::class)
class SetupBackground : PostStartupActivity() {
    private val durationAll     = Duration.minutes(1)
    private val durationSuccess = Duration.minutes(4)

    @OptIn(ExperimentalTime::class, DelicateCoroutinesApi::class)
    override fun runActivity(project: Project) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val log = Logger.getInstance(SetupBackground::class.java)

            while (true) {
                try {
                    CalendarStorage.loadAll()

                    if (!trySendNotification(project)) {
                        delay(durationSuccess)
                    }
                } catch (e: Throwable) {
                    log.error(e)
                }

                println("new meets loaded, current count is ${CalendarStorage.events.size}")

                delay(Duration.seconds(10))
            }
        }

        Disposer.register(project) { job.cancel() }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun trySendNotification(
        project: Project
    ): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val minCalendarItem = CalendarStorage.events
            .filter { it.dateTime > now }
            .minByOrNull { it.dateTime }

        if (minCalendarItem != null) {
            val durationToEvent =
                java.time.Duration.between(now.toJavaLocalDateTime(), minCalendarItem.dateTime.toJavaLocalDateTime())
                    .toKotlinDuration()

            if (durationToEvent <= (durationAll + durationSuccess)) {
                delay(durationToEvent)
                Notification("Print", "Meet ${minCalendarItem.title} is started", NotificationType.INFORMATION).notify(
                    project
                )

                return true
            }
        }

        return false
    }
}
