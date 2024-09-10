package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.DialogCalendarBinding
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.fragments.CalendarFragment
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.util.SizeUtil
import de.felixnuesse.timedsilence.util.VibrationUtil
import de.felixnuesse.timedsilence.util.WindowUtils
import de.felixnuesse.timedsilence.volumestate.calendar.DeviceCalendar


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

    private var tfrag: CalendarFragment? = null
    private lateinit var calHandler: DeviceCalendar
    private var calendarObject: CalendarObject? = null


    private var radioMap: HashMap<Int,Long> = HashMap()
    private var radioNameMap: HashMap<Long,String> = HashMap()

    private lateinit var binding: DialogCalendarBinding

    constructor(context: Context, tfragment: CalendarFragment, calHandler: DeviceCalendar) : this(context) {
        tfrag = tfragment
        this.calHandler=calHandler
    }


    constructor(context: Context, calendarFragment: CalendarFragment) : this(context) {
        tfrag = calendarFragment
        calHandler = DeviceCalendar(context)
    }

    fun setCalendarObject(calendarObject: CalendarObject) {
        this.calendarObject = calendarObject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.let { WindowUtils.applyDialogPaddingFixForDarkmode(context, it) }

        binding = DialogCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if(!VibrationUtil.canVibrate(context)) {
            binding.calendarDialogRbVibrate.visibility = View.GONE
        }

        if(calendarObject != null) {
            when(calendarObject!!.volume) {
                TIME_SETTING_LOUD -> binding.calendarDialogRbLoud.isChecked = true
                TIME_SETTING_SILENT -> binding.calendarDialogRbSilent.isChecked = true
                else ->  binding.calendarDialogRbVibrate.isChecked = true
            }
        }

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)

        val rg = findViewById<RadioGroup>(R.id.calendar_radio_group)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for(calObject in calHandler.getDeviceCalendars()){
            val radioButton = inflater.inflate(R.layout.customui_radiobutton, null) as RadioButton
            val hexColor = String.format("#%06X", 0xFFFFFF and calObject.color)
            val text = "<font color=\"$hexColor\">&#9612;</font>${calObject.name}"

            radioButton.setText(Html.fromHtml(text), TextView.BufferType.NORMAL)
            radioButton.id = View.generateViewId()
            radioMap[radioButton.id] = calObject.externalID
            radioNameMap[calObject.externalID] = calObject.name

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                SizeUtil.getSizeInDP(context, 32)
            )

            rg.addView(radioButton, params)
        }

        rg.check(calHandler.getDeviceCalendars()[0].externalID.toInt())

        binding.calendarCancel.setOnClickListener {
            Log.e(TAG(), "CalendarDialog: cancel!")
            this.cancel()
        }

        binding.calendarSave.setOnClickListener {
            Log.e(TAG(), "CalendarDialog: save!")

            val volId = getValueForVolumeRadioGroup();
            val calId = getValueForCalendarRadioGroup();
            Log.e(TAG(), "CalendarDialog: Volume: $volId")
            Log.e(TAG(), "CalendarDialog: CalID:  $calId")
            val so = CalendarObject(
                calendarObject?.id ?: 0,
                calId,
                volId
            )
            so.externalID=calId
            so.name = radioNameMap.getOrDefault(calId, "NOTSET")
            tfrag?.saveCalendar(context,so)

            this.cancel()
        }
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
        val key: Int = binding.calendarRadioGroup.checkedRadioButtonId

        if(radioMap.containsKey(key)){
            ret= radioMap[key]!!
        }
        return ret;
    }
}