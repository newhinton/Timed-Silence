package de.felixnuesse.timedsilence.handler.volume

import de.felixnuesse.timedsilence.Constants.Companion.REASON_CALENDAR
import de.felixnuesse.timedsilence.Constants.Companion.REASON_KEYWORD
import de.felixnuesse.timedsilence.Constants.Companion.REASON_MANUALLY_SET
import de.felixnuesse.timedsilence.Constants.Companion.REASON_TIME
import de.felixnuesse.timedsilence.Constants.Companion.REASON_UNDEFINED
import de.felixnuesse.timedsilence.Constants.Companion.REASON_WIFI
import de.felixnuesse.timedsilence.util.DateUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class VolumeState(var state: Int) {

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

        fun isFirstLouder(first: VolumeState, second: VolumeState): Boolean {
            return first.state > second.state && first.state != TIME_SETTING_UNSET
        }
    }

    constructor(startTime: Long, state: Int, reason: Int, reasonDescription: String): this(state) {
        this.startTime = startTime
        this.endTime = startTime
        this.state = state
        this.reason = reason
        this.reasonDescription = reasonDescription
    }

    var startTime: Long = 0
    var endTime: Long = startTime

    private var reason: Int = REASON_UNDEFINED
    private var reasonDescription: String = ""

    val duration: Long
        get() = endTime-startTime


    fun setReason(reason: Int, reasonDescription: String) {
        this.reason = reason
        this.reasonDescription = reasonDescription
    }

    //Todo: translate this!
    fun getReason(): String {
        var s = ""
        when (reason) {
            REASON_CALENDAR -> s = "Volume changed because of calendar: $reasonDescription"
            REASON_KEYWORD -> s = "Volume changed because of Keyword in calendar: $reasonDescription"
            REASON_TIME -> s = "Volume changed because of time: $reasonDescription"
            REASON_UNDEFINED -> s = "Default Volume."
            REASON_WIFI -> s = "Volume changed because of wifi: $reasonDescription"
            REASON_MANUALLY_SET -> s = "Volume was set manually: $reasonDescription"
        }
        return s
    }

    fun getFormattedStartDate(): String {
        return DateUtil.getDate(startTime, "HH:mm")
    }

    fun getFormattedEndDate(): String {
        return DateUtil.getDate(endTime, "HH:mm")
    }

    private fun getFormattedDate(time: Long): String {
        var today = LocalDate.now(ZoneId.systemDefault())
        var todayMidnight = LocalDateTime.of(today, LocalTime.MIDNIGHT)
        val timestamp = todayMidnight.plusMinutes(time)

        var shortFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        return timestamp.toLocalTime().format(shortFormat)
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

    override fun toString(): String {

        var stringReason = reason.toString()
        when (reason) {
            REASON_CALENDAR -> stringReason = "CALENDAR"
            REASON_KEYWORD -> stringReason = "KEYWORD"
            REASON_TIME -> stringReason = "TIME"
            REASON_UNDEFINED -> stringReason = "UNDEFINED"
            REASON_WIFI -> stringReason = "WIFI"
            REASON_MANUALLY_SET -> stringReason = "MANUAL"
        }

        return "VolumeState(startTime=${getFormattedStartDate()}, endTime=${getFormattedEndDate()}, duration=$duration, state=${stateString()}, reason=$stringReason, reasonDescription='$reasonDescription')"
    }

    fun copy(): VolumeState {
        val copy = VolumeState(state)
        copy.startTime = startTime
        copy.endTime = endTime
        copy.reason = reason
        copy.reasonDescription = reasonDescription
        return copy
    }
}