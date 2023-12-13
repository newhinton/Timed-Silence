package de.felixnuesse.timedsilence.handler.volume

import de.felixnuesse.timedsilence.volumestate.calendar.DeviceCalendarEventModel
import java.util.Comparator

class VolumeStateStartComparator : Comparator<VolumeState> {

    override fun compare(o1: VolumeState, o2: VolumeState): Int {

        val s1 = o1.startTime
        val s2 = o2.startTime

        if (s1 < s2) {
            return -1
        }

        if (s1 > s2) {
            return 1
        }

        return 0
    }
}