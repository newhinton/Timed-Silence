package de.felixnuesse.timedsilence.model.calendar

import android.content.Context
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler

class SettingsCalendar(private var mContext: Context) {

    private var calendarCache = HashMap<String, CalendarObject>()
    private var mDatabase = DatabaseHandler(mContext)

    fun getCalendars(): HashMap<String, CalendarObject> {
        if(calendarCache.size>0){
            return calendarCache
        }
        mDatabase.getAllCalendarEntries().forEach {
            calendarCache[it.name]=it
        }


        return calendarCache
    }
}