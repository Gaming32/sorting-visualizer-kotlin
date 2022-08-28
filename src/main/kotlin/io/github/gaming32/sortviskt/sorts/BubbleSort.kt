package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList

class BubbleSort : AbstractSort("Bubble Sort") {
    override fun run(list: VisualList) {
        for (i in list.indices.reversed()) {
            var sorted = true
            for (j in 1 until i) {
                if (list[j - 1] > list[j]) {
                    sorted = false
                    list.swap(j - 1, j)
                }
            }
            if (sorted) break
        }
    }
}
