package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList
import io.github.holygrailsortproject.rewrittengrailsort.GRAIL_STATIC_EXT_BUFFER_LEN
import io.github.holygrailsortproject.rewrittengrailsort.lazyStableSort
import io.github.holygrailsortproject.rewrittengrailsort.GrailSort as GrailSortImpl

class GrailSort : AbstractSort("Grail Sort") {
    override fun run(list: VisualList) = GrailSortImpl<Int>().commonSort(list, null)
}

class GrailSortStaticOop : AbstractSort("Grail Sort (Static OOP)") {
    override fun run(list: VisualList) = GrailSortImpl<Int>().commonSort(list, arrayOfNulls(GRAIL_STATIC_EXT_BUFFER_LEN))
}

class GrailSortDynamicOop : AbstractSort("Grail Sort (Dynamic OOP)") {
    override fun run(list: VisualList) = GrailSortImpl<Int>().commonSort(list, arrayOfNulls(run {
        var bufferLen = 1
        while (bufferLen * bufferLen < list.size) {
            bufferLen *= 2
        }
        bufferLen
    }))
}

class LazyStableSort : AbstractSort("Lazy Stable Sort") {
    override fun run(list: VisualList) = list.lazyStableSort()
}
