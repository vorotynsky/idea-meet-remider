package calendars

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.delay

class EventListFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.addEventList(listOf())
        
        launchIoGlobally {
            while (true) {
                try {
                    updateEvents(toolWindow)
                    delay(60 * 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(15 * 1000)
                }
            }
        }
    }
}