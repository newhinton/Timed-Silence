package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.model.data.CalendarObject
import kotlinx.android.synthetic.main.calendar_dialog.*
import android.widget.RadioGroup
import android.widget.RadioButton
import de.felixnuesse.timedsilence.handler.calculator.CalendarHandler
import android.widget.TextView
import android.text.Html





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
class CalendarDialog(context: Context) : Dialog(context) {


    private var tfrag: CalendarEventFragment? = null
    private var update_co: CalendarObject? = null
    private lateinit var calHandler: CalendarHandler


    private var radioMap: HashMap<Int,Long> = HashMap()

    constructor(context: Context, tfragment: CalendarEventFragment, calHandler: CalendarHandler) : this(context) {
        tfrag=tfragment
        this.calHandler=calHandler
    }


    private var state: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.calendar_dialog)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        calendar_back.visibility = View.INVISIBLE
        calendar_dialog_title.text = context.getText(R.string.calendar_dialog_title_title)
        calendar_id_layout.visibility = View.VISIBLE


        val rg = findViewById<RadioGroup>(R.id.calendar_radio_group)

        for(calObject in calHandler.getCalendars()){
            val radioButton = RadioButton(context)
            val hexColor = String.format("#%06X", 0xFFFFFF and calObject.color)
            val text = "<font color=\"$hexColor\">&#9612;</font>${calObject.name}"

            radioButton.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE)
            radioButton.id = View.generateViewId()
            radioMap.put(radioButton.id,calObject.ext_id)

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )

            rg.addView(radioButton, params)
        }

        rg.check(calHandler.getCalendars()[0].ext_id.toInt())

        calendar_next.setOnClickListener {
            Log.e(Constants.APP_NAME, "CalendarDialog: next!")

            hideAll()
            state++
            decideState()
        }

        calendar_back.setOnClickListener {
            Log.e(Constants.APP_NAME, "CalendarDialog: back!")

            hideAll()
            state--
            decideState()
        }

        calendar_cancel.setOnClickListener {
            Log.e(Constants.APP_NAME, "CalendarDialog: cancel!")
            this.cancel()
        }

        calendar_save.setOnClickListener {
            Log.e(Constants.APP_NAME, "CalendarDialog: save!")

            val volId = getValueForVolumeRadioGroup();
            val calId = getValueForCalendarRadioGroup();
            Log.e(Constants.APP_NAME, "CalendarDialog: Volume: "+volId)
            Log.e(Constants.APP_NAME, "CalendarDialog: CalID:  "+calId)
            val so = CalendarObject(
                0,//calendar_id_select.text.toString(),
                calId,
                volId
            )
            so.ext_id=calId
            tfrag?.saveCalendar(context,so)

            this.cancel()
        }
    }

    private fun hideAll() {
        calendar_id_layout.visibility = View.GONE
        calendar_dialog_rb_volume.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (calendar_dialog_rb_volume.checkedRadioButtonId) {
            R.id.calendar_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.calendar_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.calendar_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun getValueForCalendarRadioGroup(): Long{
        var ret: Long = 0
        var key: Int = calendar_radio_group.checkedRadioButtonId

        if(radioMap.containsKey(key)){
            ret= radioMap[key]!!
        }
        return ret;
    }

    private fun decideState() {

        if(state==0){
            calendar_back.visibility = View.INVISIBLE
            calendar_save.visibility = View.GONE
            calendar_next.visibility = View.VISIBLE
        }else if (state == 1){
            calendar_save.visibility = View.VISIBLE
            calendar_back.visibility = View.VISIBLE
            calendar_next.visibility = View.GONE
        }else {
            calendar_back.visibility = View.VISIBLE
            calendar_next.visibility = View.VISIBLE
            calendar_save.visibility = View.GONE
        }

        when (state) {
            0 -> {
                calendar_dialog_title.text = context.getText(R.string.schedule_dialog_title_title)
                calendar_id_layout.visibility = View.VISIBLE
            }
            1 -> {
                calendar_dialog_title.text = context.getText(R.string.schedule_dialog_title_volume)
                calendar_dialog_rb_volume.visibility = View.VISIBLE

            }

        }

    }
}