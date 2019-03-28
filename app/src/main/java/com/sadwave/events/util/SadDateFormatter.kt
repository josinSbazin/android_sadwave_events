package com.sadwave.events.util

import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class SadDateFormatter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val postDateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
    private val postTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun parseDate(sDate: String?): Date? {
        sDate ?: return null
        return try {
            dateFormat.parse(sDate)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun parseTime(sTime: String?): Date? {
        sTime ?: return null
        return try {
            timeFormat.parse(sTime)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun getFormattedDate(sDate: String?): String {
        val result = parseDate(sDate) ?: return ""
        return postDateFormat.format(result)
    }

    fun getFormattedTime(sTime: String?): String {
        val result = parseTime(sTime) ?: return ""
        return postTimeFormat.format(result)
    }
}