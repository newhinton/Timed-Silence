package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import kotlinx.android.synthetic.main.schedule_dialog.*


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
class ScheduleDialog(context: Context, var tfrag: TimeFragment, private var create: Boolean) : Dialog(context) {


    private var state: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.schedule_dialog)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        schedule_back.visibility = View.INVISIBLE
        schedule_start_timepicker.setIs24HourView(DateFormat.is24HourFormat(context))
        schedule_end_timepicker.setIs24HourView(DateFormat.is24HourFormat(context))

        schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_title)
        schedule_title_layout.visibility = View.VISIBLE

        /*
        schedule_dialog_title.text = context.getText(R.string.schedule_dialog_title_days)

        */


        schedule_next.setOnClickListener {
            Log.e(Constants.APP_NAME, "ScheduleDialog: next!")

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
            tfrag.saveSchedule(context, ScheduleObject(
                schedule_title_textfield.text.toString(),
                (schedule_start_timepicker.hour *60*60*1000+schedule_start_timepicker.minute *60*1000).toLong(),
                (schedule_end_timepicker.hour *60*60*1000+schedule_end_timepicker.minute *60*1000).toLong(),
                getValueForVolumeRadioGroup(),
                0)
            )
            this.cancel()
        }


    }

    private fun hideAll() {
        schedule_title_layout.visibility = View.GONE
        schedule_start_timepicker.visibility = View.GONE
        schedule_end_timepicker.visibility = View.GONE
        schedule_volume_layout.visibility = View.GONE
        schedule_days_layout.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (schedule_dialog_rb_volume.checkedRadioButtonId) {
            R.id.schedule_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.schedule_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.schedule_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun decideState() {

        if(state==0){
            schedule_back.visibility = View.INVISIBLE
            schedule_save.visibility = View.GONE
        }else if (state == 4){
            schedule_save.visibility = View.VISIBLE
            schedule_back.visibility = View.VISIBLE
            schedule_next.visibility = View.GONE
        }else {
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
                schedule_volume_layout.visibility = View.VISIBLE

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