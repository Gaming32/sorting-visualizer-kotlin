package io.github.gaming32.sortviskt

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import kotlin.concurrent.withLock

class GraphicsThread(val list: VisualList) : Thread("GraphicsThread") {
    var shouldShowStats = true
    var label = ""

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

        val window = glfwCreateWindow(videoMode.width() / 2, videoMode.height() / 2, APP_NAME, 0, 0)
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        glfwSwapInterval(1)

        glfwSetWindowSizeCallback(window) { _, width, height -> glViewport(0, 0, width, height) }
        glViewport(0, 0, videoMode.width() / 2, videoMode.height() / 2)

        glClearColor(0f, 0f, 0f, 1f)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        glTranslatef(0f, 0f, -200f)
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT)
            glDisable(GL_TEXTURE_2D)

            list.lengthLock.withLock { render() }

            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        glfwDestroyWindow(window)
        glfwTerminate()
    }

    private fun render() {
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, list.size.toDouble(), 0.0, list.size.toDouble(), 100.0, 300.0)
        glBegin(GL_TRIANGLE_STRIP)
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
}
