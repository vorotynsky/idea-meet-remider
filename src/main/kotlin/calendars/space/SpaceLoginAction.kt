package calendars.space

import calendars.CalendarStorage
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.ui.Messages
import calendars.launchIoGlobally
import java.net.URL

class SpaceLoginAction : AnAction("Login to Space for Meet Reminder") {

    override fun actionPerformed(e: AnActionEvent) {

        val url = Messages.showInputDialog("Organization URL:", "Space | IDEA Meet Reminder", null, "https://", null) ?: ""
        val password = Messages.showPasswordDialog("Personal permanent token:", "Space | IDEA Meet Reminder") ?: ""

        launchIoGlobally {
            CalendarStorage.spaceCalendarProvider.login(URL(url), password) {
                invokeLater {
                    if (it == null)
                        Notification(
                            "Plugin Error", "Invalid login or password.", NotificationType.ERROR
                        ).notify(e.project)
                    else Notification(
                        "Print", "Login success!", NotificationType.INFORMATION
                    ).notify(e.project)
                }
            }
        }
    }
}