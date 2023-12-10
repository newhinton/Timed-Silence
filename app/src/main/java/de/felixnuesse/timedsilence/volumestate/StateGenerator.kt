package de.felixnuesse.timedsilence.volumestate

import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeStateComparator
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.volumestate.calendar.Events
import de.felixnuesse.timedsilence.volumestate.calendar.Keywords
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Collections

class StateGenerator(private var mContext: Context) {

    companion object {
        private const val TAG = "StateGenerator"
    }

    var mDate: LocalDateTime = LocalDateTime.now()
    var mPreferencesManager = PreferencesManager(mContext)
    var defaultVolume = mPreferencesManager.getDefaultUnsetVolume()

    var mEvents = Events(mContext)
    var mKeywords = Keywords(mContext)
    var mSchedules = Schedule(mContext)

    init {
        LogHandler.writeLog(mContext, TAG, "instantiate","VolumeCalculator was now instantiated")
        mDate = LocalDateTime.now().plusDays(1)
        mEvents.date = mDate
        mKeywords.date = mDate
        mSchedules.date = mDate
    }

    fun stateAt(timeInMs: Long): VolumeState {
        return VolumeState(0)
    }

    private fun states(): ArrayList<VolumeState> {
        val start = System.currentTimeMillis()
        Log.e(TAG, "Start-time: $start")

        val stateList = arrayListOf<VolumeState>()

        stateList.addAll(mEvents.states())
        stateList.addAll(mKeywords.states())
        stateList.addAll(mSchedules.states())

        Collections.sort(stateList, VolumeStateComparator())

        val dayStart = DateUtil.getMidnight(mDate).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dayEnd = DateUtil.getMidnight(mDate).plusHours(24).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val first = stateList.firstOrNull()
        Log.e(TAG, "DS: ${DateUtil.getDate(dayStart)}")
        Log.e(TAG, "DE: ${DateUtil.getDate(dayEnd)}")
        Log.e(TAG, "FS: ${first?.startTime}")

        if(first == null || first.startTime > dayStart) {
            val initialVolumeState = VolumeState(defaultVolume)
            initialVolumeState.startTime = dayStart
            initialVolumeState.endTime = first?.startTime?: dayEnd
            stateList.add(0, initialVolumeState)
        }

        val last = stateList.last()
        if(last.endTime > dayEnd) {
            stateList.remove(last)
            last.endTime = dayEnd
            stateList.add(last)
        }

        if(last.endTime < dayEnd) {
            val lastVolumeState = VolumeState(defaultVolume)
            lastVolumeState.startTime = last.endTime
            lastVolumeState.endTime = dayEnd
            stateList.add(lastVolumeState)
        }

        for (i in 0..<stateList.size) {
            val current = stateList[i].endTime
            val next = stateList[i+1].startTime

            // If the difference is bigger than a minute, insert filler
            if(next-current>60*1000) {
                val fillerState = VolumeState(defaultVolume)
                fillerState.startTime = current
                fillerState.endTime = next
                stateList.add(fillerState)
            }
        }

        stateList.forEach {
            Log.e(TAG, "-----")
            Log.e(TAG, "State: ${it.state}")
            Log.e(TAG, "reason: ${it.getReason()}")
            Log.e(TAG, "start: ${it.startTime} - ${DateUtil.getDate(it.startTime)}")
            Log.e(TAG, "end: ${it.endTime} - ${DateUtil.getDate(it.endTime)}")
            Log.e(TAG, "-----")
        }

        Collections.sort(stateList, VolumeStateComparator())
        val end = System.currentTimeMillis()
        Log.e(TAG, "Endtime: $end")
        Log.e(TAG, "Diff: ${end-start}ms")
        return stateList
    }

    fun getLinearStates(): ArrayList<VolumeState> {
        var states = states()

        var timeList = ArrayList<Long>()

        states.forEach {
            if(!timeList.contains(it.startTime)) {
                timeList.add(it.startTime)
            }
            if(!timeList.contains(it.startTime)) {
                timeList.add(it.endTime)
            }
        }

        timeList.sort()



        timeList.forEach {
            Log.e(TAG, "Time: $it")
        }

        //val stateList = arrayListOf<VolumeState>()




        return states
    }



}