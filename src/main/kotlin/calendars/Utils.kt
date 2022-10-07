package calendars

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.impl.ContentImpl
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.suspendCoroutine

suspend fun Call.executeAsync() = suspendCoroutine {
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            it.resumeWith(Result.failure(e))
        }

        override fun onResponse(call: Call, response: Response) {
            it.resumeWith(Result.success(response))
        }
    })
}

@OptIn(DelicateCoroutinesApi::class)
fun launchIoGlobally(f: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.IO) { f() }

fun ToolWindow.addEventList(events: List<CalendarItem>) {
    contentManager.addContent(ContentImpl(createMeetingsPanel(this, events), "", false))
}

suspend fun updateEvents(toolWindow: ToolWindow) {
    val events = CalendarStorage.spaceCalendarProvider.load()
    invokeLater {
        toolWindow.contentManager.removeAllContents(false)
        toolWindow.addEventList(events)
    }
}
