package calendars

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class LoadCalendarEventsAction: AnAction("Load All Calendar Events") {
    override fun actionPerformed(e: AnActionEvent) {
        CalendarStorage.loadAll()
        val count = CalendarStorage.events.size

        Notification("Print", "Loaded $count meets", NotificationType.INFORMATION).notify(e.project)
    }
}