package io.github.gaming32.sortviskt.sorts

import io.github.gaming32.sortviskt.AbstractSort
import io.github.gaming32.sortviskt.VisualList
import java.util.Deque
import java.util.PriorityQueue

class DequeSort : AbstractSort("Deque Sort") {
    override fun run(list: VisualList) {
        if (list.size < 2) return

        // Collect deques into pq
        val pq = PriorityQueue<Deque<Int>> { a, b -> a.first.compareTo(b.first) }
        var dq: Deque<Int> = java.util.ArrayDeque()
        dq.add(list[0])
        for (i in 1 until list.size) {
            val item = list[i]
            if (item <= dq.first) {
                dq.addFirst(item)
            } else if (item >= dq.last) {
                dq.addLast(item)
            } else {
                pq.add(dq)
                if (i < list.size) {
                    dq = java.util.ArrayDeque()
                    dq.add(item)
                }
            }
        }
        if (dq.isNotEmpty()) {
            pq.add(dq)
        }

        // Write deques back into list
        var i = 0
        while (pq.isNotEmpty()) {
            dq = pq.poll()
            list[i++] = dq.pollFirst()
            if (dq.isNotEmpty()) {
                pq.add(dq)
            }
        }
    }
}
