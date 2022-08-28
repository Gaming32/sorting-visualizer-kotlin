package io.github.gaming32.sortviskt

class Stats {
    var writes = 0
        private set
    var reads = 0
        private set
    var accesses = 0
        private set

    fun reset() {
        writes = 0
        reads = 0
        accesses = 0
    }

    @JvmOverloads
    fun addReads(count: Int = 1) {
        reads += count
        accesses += count
    }

    @JvmOverloads
    fun addWrites(count: Int = 1) {
        writes += count
        accesses += count
    }
}