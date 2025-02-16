package com.example.grocerychecklist.domain.utility

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateUtility {
    companion object {
        fun getCurrentDateTime(): LocalDateTime {
            return LocalDateTime.now()
        }

        fun isCurrentMonth(date: String): Boolean {
            val formattedDate = "$date 01"
            val inputDate = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("MMM yyyy dd"))
            val currentDate = LocalDate.now()

            return inputDate.month == currentDate.month && inputDate.year == currentDate.year
        }

        fun areDatesMatching(dateFromList: String, dateFromBackend: String): Boolean {
            return dateFromList == dateFromBackend.split(" ")[0] + " " + dateFromBackend.split(" ")[1]
        }

        fun formatDate(createdAt: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
            return createdAt.toLocalDate().format(formatter)
        }

        fun formatDateWithDay(createdAt: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
            return createdAt.toLocalDate().format(formatter)
        }
    }
}