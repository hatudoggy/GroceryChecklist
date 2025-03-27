package com.example.grocerychecklist.util

object IdGenerator {
    @Volatile
    private var previousTimeMillis: Long = System.currentTimeMillis()
    @Volatile
    private var counter: Long = 0L
    private val lock = Any()

    fun nextID(): Long {
        val currentTimeMillis = System.currentTimeMillis()
        synchronized(lock) {
            counter = if (currentTimeMillis == previousTimeMillis) {
                (counter + 1L) and 1048575L
            } else {
                0L
            }
            previousTimeMillis = currentTimeMillis
            val timeComponent = (currentTimeMillis and 8796093022207L) shl 20
            return timeComponent or counter
        }
    }

}