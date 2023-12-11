package de.felixnuesse.timedsilence.volumestate

import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeStateStartComparator
import de.felixnuesse.timedsilence.handler.volume.VolumeStateStateComparator
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
        mEvents.date = mDate
        mKeywords.date = mDate
        mSchedules.date = mDate
    }

    fun stateAt(timeInMs: Long): VolumeState {
        for (state in states()) {
            if(state.startTime <= timeInMs && timeInMs <= state.endTime) {
                return state
            }
        }
        return VolumeState(mPreferencesManager.getDefaultUnsetVolume())
    }

    fun states(): ArrayList<VolumeState> {
        val start = System.currentTimeMillis()
        Log.e(TAG, "Start-time: $start")

        val stateList = arrayListOf<VolumeState>()

        stateList.addAll(mEvents.states())
        stateList.addAll(mKeywords.states())
        stateList.addAll(mSchedules.states())

        Collections.sort(stateList, VolumeStateStartComparator())


        //todo: this is a bit clunky. Maybe there is a better way?
        val timeList = ArrayList<Long>()
        stateList.forEach {
            if (!timeList.contains(it.startTime)) {timeList.add(it.startTime)}
            if (!timeList.contains(it.endTime)) {timeList.add(it.endTime)}
        }


        // Todo: check if the performance can be improved
        timeList.forEach {
            var deleteList = ArrayList<VolumeState>()
            var addList = ArrayList<VolumeState>()

            for(state in stateList) {
                if(state.startTime < it && it < state.endTime) {
                    var a = state.copy()
                    var b = state.copy()

                    a.endTime = it
                    b.startTime = it

                    addList.add(a)
                    addList.add(b)
                    deleteList.add(state)
                }
            }

            stateList.removeAll(deleteList.toSet())
            stateList.addAll(addList)
        }


        val map = HashMap<Long, ArrayList<VolumeState>>()
        stateList.forEach {
            if(map.containsKey(it.startTime)) {
                map[it.startTime]?.add(it)
            } else {
                map[it.startTime] = arrayListOf(it)
            }
        }

        var linearList = ArrayList<VolumeState>()
        map.forEach { (key, value) ->
            if(value.size > 1) {
                Collections.sort(value, VolumeStateStateComparator())
                linearList.addAll(arrayListOf(value.first()))
            } else {
                linearList.addAll(value)
            }
        }

        Collections.sort(linearList, VolumeStateStartComparator())

        val end = System.currentTimeMillis()
        Log.e(TAG, "Endtime: $end")
        Log.e(TAG, "Diff: ${end-start}ms")
        return padList(linearList)
    }

    private fun padList(list: ArrayList<VolumeState>): ArrayList<VolumeState> {
        val dayStart = DateUtil.getMidnight(mDate).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dayEnd = DateUtil.getMidnight(mDate).plusHours(24).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


        val first = list.firstOrNull()

        if(first == null || first.startTime > dayStart) {
            val initialVolumeState = VolumeState(defaultVolume)
            initialVolumeState.startTime = dayStart
            initialVolumeState.endTime = first?.startTime?: dayEnd
            list.add(0, initialVolumeState)
        }

        val last = list.last()
        if(last.endTime > dayEnd) {
            list.remove(last)
            last.endTime = dayEnd
            list.add(last)
        }

        if(last.endTime < dayEnd) {
            val lastVolumeState = VolumeState(defaultVolume)
            lastVolumeState.startTime = last.endTime
            lastVolumeState.endTime = dayEnd
            list.add(lastVolumeState)
        }

        if(list.size>1) {
            for (i in 0..<list.size) {
                val current = list[i].endTime
                val next = list[i+1].startTime

                // If the difference is bigger than a minute, insert filler
                if(next-current>60*1000) {
                    val fillerState = VolumeState(defaultVolume)
                    fillerState.startTime = current
                    fillerState.endTime = next
                    list.add(fillerState)
                }
            }
        }
        Collections.sort(list, VolumeStateStartComparator())
        return list
    }

    fun setDayOffset(dayOffset: Int) {
        mDate = LocalDateTime.now().plusDays(dayOffset.toLong())
        mEvents = Events(mContext)
        mKeywords = Keywords(mContext)
        mSchedules = Schedule(mContext)
        mEvents.date = mDate
        mKeywords.date = mDate
        mSchedules.date = mDate
    }


}