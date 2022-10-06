import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.datetime.*

class MeetingsListFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val meetings = List(2) {
            Meeting("a$it", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),"https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        }
        toolWindow.contentManager.addContent(com.intellij.ui.content.impl.ContentImpl(createMeetingsPanel(meetings), "", false))
    }
}