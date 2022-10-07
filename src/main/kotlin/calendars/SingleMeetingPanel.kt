package calendars

import kotlinx.datetime.toJavaLocalDateTime
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.swing.*
import javax.swing.border.EmptyBorder


fun createMeetingPanel(meeting: CalendarItem): JPanel {
    val panel = JPanel().apply {
        layout = BorderLayout()
        border = EmptyBorder(10, 10, 10, 10)
    }
    val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
    val titleLabel = JLabel("<html><h2>${meeting.title}</h2></html>").apply { alignmentX = Component.LEFT_ALIGNMENT }
    val startDateFormatted = meeting.startDateTime.toJavaLocalDateTime().format(dateFormat)
    val startFormatted = meeting.startDateTime.toJavaLocalDateTime().format(timeFormat)
    val endFormatted = meeting.finishDateTime.toJavaLocalDateTime().format(timeFormat)
    val dateLabel = JLabel("<html><div align=\"center\">$startDateFormatted<br>$startFormatted - $endFormatted</div></html>").apply {
        alignmentX = Component.LEFT_ALIGNMENT
    }
    val urlButton = if (meeting.url != null) JButton("Copy URL").apply {
        toolTipText = meeting.url
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                meeting.copyToClipboard()
                this@apply.rootPane.requestFocus()
            }
        })
    } else null

    val joinButton = if (meeting.url != null) JButton("Open in browser").apply {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                try {
                    meeting.join()
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        panel,
                        "Could not open the hyperlink. Error: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                } finally {
                    this@apply.rootPane.requestFocus()
                }
            }
        })
    } else null
    panel.apply {
        add(titleLabel.apply { border = EmptyBorder(0, 20, 0, 0) }, BorderLayout.NORTH)
        val backgroundColor = background.darker()
        background = backgroundColor
        add(JPanel().apply {
            background = backgroundColor
            layout = BorderLayout()
            add(JPanel().apply {
                background = backgroundColor
                if (meeting.url != null) {
                    add(joinButton)
                    add(urlButton)
                }
                add(dateLabel)
            }, BorderLayout.WEST)
        })
    }
    return panel
}