package calendars

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater

class LoadCalendarEventsAction: AnAction("Load All Calendar Events") {
    override fun actionPerformed(e: AnActionEvent) {
        launchIoGlobally {
            CalendarStorage.loadAll()
            val count = CalendarStorage.events.size

            invokeLater {
                Notification(
                    "Print",
                    "Loaded $count meets",
                    NotificationType.INFORMATION
                ).notify(e.project)
            }
        }
    }
}