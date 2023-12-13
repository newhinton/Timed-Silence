package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.Context
import java.util.*
import kotlin.collections.ArrayList

import de.felixnuesse.timedsilence.Constants.Companion.REASON_KEYWORD
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.database.DatabaseHandler


class Keywords(private var mContext: Context): Events(mContext) {


    private val TAG: String = "Keywords"

    private val mDbHandler = DatabaseHandler(mContext)


    override fun stateAt(timeInMs: Long): ArrayList<VolumeState> {
        return ArrayList()
    }

    override fun states(): ArrayList<VolumeState> {

        val list = ArrayList<VolumeState>()

        getEventsForDay().forEach {

            val desc = it.mDescription.lowercase(Locale.getDefault())
            val name = it.mTitle.lowercase(Locale.getDefault())

            for (keyword in mDbHandler.getKeywords()){
                val key = keyword.keyword.lowercase(Locale.getDefault())
                if(desc.contains(key) || name.contains(key)){

                    val vs = VolumeState(keyword.volume)
                    vs.startTime = it.mStart
                    vs.endTime = it.mEnd
                    vs.setReason(REASON_KEYWORD, keyword.keyword)

                    list.add(vs)
                    continue
                }
            }
        }

        return list
    }

    override fun isEnabled(): Boolean {
        return DeviceCalendar.hasCalendarReadPermission(mContext)
    }
}