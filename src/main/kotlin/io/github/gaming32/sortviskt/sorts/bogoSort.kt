package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList
import kotlin.random.Random

fun <T : Comparable<T>> List<T>.isSorted(): Boolean {
    for (i in 1 until size) {
        if (this[i - 1] > this[i]) {
            return false
        }
    }
    return true
}

class BogoSort : AbstractSort("Bogo Sort") {
    override fun run(list: VisualList) {
        while (!list.isSorted()) {
            list.shuffle()
        }
    }
}

class BozoSort : AbstractSort("Bozo Sort") {
    override fun run(list: VisualList) {
        while (!list.isSorted()) {
            list.swap(Random.nextInt(list.size), Random.nextInt(list.size))
        }
    }
}
