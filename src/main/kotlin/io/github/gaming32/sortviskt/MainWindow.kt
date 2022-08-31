package io.github.gaming32.sortviskt

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*
import javax.swing.Timer
import kotlin.concurrent.thread
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

internal const val APP_NAME = "Kotlin Sorting Visualizer"

class MainWindow : JFrame(APP_NAME) {
    private val sorts = mutableMapOf<String, Sort>()
    val list = VisualList(2048)
    val graphics = GraphicsThread(this)
    private var sortThread: SortThread? = null

    lateinit var chooseSort: JLabel
        private set
    lateinit var cancelSort: JButton
        private set

    internal var playSound = true
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

        chooseSort = add(JLabel("Choose sort:").also { label ->
            label.alignmentX = CENTER_ALIGNMENT
        }) as JLabel
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

//        add(JButton("Import sort").also { importSort ->
//            importSort.alignmentX = CENTER_ALIGNMENT
//            importSort.addActionListener {
//                println("Import sort")
//            }
//        })

        add(JButton("Cancel delay").also { cancelDelay ->
            cancelDelay.alignmentX = CENTER_ALIGNMENT
            cancelDelay.addActionListener {
                println("Cancel delay")
                list.delay = 0.0
            }
        })

        cancelSort = add(JButton("Cancel sort").also { cancelSort ->
            cancelSort.alignmentX = CENTER_ALIGNMENT
            cancelSort.isEnabled = false
            cancelSort.addActionListener {
                println("Cancel sort")
                list.cancel()
            }
        }) as JButton

        add(JButton("Change speed modifier").also { setDelay ->
            setDelay.alignmentX = CENTER_ALIGNMENT
            setDelay.addActionListener {
                println("Change speed modifier")
                val newDelayString = JOptionPane.showInputDialog(
                    this, "Enter a speed multiplier:", (1 / delayMultiplier).toString()
                ) ?: return@addActionListener
                val newDelay = newDelayString.toDoubleOrNull()
                if (newDelay == null) {
                    JOptionPane.showMessageDialog(
                        this, "Not a valid number: $newDelayString", title, JOptionPane.ERROR_MESSAGE
                    )
                    return@addActionListener
                }
                delayMultiplier = 1 / newDelay
                list.delay = delayMultiplier
            }
        })

        add(JCheckBox("Show stats").also { checkBox ->
            checkBox.alignmentX = CENTER_ALIGNMENT
            checkBox.isSelected = graphics.shouldShowStats
            checkBox.addActionListener {
                graphics.shouldShowStats = checkBox.isSelected
            }
        })

        add(JCheckBox("Play sound").also { checkBox ->
            checkBox.alignmentX = CENTER_ALIGNMENT
            checkBox.isSelected = playSound
            checkBox.addActionListener {
                playSound = checkBox.isSelected
            }
        })

        add(JSlider(JSlider.VERTICAL, 10_0000, 20_00000, 11_00000).also { slider ->
            var lockToPow2 = false
            var lockSlider = false
            slider.addChangeListener {
                if (lockSlider) return@addChangeListener
                lockSlider = true
                var valuePow2 = slider.value / 1_00000.0
                if (lockToPow2) {
                    valuePow2 = valuePow2.roundToInt().toDouble()
                }
                slider.value = (valuePow2 * 1_00000).toInt()
                list.reset(2.0.pow(valuePow2).toInt())
                lockSlider = false
            }
            slider.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount == 2) {
                        val newSizeString = JOptionPane.showInputDialog(
                            this@MainWindow,
                            "Enter array size:",
                            2.0.pow(slider.value / 1_00000.0).roundToInt().toString()
                        ) ?: return
                        val newSize = newSizeString.toIntOrNull()
                        if (newSize == null) {
                            JOptionPane.showMessageDialog(
                                this@MainWindow,
                                "Not a valid integer: $newSizeString",
                                title,
                                JOptionPane.ERROR_MESSAGE
                            )
                            return
                        }
                        slider.value = (log2(newSize.toDouble()) * 1_00000).toInt()
                    }
                }
            })
            val keyListener = object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_SHIFT) {
                        lockToPow2 = true
                    }
                }

                override fun keyReleased(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_SHIFT) {
                        lockToPow2 = false
                    }
                }
            }
            addKeyListener(keyListener)
            slider.addKeyListener(keyListener)
        })
    }

    override fun dispose() {
        graphics.interrupt()
        super.dispose()
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val mainWindow = MainWindow()
        mainWindow.isVisible = true
        mainWindow.graphics.start()
        while (!mainWindow.graphics.isAlive) {
            // Just wait
        }
        thread {
            SoundSystem(mainWindow).use { soundSystem ->
                while (mainWindow.graphics.isAlive) {
                    soundSystem.tick()
                    Thread.sleep(1000 / 60)
                }
            }
        }
        Timer(500) { event ->
            if (!mainWindow.graphics.isAlive) {
                mainWindow.dispose()
                (event.source as Timer).stop()
            }
        }.start()
    }
}
