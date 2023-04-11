package com.vladrip.ifchat.utils

import android.content.Context
import com.vladrip.ifchat.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.WeekFields
import java.util.Locale

object FormatHelper {

    private fun format(dateTime: LocalDateTime, pattern: String): String {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern))
    }

    fun formatLastOnline(dateTime: LocalDateTime, context: Context): String {
        val now = LocalDateTime.now()
        val weekOfMonth = WeekFields.of(Locale.getDefault()).weekOfMonth()
        return when {
            dateTime.year > now.year -> {
                val yearDiff = dateTime.year - now.year
                return if (yearDiff == 1)
                    context.getString(R.string.last_online, context.getString(R.string.last_year))
                else context.getString(R.string.last_online,
                    "$yearDiff ${context.getString(R.string.years_ago)}")
            }

            dateTime.month.value != now.month.value ||
                    dateTime.get(weekOfMonth) != dateTime.get(weekOfMonth) ->
                context.getString(R.string.last_online_at, format(dateTime, "MMM dd, hh:ss"))

            dateTime.dayOfYear != dateTime.dayOfYear ->
                context.getString(R.string.last_online_at, format(dateTime, "EEE, hh:ss"))

            dateTime.hour != now.hour ->
                context.getString(R.string.last_online_at, format(dateTime, "hh:ss"))

            dateTime.minute != now.minute ->
                context.getString(R.string.last_online,
                    "${dateTime.minute} ${context.getString(R.string.ago)}")

            else -> return "online"
        }
    }

    fun formatLastSent(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val weekOfMonth = WeekFields.of(Locale.getDefault()).weekOfMonth()
        val formatter: DateTimeFormatter = when {
            dateTime.year != now.year ->
                DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

            dateTime.month.value != now.month.value ||
                    dateTime.get(weekOfMonth) != now.get(weekOfMonth) ->
                DateTimeFormatter.ofPattern("MMM dd")

            dateTime.dayOfYear != now.dayOfYear ->
                DateTimeFormatter.ofPattern("EEE")

            else -> DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.UK)
        }
        return dateTime.format(formatter)
    }
}