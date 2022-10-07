package calendars

import com.intellij.openapi.wm.ToolWindow
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import javax.swing.*
import javax.swing.border.EmptyBorder


fun createMeetingsPanel(toolWindow: ToolWindow, meetings: List<CalendarItem>): JPanel {
    val list = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(10, 10, 10, 10)
        add(JPanel().apply {
            add(RoundButton("↺").also { button ->
                button.preferredSize = Dimension(25, 25)
                button.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        button.rootPane.requestFocus()
                        toolWindow.contentManager.removeAllContents(false)
                        toolWindow.addEventList(listOf())
                        
                        launchIoGlobally {
                            CalendarStorage.loadAll()
                        }
                    }
                })
            })
        })
        meetings.sortedBy { it.startDateTime }.forEach {
            val dimension = Dimension(0, 10)
            add(Box.Filler(dimension, dimension, dimension))
            add(createMeetingPanel(it))
        }
    }
    return JPanel().apply {
        layout = BorderLayout()
        add(list, BorderLayout.NORTH)
    }.let { innerPanel ->
        val scroller = JScrollPane(innerPanel).apply {
            border = null
        }
        JPanel().apply {
            layout = BorderLayout()
            add(scroller, BorderLayout.CENTER)
        }
    }
}

class RoundButton(label: String?) : JButton(label) {
    override fun paintComponent(g: Graphics) {
        if (getModel().isArmed) {
            g.color = Color.gray
        } else {
            g.color = background
        }
        g.fillOval(0, 0, size.width - 1, size.height - 1)
        super.paintComponent(g)
    }

    override fun paintBorder(g: Graphics) {
        g.color = foreground
        g.drawOval(0, 0, size.width - 1, size.height - 1)
    }

    var shape: Shape? = null

    init {
        val size = preferredSize
        size.height = Math.max(size.width, size.height)
        size.width = size.height
        preferredSize = size
        isContentAreaFilled = false
    }

    override fun contains(x: Int, y: Int): Boolean {
        if (shape == null ||
            !shape!!.bounds.equals(bounds)
        ) {
            shape = Ellipse2D.Float(0f, 0f, width.toFloat(), height.toFloat())
        }
        return shape!!.contains(x.toDouble(), y.toDouble())
    }
}