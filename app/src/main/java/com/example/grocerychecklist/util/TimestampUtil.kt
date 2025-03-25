package com.example.grocerychecklist.util

import java.time.LocalDateTime

object TimestampUtil {
    fun parseTimestamp(map: Map<*, *>?): LocalDateTime? {
        return map?.let {
            try {
                LocalDateTime.of(
                    (it["year"] as Long).toInt(),
                    (it["monthValue"] as Long).toInt(),
                    (it["dayOfMonth"] as Long).toInt(),
                    (it["hour"] as Long).toInt(),
                    (it["minute"] as Long).toInt(),
                    (it["second"] as Long).toInt(),
                    (it["nano"] as Long).toInt()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}