package com.example.grocerychecklist.domain.utility

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class DateUtility {
    companion object {

        fun getCurrentDateTime(): LocalDateTime {
            return LocalDateTime.now()
        }

        fun getStartOfDay(startDate: LocalDateTime): LocalDateTime {
            return startDate.withHour(0).withMinute(0).withSecond(0).withNano(0)
        }

        // Add this to DateUtility
        fun getEndOfDay(startDate: LocalDateTime): LocalDateTime {
            return getStartOfDay(startDate).plusDays(1).minusNanos(1)
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
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            return createdAt.toLocalDate().format(formatter)
        }

        fun formatDateMonthOnly(createdAt: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MMMM")
            return createdAt.format(formatter)
        }

        fun getYesterdayStartOfDay(): LocalDateTime {
            return getStartOfDay(LocalDateTime.now().minusDays(1))
        }

        fun getThisWeekDateStartDay(): LocalDateTime {
            return getStartOfDay(LocalDateTime.now().minusDays(7))
        }

        fun getThisMonthStartDay(): LocalDateTime {
            val firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1)
            return getStartOfDay(firstDayOfMonth)
        }

        fun getThisMonthEndDay(): LocalDateTime {
            val lastDayOfMonth = LocalDateTime.now().plusMonths(1).minusDays(1)
            return getEndOfDay(lastDayOfMonth)
        }

        fun getCurrentMonth(): Month {
            return LocalDate.now().month
        }

        fun getPreviousMonthsFromNow(currentMonth: Month, monthsToAdd: Int): Month {
            val targetMonth = currentMonth.minus(monthsToAdd.toLong())
            return targetMonth
        }

        fun getStartOfMonthTimestamp(date: String): Timestamp {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val startOfMonth = LocalDate.parse("$date-01", formatter).atStartOfDay()
            return Timestamp(startOfMonth.toInstant(ZoneOffset.UTC).epochSecond, 0)
        }

        fun getEndOfMonthTimestamp(date: String): Timestamp {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val endOfMonth = LocalDate.parse("$date-01", formatter)
                .plusMonths(1).atStartOfDay()
            return Timestamp(endOfMonth.toInstant(ZoneOffset.UTC).epochSecond, 0)
        }
    }
}