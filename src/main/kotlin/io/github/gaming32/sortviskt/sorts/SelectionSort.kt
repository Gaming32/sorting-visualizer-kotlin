package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList

class SelectionSort : AbstractSort("Selection Sort") {
    override fun run(list: VisualList) {
        for (i in 0 until list.size - 1) {
            var least = list[i]
            var leastIndex = i
            for (j in i + 1 until list.size) {
                if (list[j] < least) {
                    least = list[j]
                    leastIndex = j
                }
            }
            if (leastIndex != i) {
                list.swap(i, leastIndex)
            }
        }
    }
}
