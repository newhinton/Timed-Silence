package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.Context
import android.provider.CalendarContract
import de.felixnuesse.timedsilence.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.regex.Pattern

class DeviceCalendarEventModel(mContext: Context, private val mDate: LocalDateTime) {

    private val TAG: String = "DeviceCalendarEventModel"

    var mCalendarID = 0
    var mTitle = mContext.getString(R.string.event_no_title)
    var mDescription = mContext.getString(R.string.event_no_description)
    var mStart = 0L
    var mEnd = 0L
    var mAllDay = false
    var mDuration = "" // This is a pattern, not an int!
    var mStatus = 0
    var mAvailability = 0


    fun setTitle(title: String?) {
        if(!title.isNullOrEmpty()) {
            mTitle = title
        }
    }
    fun setDescription(description: String?) {
        if(!description.isNullOrEmpty()) {
            mDescription = description
        }
    }

    fun setDtstart(start: Long) {
        mStart = getRecurringEventTimeOffset(start)
    }

    fun setOrCalculateDtend(end: Long, duration: String) {
        if(duration.isNotBlank()) {
            mDuration = duration
            val sPattern = Pattern.compile("P\\d+S")
            if (sPattern.matcher(mDuration).matches()) {
                val sb = StringBuffer(mDuration)
                sb.insert(mDuration.indexOf("P") + 1, "T")
                mDuration = sb.toString()
            }

            val durationLength = Duration.parse(mDuration).toMillis()
            val calculatedEndTime = mStart + durationLength
            mEnd = calculatedEndTime
        } else {
            if(end == 0L) {
                mEnd = mStart
            } else {
                mEnd = end
            }
        }
    }

    private fun getRecurringEventTimeOffset(time: Long): Long {
        //the start time is from the FIRST time the event happens, so adjust it (recurring events)

        // Also obey the mDate variable to allow other days to be caluclated.
        val caldendarDateToCheck = Calendar.getInstance()
        caldendarDateToCheck.timeInMillis = mDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(
            caldendarDateToCheck.get(Calendar.YEAR),
            caldendarDateToCheck.get(Calendar.MONTH),
            caldendarDateToCheck.get(Calendar.DAY_OF_MONTH)
        )
        return calendar.timeInMillis
    }


    fun shouldEventBeExcluded(ignoreAllday: Boolean, ignoreTentative: Boolean, ignoreCancelled: Boolean, ignoreFree: Boolean): Boolean {
        var isIgnoredEventType = false
        if (mAllDay && ignoreAllday) {
            //Log.d(TAG, "Event $mTitle is all Day.")
            isIgnoredEventType = true
        }

        if (mStatus == CalendarContract.Events.STATUS_TENTATIVE && ignoreTentative) {
            //Log.d(TAG, "Event $mTitle is Tentative.")
            isIgnoredEventType = true
        }

        val bc2workaround = mDescription.contains("BC2-Status: Cancelled")
        if ((mStatus == CalendarContract.Events.STATUS_CANCELED || bc2workaround) && ignoreCancelled) {
            //Log.d(TAG, "Event $mTitle is Cancelled.")
            isIgnoredEventType = true
        }

        if (mAvailability == CalendarContract.Events.AVAILABILITY_FREE && ignoreFree) {
            //Log.d(TAG, "Event $mTitle is Free.")
            isIgnoredEventType = true
        }

        return isIgnoredEventType
    }
}