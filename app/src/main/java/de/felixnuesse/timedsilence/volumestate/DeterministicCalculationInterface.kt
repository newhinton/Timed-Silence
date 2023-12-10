package de.felixnuesse.timedsilence.volumestate

import de.felixnuesse.timedsilence.handler.volume.VolumeState
import java.time.LocalDateTime

abstract class DeterministicCalculationInterface {

    var date: LocalDateTime = LocalDateTime.now()

    abstract fun stateAt(timeInMs: Long): ArrayList<VolumeState>

    abstract fun states(): ArrayList<VolumeState>

    fun getCurrentState(): ArrayList<VolumeState> {
        return stateAt(System.currentTimeMillis())
    }

    abstract fun isEnabled(): Boolean

}