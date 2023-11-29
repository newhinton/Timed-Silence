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
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.DialogScheduleBinding
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import java.util.*
import java.util.concurrent.TimeUnit

import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE

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
class ScheduleDialog(context: Context) : Dialog(context, R.style.AlertDialogCustom) {

    companion object {
        private const val TAG = "ScheduleDialog"
    }

    private var timeFragment: TimeFragment? = null
    private var scheduleListHolder: ScheduleListAdapter? = null
    private var createNewSchedule: Boolean = true
    private var existingSchedule: ScheduleObject? = null

    private lateinit var binding: DialogScheduleBinding

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

        binding = DialogScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
                Calendar.MONDAY -> binding.scheduleDialogDaysMonday.isChecked = true
                Calendar.TUESDAY -> binding.scheduleDialogDaysTuesday.isChecked = true
                Calendar.WEDNESDAY -> binding.scheduleDialogDaysWednesday.isChecked = true
                Calendar.THURSDAY -> binding.scheduleDialogDaysThursday.isChecked = true
                Calendar.FRIDAY -> binding.scheduleDialogDaysFriday.isChecked = true
                Calendar.SATURDAY -> binding.scheduleDialogDaysSaturday.isChecked = true
                Calendar.SUNDAY -> binding.scheduleDialogDaysSunday.isChecked = true
            }
        }

        hideAll()
        binding.scheduleBack.visibility = View.INVISIBLE
        binding.scheduleStartTimepicker.setIs24HourView(DateFormat.is24HourFormat(context))
        binding.scheduleEndTimepicker.setIs24HourView(DateFormat.is24HourFormat(context))

        binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_title)
        binding.scheduleTitleLayout.visibility = View.VISIBLE

        binding.scheduleNext.setOnClickListener {
            Log.e(TAG, "ScheduleDialog: next!")

            val view = binding.scheduleDialogTitle
            val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)

            hideAll()
            state++
            decideState()
        }

        binding.scheduleBack.setOnClickListener {
            Log.e(TAG, "ScheduleDialog: back!")

            hideAll()
            state--
            decideState()
        }

        binding.scheduleCancel.setOnClickListener {
            Log.e(TAG, "ScheduleDialog: cancel!")
            this.cancel()
        }

        binding.scheduleSave.setOnClickListener {
            Log.e(TAG, "ScheduleDialog: save!")

            if (createNewSchedule) {
                val so = ScheduleObject(
                    binding.scheduleTitleTextfield.text.toString(),
                    (binding.scheduleStartTimepicker.hour * 60 * 60 * 1000 + binding.scheduleStartTimepicker.minute * 60 * 1000).toLong(),
                    (binding.scheduleEndTimepicker.hour * 60 * 60 * 1000 + binding.scheduleEndTimepicker.minute * 60 * 1000).toLong(),
                    getValueForVolumeRadioGroup(),
                    0,
                    binding.scheduleDialogDaysMonday.isChecked,
                    binding.scheduleDialogDaysTuesday.isChecked,
                    binding.scheduleDialogDaysWednesday.isChecked,
                    binding.scheduleDialogDaysThursday.isChecked,
                    binding.scheduleDialogDaysFriday.isChecked,
                    binding.scheduleDialogDaysSaturday.isChecked,
                    binding.scheduleDialogDaysSunday.isChecked
                )
                timeFragment?.saveSchedule(context, so)
            } else {
                val so = ScheduleObject(
                    binding.scheduleTitleTextfield.text.toString(),
                    (binding.scheduleStartTimepicker.hour * 60 * 60 * 1000 + binding.scheduleStartTimepicker.minute * 60 * 1000).toLong(),
                    (binding.scheduleEndTimepicker.hour * 60 * 60 * 1000 + binding.scheduleEndTimepicker.minute * 60 * 1000).toLong(),
                    getValueForVolumeRadioGroup(),
                    existingSchedule!!.id,
                    binding.scheduleDialogDaysMonday.isChecked,
                    binding.scheduleDialogDaysTuesday.isChecked,
                    binding.scheduleDialogDaysWednesday.isChecked,
                    binding.scheduleDialogDaysThursday.isChecked,
                    binding.scheduleDialogDaysFriday.isChecked,
                    binding.scheduleDialogDaysSaturday.isChecked,
                    binding.scheduleDialogDaysSunday.isChecked
                )
                scheduleListHolder?.update(context, so)
            }


            this.cancel()
        }
    }

    private fun hideAll() {
        binding.scheduleTitleLayout.visibility = View.GONE
        binding.scheduleStartTimepicker.visibility = View.GONE
        binding.scheduleEndTimepicker.visibility = View.GONE
        binding.scheduleDialogRbVolume.visibility = View.GONE
        binding.scheduleDaysLayout.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int {
        when (binding.scheduleDialogRbVolume.checkedRadioButtonId) {
            R.id.schedule_dialog_rb_loud -> return TIME_SETTING_LOUD
            R.id.schedule_dialog_rb_silent -> return TIME_SETTING_SILENT
            R.id.schedule_dialog_rb_vibrate -> return TIME_SETTING_VIBRATE
        }
        return TIME_SETTING_VIBRATE;
    }

    private fun setValueForVolumeRadioGroup(id: Int) {
        when (id) {
            TIME_SETTING_LOUD -> binding.scheduleDialogRbLoud.isChecked = true
            TIME_SETTING_SILENT -> binding.scheduleDialogRbSilent.isChecked = true
            TIME_SETTING_VIBRATE -> binding.scheduleDialogRbVibrate.isChecked = true
        }
    }

    private fun prepareUpdate(so: ScheduleObject) {
        binding.scheduleTitleTextfield.setText(so.name)

        var hours = TimeUnit.MILLISECONDS.toHours(so.time_start).toInt()
        var min =
            TimeUnit.MILLISECONDS.toMinutes(so.time_start - TimeUnit.HOURS.toMillis(hours.toLong()))
                .toInt()
        binding.scheduleStartTimepicker.hour = hours
        binding.scheduleStartTimepicker.minute = min

        hours = TimeUnit.MILLISECONDS.toHours(so.time_end).toInt()
        min = TimeUnit.MILLISECONDS.toMinutes(so.time_end - TimeUnit.HOURS.toMillis(hours.toLong()))
            .toInt()
        binding.scheduleEndTimepicker.hour = hours
        binding.scheduleEndTimepicker.minute = min

        setValueForVolumeRadioGroup(so.time_setting)

        binding.scheduleDialogDaysMonday.isChecked = so.mon
        binding.scheduleDialogDaysTuesday.isChecked = so.tue
        binding.scheduleDialogDaysWednesday.isChecked = so.wed
        binding.scheduleDialogDaysThursday.isChecked = so.thu
        binding.scheduleDialogDaysFriday.isChecked = so.fri
        binding.scheduleDialogDaysSaturday.isChecked = so.sat
        binding.scheduleDialogDaysSunday.isChecked = so.sun
    }

    private fun decideState() {

        if (state == 0) {
            binding.scheduleBack.visibility = View.INVISIBLE
            binding.scheduleSave.visibility = View.GONE
        } else if (state == 4) {
            binding.scheduleSave.visibility = View.VISIBLE
            binding.scheduleBack.visibility = View.VISIBLE
            binding.scheduleNext.visibility = View.GONE
        } else {
            binding.scheduleBack.visibility = View.VISIBLE
            binding.scheduleNext.visibility = View.VISIBLE
            binding.scheduleSave.visibility = View.GONE
        }

        when (state) {
            0 -> {
                binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_title)
                binding.scheduleTitleLayout.visibility = View.VISIBLE
            }
            1 -> {
                binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.scheduleDialogRbVolume.visibility = View.VISIBLE
            }
            2 -> {
                binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_start)
                binding.scheduleStartTimepicker.visibility = View.VISIBLE
            }
            3 -> {
                binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_end)
                binding.scheduleEndTimepicker.visibility = View.VISIBLE
            }
            4 -> {
                binding.scheduleDialogTitle.text = context.getText(R.string.schedule_dialog_title_days)
                binding.scheduleDaysLayout.visibility = View.VISIBLE
            }
        }

    }
}