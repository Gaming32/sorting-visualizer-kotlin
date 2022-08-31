package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList
import io.github.holygrailsortproject.rewrittengrailsort.lazyStableSort

class GrailSort : AbstractSort("Grail Sort") {
    override fun run(list: VisualList) {
        io.github.holygrailsortproject.rewrittengrailsort.GrailSort<Int>().commonSort(list, null)
    }
}

class LazyStableSort : AbstractSort("Lazy Stable Sort") {
    override fun run(list: VisualList) {
        list.lazyStableSort()
    }
}
