package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.handler.permissions.CalendarAccess
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.model.data.CalendarObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


import android.provider.CalendarContract.Events.CALENDAR_ID
import android.provider.CalendarContract.Events.TITLE
import android.provider.CalendarContract.Events.DESCRIPTION
import android.provider.CalendarContract.Events.DTSTART
import android.provider.CalendarContract.Events.DTEND
import android.provider.CalendarContract.Events.ALL_DAY
import android.provider.CalendarContract.Events.DURATION
import android.provider.CalendarContract.Events.EVENT_LOCATION
import android.provider.CalendarContract.Events.STATUS
import android.provider.CalendarContract.Events.AVAILABILITY
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.REASON_CALENDAR
import de.felixnuesse.timedsilence.Constants.Companion.REASON_KEYWORD
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.model.data.CachedArrayList
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.volumestate.DeterministicCalculationInterface
import java.time.ZoneId


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
        return hasCalendarReadPermission(mContext)
    }
}