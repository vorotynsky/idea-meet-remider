import java.awt.BorderLayout
import java.awt.Dimension
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
            add(JButton("Update"), BorderLayout.LINE_START)
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