package com.example.grocerychecklist.domain.utility

import java.time.LocalDateTime

class DateUtility {
    companion object {
        fun getCurrentDateTime(): LocalDateTime {
            return LocalDateTime.now()
        }
    }
}