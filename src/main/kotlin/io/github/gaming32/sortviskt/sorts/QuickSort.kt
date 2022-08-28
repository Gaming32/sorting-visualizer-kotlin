package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList

class QuickSort : AbstractSort("Quick Sort") {
    override fun run(list: VisualList) {
        quickSort(list, 0, list.size - 1)
    }

    private fun quickSort(list: VisualList, low: Int, high: Int) {
        if (low < high) {
            val splitIndex = partition(list, low, high)
            quickSort(list, low, splitIndex)
            quickSort(list, splitIndex + 1, high)
        }
    }

    private fun partition(list: VisualList, low: Int, high: Int): Int {
        val pivot = list[(low + high) / 2]
        var i = low - 1
        var j = high + 1
        while (true) {
            while (list[++i] < pivot) {
                // Intentionally empty
            }
            while (list[--j] > pivot) {
                // Intentionally empty
            }
            if (i >= j) return j
            list.swap(i, j)
        }
    }
}
