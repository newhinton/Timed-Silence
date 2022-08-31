package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import kotlinx.android.synthetic.main.schedule_dialog.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on  28.06.2019
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */
class ScheduleDialog(context: Context) : Dialog(context) {


    private var timeFragment: TimeFragment? = null
    private var scheduleListHolder: ScheduleListAdapter? = null
    private var createNewSchedule: Boolean = true
    private var existingSchedule: ScheduleObject? = null

    constructor(context: Context, timeFragment: TimeFragment) : this(context) {
        this.timeFragment = timeFragment
        createNewSchedule = true
    }

    constructor(context: Context, scheduleHolder: ScheduleListAdapter, scheduleObject: ScheduleObject) : this(context) {
        scheduleListHolder = scheduleHolder
        createNewSchedule = false
        existingSchedule = scheduleObject
    }

    private var state: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.schedule_dialog)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setCanceledOnTouchOutside(true)


        if (!createNewSchedule) {
            prepareUpdate(existingSchedule!!)
        }

        if (createNewSchedule) {
            when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> schedule_dialog_days_monday.isChecked = true
                Calendar.TUESDAY -> schedule_dialog_days_tuesday.isChecked = true
                Calendar.WEDNESDAY -> schedule_dialog_days_wednesday.isChecked = true
                Calendar.THURSDAY -> schedule_dialog_days_thursday.isChecked = true
                Calendar.FRIDAY -> schedule_dialog_days_friday.isChecked = true
                Calendar.SATURDAY -> schedule_dialog_days_saturday.isChecked = true
                Calendar.SUNDAY -> schedule_dialog_days_sunday.isChecked = true
            }
        }

        hideAll()
        schedule_back.visibility = View.INVISIBLE
        schedule_start_timepicker.setIs24HourView(DateFormat.is24HourFormat(context))
        schedule_end_timepicker.setIs24HourView(DateFormat.is24HourFormat(context))

        schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_title)
        schedule_title_layout.visibility = View.VISIBLE

        schedule_next.setOnClickListener {
            Log.e(Constants.APP_NAME, "ScheduleDialog: next!")

            val view = schedule_dialog_title
            val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)

            hideAll()
            state++
            decideState()
        }

        schedule_back.setOnClickListener {
            Log.e(Constants.APP_NAME, "ScheduleDialog: back!")

            hideAll()
            state--
            decideState()
        }

        schedule_cancel.setOnClickListener {
            Log.e(Constants.APP_NAME, "ScheduleDialog: cancel!")
            this.cancel()
        }

        schedule_save.setOnClickListener {
            Log.e(Constants.APP_NAME, "ScheduleDialog: save!")

            if (createNewSchedule) {
                val so = ScheduleObject(
                    schedule_title_textfield.text.toString(),
                    (schedule_start_timepicker.hour * 60 * 60 * 1000 + schedule_start_timepicker.minute * 60 * 1000).toLong(),
                    (schedule_end_timepicker.hour * 60 * 60 * 1000 + schedule_end_timepicker.minute * 60 * 1000).toLong(),
                    getValueForVolumeRadioGroup(),
                    0,
                    schedule_dialog_days_monday.isChecked,
                    schedule_dialog_days_tuesday.isChecked,
                    schedule_dialog_days_wednesday.isChecked,
                    schedule_dialog_days_thursday.isChecked,
                    schedule_dialog_days_friday.isChecked,
                    schedule_dialog_days_saturday.isChecked,
                    schedule_dialog_days_sunday.isChecked
                )
                timeFragment?.saveSchedule(context, so)
            } else {
                val so = ScheduleObject(
                    schedule_title_textfield.text.toString(),
                    (schedule_start_timepicker.hour * 60 * 60 * 1000 + schedule_start_timepicker.minute * 60 * 1000).toLong(),
                    (schedule_end_timepicker.hour * 60 * 60 * 1000 + schedule_end_timepicker.minute * 60 * 1000).toLong(),
                    getValueForVolumeRadioGroup(),
                    existingSchedule!!.id,
                    schedule_dialog_days_monday.isChecked,
                    schedule_dialog_days_tuesday.isChecked,
                    schedule_dialog_days_wednesday.isChecked,
                    schedule_dialog_days_thursday.isChecked,
                    schedule_dialog_days_friday.isChecked,
                    schedule_dialog_days_saturday.isChecked,
                    schedule_dialog_days_sunday.isChecked
                )
                scheduleListHolder?.update(context, so)
            }


            this.cancel()
        }
    }

    private fun hideAll() {
        schedule_title_layout.visibility = View.GONE
        schedule_start_timepicker.visibility = View.GONE
        schedule_end_timepicker.visibility = View.GONE
        schedule_dialog_rb_volume.visibility = View.GONE
        schedule_days_layout.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int {
        when (schedule_dialog_rb_volume.checkedRadioButtonId) {
            R.id.schedule_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.schedule_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.schedule_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun setValueForVolumeRadioGroup(id: Int) {
        when (id) {
            Constants.TIME_SETTING_LOUD -> schedule_dialog_rb_loud.isChecked = true
            Constants.TIME_SETTING_SILENT -> schedule_dialog_rb_silent.isChecked = true
            Constants.TIME_SETTING_VIBRATE -> schedule_dialog_rb_vibrate.isChecked = true
        }
    }

    private fun prepareUpdate(so: ScheduleObject) {
        schedule_title_textfield.setText(so.name)

        var hours = TimeUnit.MILLISECONDS.toHours(so.time_start).toInt()
        var min =
            TimeUnit.MILLISECONDS.toMinutes(so.time_start - TimeUnit.HOURS.toMillis(hours.toLong()))
                .toInt()
        schedule_start_timepicker.hour = hours
        schedule_start_timepicker.minute = min

        hours = TimeUnit.MILLISECONDS.toHours(so.time_end).toInt()
        min = TimeUnit.MILLISECONDS.toMinutes(so.time_end - TimeUnit.HOURS.toMillis(hours.toLong()))
            .toInt()
        schedule_end_timepicker.hour = hours
        schedule_end_timepicker.minute = min

        setValueForVolumeRadioGroup(so.time_setting)

        schedule_dialog_days_monday.isChecked = so.mon
        schedule_dialog_days_tuesday.isChecked = so.tue
        schedule_dialog_days_wednesday.isChecked = so.wed
        schedule_dialog_days_thursday.isChecked = so.thu
        schedule_dialog_days_friday.isChecked = so.fri
        schedule_dialog_days_saturday.isChecked = so.sat
        schedule_dialog_days_sunday.isChecked = so.sun
    }

    private fun decideState() {

        if (state == 0) {
            schedule_back.visibility = View.INVISIBLE
            schedule_save.visibility = View.GONE
        } else if (state == 4) {
            schedule_save.visibility = View.VISIBLE
            schedule_back.visibility = View.VISIBLE
            schedule_next.visibility = View.GONE
        } else {
            schedule_back.visibility = View.VISIBLE
            schedule_next.visibility = View.VISIBLE
            schedule_save.visibility = View.GONE
        }

        when (state) {
            0 -> {
                schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_title)
                schedule_title_layout.visibility = View.VISIBLE
            }
            1 -> {
                schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_volume)
                schedule_dialog_rb_volume.visibility = View.VISIBLE
            }
            2 -> {
                schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_start)
                schedule_start_timepicker.visibility = View.VISIBLE
            }
            3 -> {
                schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_end)
                schedule_end_timepicker.visibility = View.VISIBLE
            }
            4 -> {
                schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_days)
                schedule_days_layout.visibility = View.VISIBLE
            }
        }

    }
}