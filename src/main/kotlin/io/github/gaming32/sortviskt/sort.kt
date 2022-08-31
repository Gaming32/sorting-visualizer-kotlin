package io.github.gaming32.sortviskt

import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.concurrent.withLock

interface Sort {
    val name: String

    fun run(list: VisualList)
}

abstract class AbstractSort(override val name: String) : Sort

internal class CancelledException : Exception()

class SortThread(private val window: MainWindow, private val sort: Sort) : Thread("SortThread") {
    init {
        isDaemon = true
    }

    override fun run() {
        SwingUtilities.invokeAndWait {
            window.chooseSort.isEnabled = false
            window.cancelSort.isEnabled = true
        }
        val oldDelay = window.delayMultiplier
        window.delayMultiplier = 1024.0 / window.list.size
        window.list.delay = window.delayMultiplier
        window.graphics.label = "Shuffling..."
        window.list.stats.reset()
        try {
            window.list.shuffle()
        } catch (t: Throwable) {
            if (t !is CancelledException) {
                t.printStackTrace()
                showError("Shuffle error: $t")
            }
            return cleanup()
        }
        window.graphics.label = "            "
        window.delayMultiplier = oldDelay
        window.list.delay = oldDelay
        window.list.marksLock.withLock {
            window.list.marks.clear()
        }
        sleep(750)
        window.graphics.label = sort.name
        window.list.stats.reset()
        try {
            sort.run(window.list)
        } catch (t: Throwable) {
            if (t !is CancelledException) {
                t.printStackTrace()
                showError("Sort error: $t")
            }
        }
        cleanup()
    }

    private fun cleanup() {
        window.list.marksLock.withLock {
            window.list.marks.clear()
        }
        SwingUtilities.invokeLater {
            window.chooseSort.isEnabled = true
            window.cancelSort.isEnabled = false
        }
    }

    private fun showError(message: String) {
        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(window, message, APP_NAME, JOptionPane.ERROR_MESSAGE)
        }
    }
}
