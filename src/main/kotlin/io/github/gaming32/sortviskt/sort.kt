package io.github.gaming32.sortviskt

import javax.swing.SwingUtilities
import kotlin.concurrent.withLock

interface Sort {
    val name: String

    fun run(list: VisualList)
}

abstract class AbstractSort(override val name: String) : Sort

class SortThread(private val window: MainWindow, private val sort: Sort) : Thread("SortThread") {
    init {
        isDaemon = true
    }

    override fun run() {
        SwingUtilities.invokeAndWait {
            window.chooseSort.isEnabled = false
        }
        val oldDelay = window.delayMultiplier
        window.delayMultiplier = 1024.0 / window.list.size
        window.list.delay = window.delayMultiplier
        window.graphics.label = "Shuffling..."
        window.list.stats.reset()
        window.list.shuffle()
        window.graphics.label = "            "
        window.delayMultiplier = oldDelay
        window.list.delay = oldDelay
        window.list.marksLock.withLock {
            window.list.marks.clear()
        }
        sleep(750)
        window.graphics.label = sort.name
        window.list.stats.reset()
        sort.run(window.list)
        window.list.marksLock.withLock {
            window.list.marks.clear()
        }
        SwingUtilities.invokeLater {
            window.chooseSort.isEnabled = true
        }
    }
}
