import calendars.CalendarStorage
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.file.impl.FileManager
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration


@OptIn(ExperimentalTime::class)
class SetupBackground : PostStartupActivity() {
    private val durationAll     = Duration.seconds(1)
    private val durationSuccess = Duration.seconds(4)

    @OptIn(ExperimentalTime::class, DelicateCoroutinesApi::class)
    override fun runActivity(project: Project) {
        GlobalScope.launch(Dispatchers.IO) {
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

                delay(durationAll)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun trySendNotification(
        project: Project
    ): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val minCalendarItem = CalendarStorage.events
            .filter { it.startDateTime > now }
            .minByOrNull { it.startDateTime }

        if (minCalendarItem != null) {
            val durationToEvent =
                java.time.Duration.between(now.toJavaLocalDateTime(), minCalendarItem.startDateTime.toJavaLocalDateTime())
                    .toKotlinDuration()


            if (durationToEvent <= (durationAll + durationSuccess)) {
                delay(durationToEvent)
                Notification("Print", "Meet ${minCalendarItem.title} is started", NotificationType.INFORMATION).notify(
                    project
                )

                val list = mutableListOf<VirtualFile?>()

                WriteCommandAction.runWriteCommandAction(project) {
                    for (selectedFile in FileEditorManager.getInstance(project).selectedFiles) {
                        selectedFile?.isWritable = false
                        list.add(selectedFile)
                    }
                }


                val durationToEnd =
                    java.time.Duration.between(now.toJavaLocalDateTime(), minCalendarItem.finishDateTime.toJavaLocalDateTime())
                        .toKotlinDuration()

                delay(durationToEnd)

                WriteCommandAction.runWriteCommandAction(project) {
                    for (virtualFile in list) {
                        virtualFile?.isWritable = false
                    }
                }

                return true
            }
        }

        return false
    }
}
