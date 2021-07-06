package com.espressif.espblufi.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 邱永恒
 *
 * @time 2021/5/24  22:27
 *
 * @desc
 *
 */

object DateUtils {
    const val YYYYMMDD = "yyyy年MM月dd日"
    const val YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm"
    const val HH_MM = "HH:mm"

    fun formatDate(date: Date, pattern: String = "yyyy年MM月dd日"): String {
        val df = SimpleDateFormat(pattern, Locale.CHINA)
        return df.format(date)
    }

    fun formatDate(timestamp: Long, pattern: String = "yyyy年MM月dd日"): String {
        val date = Date(timestamp)
        return formatDate(date, pattern)
    }

    fun dateToTimestamp(date: String, pattern: String = "yyyy年MM月dd日"): Long {
        val df = SimpleDateFormat(pattern, Locale.CHINA)
        return df.parse(date)?.time ?: 0
    }

    fun toCalendar(date: String, pattern: String = "yyyy年MM月dd日"): Calendar {
        val calendar = Calendar.getInstance()
        val df = SimpleDateFormat(pattern, Locale.CHINA)
        calendar.time = df.parse(date) ?: Date(2021, 0, 0)
        return calendar
    }

    fun formatSeconds(seconds: Long): String {
        val hh = if (seconds / 3600 > 9) {
            (seconds / 3600).toString()
        } else {
            "0${seconds / 3600}"
        }

        val mm =
            if (seconds % 3600 / 60 > 9) {
                (seconds % 3600 / 60).toString()
            } else {
                "0${seconds % 3600 / 60}"
            }
        val ss =
            if (seconds % 3600 % 60 > 9) {
                (seconds % 3600 % 60).toString()
            } else {
                "0${seconds % 3600 % 60}"
            }
        return "$hh:$mm:$ss"
    }
}