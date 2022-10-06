import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.swing.*
import javax.swing.border.EmptyBorder


fun createMeetingPanel(meeting: Meeting): JPanel {
    val panel = JPanel().apply {
        layout = BorderLayout()
        border = EmptyBorder(10, 10, 10, 10)
    }
    val dateFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    val nameLabel = JLabel("<html><h2>${meeting.name}</h2></html>").apply { alignmentX = Component.LEFT_ALIGNMENT }
    val dateLabel = JLabel(meeting.date.format(dateFormat)).apply { alignmentX = Component.LEFT_ALIGNMENT }
    val urlButton = JButton("Copy URL").apply {
        toolTipText = meeting.url
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(meeting.url), null)
            }
        })
    }

    val joinButton = JButton("Join meeting").apply {
        toolTipText = "Open in browser"
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                try {
                    Desktop.getDesktop().browse(URI(meeting.url))
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        panel,
                        "Could not open the hyperlink. Error: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        })
    }
    panel.apply {
        add(nameLabel.apply { border = EmptyBorder(0, 20, 0, 0) }, BorderLayout.NORTH)
        val backgroundColor = background.darker()
        background = backgroundColor
        add(JPanel().apply {
            background = backgroundColor
            layout = BorderLayout()
            add(JPanel().apply {
                background = backgroundColor
                add(joinButton)
                add(urlButton)
                add(dateLabel)
            }, BorderLayout.WEST)
        })
    }
    return panel
}