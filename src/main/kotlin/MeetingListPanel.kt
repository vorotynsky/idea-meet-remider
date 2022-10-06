import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

fun createMeetingsPanel(meetings: List<Meeting>): JPanel {
    val list = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(10, 10, 10, 10)
        add(JPanel().apply {
            border = EmptyBorder(5, 5, 5, 5)
            layout = BorderLayout()
            add(JButton("Update").also { button ->
                button.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        button.rootPane.requestFocus()
                    }
                })
            }, BorderLayout.CENTER)
        })
        meetings.forEach {
            val dimension = Dimension(0, 10)
            add(Box.Filler(dimension, dimension, dimension))
            add(createMeetingPanel(it))
        }
    }
    return JPanel().apply {
        layout = BorderLayout()
        add(list, BorderLayout.NORTH)
    }
}