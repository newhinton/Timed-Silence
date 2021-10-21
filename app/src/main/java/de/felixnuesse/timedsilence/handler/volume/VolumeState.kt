package de.felixnuesse.timedsilence.handler.volume

import de.felixnuesse.timedsilence.Constants

class VolumeState(val state: Int, val reason: Int, val reasonDescription: String) {

    fun getReason(): String{
        var s = ""
        when (reason) {
            Constants.REASON_CALENDAR -> s = "Volume changed because of calendar: $reasonDescription"
            Constants.REASON_KEYWORD -> s = "Volume changed because of Keyword in calendar: $reasonDescription"
            Constants.REASON_TIME -> s = "Volume changed because of time: $reasonDescription"
            Constants.REASON_UNDEFINED -> s = "No reason given."
            Constants.REASON_WIFI -> s = "Volume changed because of wifi: $reasonDescription"
        }
        return s
    }

}