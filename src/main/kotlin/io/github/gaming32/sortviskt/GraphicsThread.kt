package io.github.gaming32.sortviskt

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import kotlin.concurrent.withLock

class GraphicsThread(private val list: VisualList) : Thread("GraphicsThread") {
    var shouldShowStats = true
    var label = ""
    var windowSize = 0 to 0

    init {
        isDaemon = false
    }

    override fun run() {
        if (!glfwInit()) {
            throw RuntimeException("Failed to initialize GLFW")
        }

        glfwDefaultWindowHints()

        val monitor = glfwGetPrimaryMonitor()
        val videoMode = glfwGetVideoMode(monitor)
            ?: throw RuntimeException("You need a monitor to run this application")

        windowSize = Pair(videoMode.width() / 2, videoMode.height() / 2)
        val window = glfwCreateWindow(windowSize.first, windowSize.second, APP_NAME, 0, 0)
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        glfwSwapInterval(1)

        glfwSetWindowSizeCallback(window) { _, width, height ->
            windowSize = Pair(width, height)
            glViewport(0, 0, width, height)
        }
        glViewport(0, 0, windowSize.first, windowSize.second)

        glClearColor(0f, 0f, 0f, 1f)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        glTranslatef(0f, 0f, -200f)
        while (!glfwWindowShouldClose(window) && !interrupted()) {
            glClear(GL_COLOR_BUFFER_BIT)

            list.lengthLock.withLock { render() }
            if (shouldShowStats) {
                showStats()
            }

            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        glfwDestroyWindow(window)
        glfwTerminate()
    }

    private fun render() {
        glDisable(GL_TEXTURE_2D)
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, list.size.toDouble(), 0.0, list.size.toDouble(), 100.0, 300.0)
        glBegin(GL_TRIANGLES)
        for (i in list.indices) {
            if (i in list.marks) {
                glColor3f(1f, 0f, 0f)
            } else {
                glColor3f(1f, 1f, 1f)
            }
            val x = i.toFloat()
            val height = list.internal[i].toFloat()
            glVertex2f(x, 0f)
            glVertex2f(x, height)
            glVertex2f(x + 1, 0f)
            glVertex2f(x, height)
            glVertex2f(x + 1, height)
            glVertex2f(x + 1, 0f)
        }
        glEnd()
    }

    private fun showStats() {
        glEnable(GL_TEXTURE_2D)
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, windowSize.first / 2.0, windowSize.second / 2.0, 0.0, 100.0, 300.0)
        val delay = list.singleDelay * list.delay
        val stats = list.stats
        val text = label +
            " ${list.size.toString().padStart(7)} numbers" +
            "   ${delay.toString().padStart(5)}ms delay" +
            "   ${stats.writes.toString().padStart(4)} writes" +
            "   ${stats.accesses.toString().padStart(4)} accesses"
        TextRenderer.drawText(text, 5f, 5f, 0xffffff)
    }
}
