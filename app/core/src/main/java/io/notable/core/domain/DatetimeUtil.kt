package io.notable.core.domain

import kotlinx.datetime.*

class DatetimeUtil {
    private fun now(): LocalDateTime {
        val currentMoment: Instant = Clock.System.now()
        return currentMoment.toLocalDateTime(TimeZone.UTC)
    }

    fun humanizeDatetime(createdAt: String): String {
        val date = createdAt.substring(0, createdAt.length - 8).toLocalDateTime()
        val sb = StringBuilder()
        date.run {
            val hour = if (this.hour > 12) {
                (this.hour - 12).toString() + "pm"
            } else {
                if (this.hour != 0) this.hour.toString() + "am" else "midnight"
            }
            val today = now()

            when {
                this.date == today.date -> {
                    sb.append("Today at $hour")
                }
                this.date.year == today.date.year -> {
                    sb.append("${appendZeroToDate(this.date.dayOfMonth)}.${appendZeroToDate(this.date.monthNumber)}")
                }
                else -> {
                    sb.append("${appendZeroToDate(this.date.dayOfMonth)}.${appendZeroToDate(this.date.monthNumber)}.${this.date.year}")
                }
            }

        } ?: sb.append("Unknown")
        return sb.toString()
    }

    private fun appendZeroToDate(date: Int): String {
        return if (date < 10) "0$date" else "$date"
    }

}