package de.felixnuesse.timedsilence.handler.volume

import java.util.Comparator

class VolumeStateStateComparator : Comparator<VolumeState> {

    override fun compare(o1: VolumeState, o2: VolumeState): Int {

        if(o1.state == o2.state) {
            return 0
        }

        if (VolumeState.isFirstLouder(o1, o2)) {
            return -1
        }

        if (VolumeState.isFirstLouder(o2, o1)) {
            return -1
        }
        return 0
    }
}