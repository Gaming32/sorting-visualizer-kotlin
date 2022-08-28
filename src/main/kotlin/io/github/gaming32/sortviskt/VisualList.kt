package io.github.gaming32.sortviskt

import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class VisualList(initialLength: Int) : AbstractList<Int>(), RandomAccess {
    internal var internal = (0 until initialLength).toMutableList()
    internal var marks = mutableSetOf<Int>()
        private set
    private var currentDelay = 0.0
    var singleDelay = 0.0
    var delay = 1.0
        set(value) {
            if (value == 0.0) {
                currentDelay = 0.0
            }
            field = value
        }
    internal val lengthLock: Lock = ReentrantLock()
    internal val marksLock: Lock = ReentrantLock()
    val stats = Stats()

    fun sleep(ms: Double) {
        singleDelay = ms
        if (delay == 0.0) return
        currentDelay += ms
        while (currentDelay > 0.0) {
            val start = System.nanoTime()
            Thread.sleep(1)
            val end = System.nanoTime()
            if (delay == 0.0) {
                currentDelay = 0.0
            } else {
                currentDelay -= (end - start) / 1e6 / delay
            }
        }
        singleDelay = 0.0
    }

    internal fun reset(length: Int) {
        lengthLock.withLock {
            internal.clear()
            internal.addAll(0 until length)
        }
        marksLock.withLock {
            marks.clear()
        }
        currentDelay = 0.0
    }

    fun swap(i: Int, j: Int) {
        this[i] = set(j, get(i))
    }

    fun reverseRange(i: Int, j: Int) {
        var a = i
        var b = j
        while (a < b) {
            swap(a++, b--)
        }
    }

    fun reverse() {
        reverseRange(0, size - 1)
    }

    override val size: Int
        get() = internal.size

    override operator fun set(index: Int, element: Int): Int {
        stats.addWrites()
        val old = internal.set(index, element)
        marksLock.withLock {
            marks.clear()
            marks.add(index)
        }
        sleep(0.5)
        return old
    }

    override operator fun get(index: Int): Int {
        stats.addReads()
        marksLock.withLock {
            marks.clear()
            marks.add(index)
        }
        sleep(0.5)
        return internal[index]
    }
}
