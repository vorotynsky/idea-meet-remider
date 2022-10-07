package calendars

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.delay

class EventListFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.addEventList(listOf())

        CalendarStorage.subscribe {
            launchIoGlobally {
                updateEvents(toolWindow, it)
            }
        }
    }
}
