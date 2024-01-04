package de.felixnuesse.timedsilence.volumestate

import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.REASON_TIME
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.util.DateUtil
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class Schedule(private var mContext: Context): DeterministicCalculationInterface() {

    private val mDbHandler = DatabaseHandler(mContext)

    override fun stateAt(timeInMs: Long): ArrayList<VolumeState> {
        TODO("Not yet implemented")
    }

    override fun states(): ArrayList<VolumeState> {

        val dayStart = DateUtil.getMidnight(date).atZone(ZoneId.systemDefault())
        val list = ArrayList<VolumeState>()

        mDbHandler.getSchedulesForWeekday(date.dayOfWeek).forEach {

            val startToMinutes = TimeUnit.MILLISECONDS.toMinutes(it.timeStart)
            val endToMinutes = TimeUnit.MILLISECONDS.toMinutes(it.timeEnd)



            if(it.timeEnd <= it.timeStart) {
                val midnightToEnd = VolumeState(it.timeSetting)
                midnightToEnd.startTime = dayStart.toInstant().toEpochMilli()
                midnightToEnd.endTime = dayStart.plusMinutes(endToMinutes).toInstant().toEpochMilli()
                midnightToEnd.setReason(REASON_TIME, it.name)
                list.add(midnightToEnd)

                val startToMidnight = VolumeState(it.timeSetting)
                startToMidnight.startTime = dayStart.plusMinutes(startToMinutes).toInstant().toEpochMilli()
                startToMidnight.endTime = dayStart.plusHours(24).toInstant().toEpochMilli()
                startToMidnight.setReason(REASON_TIME, it.name)
                list.add(startToMidnight)
            } else {
                val vs = VolumeState(it.timeSetting)
                vs.startTime = dayStart.plusMinutes(startToMinutes).toInstant().toEpochMilli()
                vs.endTime = dayStart.plusMinutes(endToMinutes).toInstant().toEpochMilli()
                vs.setReason(REASON_TIME, it.name)
                list.add(vs)
            }
        }

        return list
    }

    override fun isEnabled(): Boolean {
        return true
    }
}