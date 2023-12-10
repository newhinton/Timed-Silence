package de.felixnuesse.timedsilence.handler.volume

import de.felixnuesse.timedsilence.Constants
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class VolumeState(var startTime: Int, val state: Int, val reason: Int, val reasonDescription: String) {

    var endTime: Int = startTime

    val duration: Int
        get() = endTime-startTime

    companion object {
        const val TIME_SETTING_SILENT = 1
        const val TIME_SETTING_VIBRATE = 2
        const val TIME_SETTING_LOUD = 3
        const val TIME_SETTING_UNSET = -1

        fun timeSettingToReadable(setting: Int): String {
            return when(setting) {
                TIME_SETTING_SILENT -> "Silent"
                TIME_SETTING_VIBRATE -> "Vibrate"
                TIME_SETTING_LOUD -> "Loud"
                else -> "unset/other"
            }
        }
    }


    fun getReason(): String{
        var s = ""
        when (reason) {
            Constants.REASON_CALENDAR -> s = "Volume changed because of calendar: $reasonDescription"
            Constants.REASON_KEYWORD -> s = "Volume changed because of Keyword in calendar: $reasonDescription"
            Constants.REASON_TIME -> s = "Volume changed because of time: $reasonDescription"
            Constants.REASON_UNDEFINED -> s = "Default Volume."
            Constants.REASON_WIFI -> s = "Volume changed because of wifi: $reasonDescription"
        }
        return s
    }

    fun getFormattedStartDate(): String {
        return getFormattedDate(startTime)
    }

    fun getFormattedEndDate(): String {
        return getFormattedDate(endTime)
    }

    private fun getFormattedDate(time: Int): String {
        var today = LocalDate.now(ZoneId.systemDefault())
        var todayMidnight = LocalDateTime.of(today, LocalTime.MIDNIGHT)
        val timestamp = todayMidnight.plusMinutes(time.toLong())

        var shortFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        return timestamp.toLocalTime().format(shortFormat)
    }

    override fun toString(): String {


        var stringReason = reason.toString()
        when (reason) {
            Constants.REASON_CALENDAR -> stringReason = "CALENDAR"
            Constants.REASON_KEYWORD -> stringReason = "KEYWORD"
            Constants.REASON_TIME -> stringReason = "TIME"
            Constants.REASON_UNDEFINED -> stringReason = "UNDEFINED"
            Constants.REASON_WIFI -> stringReason = "WIFI"
        }

        return "VolumeState(startTime=${getFormattedStartDate()}, endTime=${getFormattedEndDate()}, duration=$duration, state=${stateString()}, reason=$stringReason, reasonDescription='$reasonDescription')"
    }

    fun stateString(): String {
        return when (state) {
            TIME_SETTING_SILENT -> "SILENT"
            TIME_SETTING_VIBRATE -> "VIBRATE"
            TIME_SETTING_LOUD -> "LOUD"
            TIME_SETTING_UNSET -> "UNSET"
            else -> state.toString()
        }
    }


}