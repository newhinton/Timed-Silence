package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.model.data.CalendarObject
import android.widget.RadioGroup
import android.widget.RadioButton
import de.felixnuesse.timedsilence.handler.calculator.CalendarHandler
import android.widget.TextView
import android.text.Html
import de.felixnuesse.timedsilence.databinding.DialogCalendarBinding
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
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
class CalendarDialog(context: Context) : Dialog(context, R.style.AlertDialogCustom) {

    companion object {
        private const val TAG = "CalendarDialog"
    }

    private var tfrag: CalendarEventFragment? = null
    private lateinit var calHandler: CalendarHandler


    private var radioMap: HashMap<Int,Long> = HashMap()
    private var radioNameMap: HashMap<Long,String> = HashMap()

    private lateinit var binding: DialogCalendarBinding

    constructor(context: Context, tfragment: CalendarEventFragment, calHandler: CalendarHandler) : this(context) {
        tfrag=tfragment
        this.calHandler=calHandler
    }


    private var state: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        binding.calendarBack.visibility = View.INVISIBLE
        binding.calendarDialogTitle.text = context.getText(R.string.calendar_dialog_title_title)
        binding.calendarIdLayout.visibility = View.VISIBLE


        val rg = findViewById<RadioGroup>(R.id.calendar_radio_group)

        for(calObject in calHandler.getDeviceCalendars()){
            val radioButton = RadioButton(context)
            val hexColor = String.format("#%06X", 0xFFFFFF and calObject.color)
            val text = "<font color=\"$hexColor\">&#9612;</font>${calObject.name}"

            radioButton.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE)
            radioButton.id = View.generateViewId()
            radioMap.put(radioButton.id,calObject.externalID)
            radioNameMap.put(calObject.externalID,calObject.name)

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )

            rg.addView(radioButton, params)
        }

        rg.check(calHandler.getDeviceCalendars()[0].externalID.toInt())

        binding.calendarNext.setOnClickListener {
            Log.e(TAG, "CalendarDialog: next!")

            hideAll()
            state++
            decideState()
        }

        binding.calendarBack.setOnClickListener {
            Log.e(TAG, "CalendarDialog: back!")

            hideAll()
            state--
            decideState()
        }

        binding.calendarCancel.setOnClickListener {
            Log.e(TAG, "CalendarDialog: cancel!")
            this.cancel()
        }

        binding.calendarSave.setOnClickListener {
            Log.e(TAG, "CalendarDialog: save!")

            val volId = getValueForVolumeRadioGroup();
            val calId = getValueForCalendarRadioGroup();
            Log.e(TAG, "CalendarDialog: Volume: "+volId)
            Log.e(TAG, "CalendarDialog: CalID:  "+calId)
            val so = CalendarObject(
                0,//calendar_id_select.text.toString(),
                calId,
                volId
            )
            so.externalID=calId
            so.name = radioNameMap.getOrDefault(calId, "NOTSET")
            tfrag?.saveCalendar(context,so)

            this.cancel()
        }
    }

    private fun hideAll() {
        binding.calendarIdLayout.visibility = View.GONE
        binding.calendarDialogRbVolume.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (binding.calendarDialogRbVolume.checkedRadioButtonId) {
            R.id.calendar_dialog_rb_loud -> return TIME_SETTING_LOUD
            R.id.calendar_dialog_rb_silent -> return TIME_SETTING_SILENT
            R.id.calendar_dialog_rb_vibrate -> return TIME_SETTING_VIBRATE
        }
        return TIME_SETTING_VIBRATE;
    }

    private fun getValueForCalendarRadioGroup(): Long{
        var ret: Long = 0
        var key: Int = binding.calendarRadioGroup.checkedRadioButtonId

        if(radioMap.containsKey(key)){
            ret= radioMap[key]!!
        }
        return ret;
    }

    private fun decideState() {

        if(state==0){
            binding.calendarBack.visibility = View.INVISIBLE
            binding.calendarSave.visibility = View.GONE
            binding.calendarNext.visibility = View.VISIBLE
        }else if (state == 1){
            binding.calendarSave.visibility = View.VISIBLE
            binding.calendarBack.visibility = View.VISIBLE
            binding.calendarNext.visibility = View.GONE
        }else {
            binding.calendarBack.visibility = View.VISIBLE
            binding.calendarNext.visibility = View.VISIBLE
            binding.calendarSave.visibility = View.GONE
        }

        when (state) {
            0 -> {
                binding.calendarDialogTitle.text = context.getText(R.string.schedule_dialog_title_title)
                binding.calendarIdLayout.visibility = View.VISIBLE
            }
            1 -> {
                binding.calendarDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.calendarDialogRbVolume.visibility = View.VISIBLE

            }

        }

    }
}