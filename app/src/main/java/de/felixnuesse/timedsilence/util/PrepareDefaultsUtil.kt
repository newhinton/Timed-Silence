package de.felixnuesse.timedsilence.util

import android.content.Context
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler

class PrepareDefaultsUtil {

    companion object {
        fun addDefaults(context: Context) {
            val db = DatabaseHandler(context)

            var nightStartHour = 21
            var nightStarMinutes = 0
            var nightEndHour = 8
            var nightEndMinutes = 0
            val night = ScheduleObject(
                context.getString(R.string.default_schedule_night),
                (nightStartHour * 60 * 60 * 1000 + nightStarMinutes * 60 * 1000).toLong(),
                (nightEndHour * 60 * 60 * 1000 + nightEndMinutes * 60 * 1000).toLong(),
                VolumeState.TIME_SETTING_VIBRATE,
                0,
                pmon = true,
                ptue = true,
                pwed = true,
                pthu = true,
                pfri = true,
                psat = true,
                psun = true
            )

            db.createScheduleEntry(night)


            var dayStartHour = 8
            var dayStarMinutes = 15
            var dayEndHour = 21
            var dayEndMinutes = 0
            val day = ScheduleObject(
                context.getString(R.string.default_schedule_day),
                (dayStartHour * 60 * 60 * 1000 + dayStarMinutes * 60 * 1000).toLong(),
                (dayEndHour * 60 * 60 * 1000 + dayEndMinutes * 60 * 1000).toLong(),
                VolumeState.TIME_SETTING_LOUD,
                0,
                pmon = true,
                ptue = true,
                pwed = true,
                pthu = true,
                pfri = true,
                psat = true,
                psun = true
            )
            db.createScheduleEntry(day)

        }
    }

}