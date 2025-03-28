package com.example.grocerychecklist.data.dto

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId


fun Timestamp.toLocalDateTime(): LocalDateTime {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun LocalDateTime.toTimestamp(): Timestamp {
    return Timestamp(this.atZone(ZoneId.systemDefault()).toInstant().epochSecond, 0)
}