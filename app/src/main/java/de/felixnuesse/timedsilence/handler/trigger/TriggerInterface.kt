package de.felixnuesse.timedsilence.handler.trigger

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.ui.notifications.PausedNotification
import java.text.DateFormat
import java.util.*

interface TriggerInterface {
    companion object {
        private const val TAG = "TriggerInterface"
    }

    var mContext: Context

    fun createTimecheck()

    fun removeTimecheck()

    fun createBroadcast(targettime: Long): PendingIntent?

    fun createBroadcast(flag: Int, targettime: Long): PendingIntent?

    fun checkIfNextAlarmExists(): Boolean {
        val pIntent = createBroadcast(PendingIntent.FLAG_NO_CREATE, 0L)
        return if (pIntent == null) {
            Log.d(TAG, "TriggerInterface: There is no next Alarm set!")
            PausedNotification.show(mContext)
            false
        } else {
            Log.d(TAG, "TriggerInterface: There is an upcoming Alarm!")
            PausedNotification.cancelNotification(mContext)
            true
        }
    }

    fun getNextAlarmTimestamp(): String {
        val alarms = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val clockInfo = alarms.nextAlarmClock ?: return mContext.getString(R.string.no_next_time_set)

        Log.d(TAG, "TriggerInterface: Next Runtime: " + clockInfo.triggerTime)
        return DateFormat.getDateInstance().format(Date(clockInfo.triggerTime))

    }
}