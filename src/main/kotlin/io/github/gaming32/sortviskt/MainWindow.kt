package io.github.gaming32.sortviskt

import java.util.*
import javax.swing.*
import javax.swing.Timer

internal const val APP_NAME = "Kotlin Sorting Visualizer"

class MainWindow : JFrame(APP_NAME) {
    private val sorts = mutableMapOf<String, Sort>()
    val list = VisualList(2048)
    val graphics = GraphicsThread(list)
    var sortThread: SortThread? = null

    lateinit var chooseSort: JLabel
        private set

    var delayMultiplier = 1.0

    init {
        loadSorts()
        initComponents()

        defaultCloseOperation = DISPOSE_ON_CLOSE
        pack()
    }

    private fun loadSorts() = ServiceLoader.load(Sort::class.java).forEach { sort -> sorts[sort.name] = sort }

    private fun initComponents() {
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)

        chooseSort = JLabel("Choose sort:").also { label ->
            label.alignmentX = CENTER_ALIGNMENT
        }
        add(chooseSort)
        add(JComboBox(sorts.keys.toTypedArray()).also { chooseSort ->
            chooseSort.alignmentX = CENTER_ALIGNMENT
            chooseSort.addActionListener {
                var sortThread2 = sortThread
                if (sortThread2 != null && sortThread2.isAlive) return@addActionListener
                println("Selected ${chooseSort.selectedItem}")
                val sort = sorts[chooseSort.selectedItem] ?: return@addActionListener
                sortThread2 = SortThread(this@MainWindow, sort)
                sortThread = sortThread2
                sortThread2.start()
            }
        })

        add(Box.createVerticalStrut(10))

        add(JButton("Import sort").also { importSort ->
            importSort.alignmentX = CENTER_ALIGNMENT
            importSort.addActionListener {
                println("Import sort")
            }
        })

        add(JButton("Cancel delay").also { cancelDelay ->
            cancelDelay.alignmentX = CENTER_ALIGNMENT
            cancelDelay.addActionListener {
                println("Cancel delay")
            }
        })

        add(JButton("Change speed modifier").also { setDelay ->
            setDelay.alignmentX = CENTER_ALIGNMENT
            setDelay.addActionListener {
                println("Change speed modifier")
            }
        })
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val mainWindow = MainWindow()
        mainWindow.isVisible = true
        mainWindow.graphics.start()
        Timer(500) { event ->
            if (!mainWindow.graphics.isAlive) {
                mainWindow.dispose()
                (event.source as Timer).stop()
            }
        }.start()
    }
}
