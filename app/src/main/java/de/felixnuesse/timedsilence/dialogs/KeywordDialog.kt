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
import de.felixnuesse.timedsilence.fragments.KeywordFragment
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.data.KeywordObject.Companion.ALL_CALENDAR
import kotlinx.android.synthetic.main.keyword_dialog.*


/**
 * Copyright (C) 2021  Felix Nüsse
 * Created on  15.07.2021
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
class KeywordDialog(context: Context) : Dialog(context) {


    private var tfrag: KeywordFragment? = null

    constructor(context: Context, tfragment: KeywordFragment) : this(context) {
        tfrag=tfragment
    }

    private var state: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.keyword_dialog)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        keyword_back.visibility = View.INVISIBLE
        keyword_dialog_title.text = context.getText(R.string.keyword_dialog_title_title)
        keyword_keyword_layout.visibility = View.VISIBLE

        keyword_next.setOnClickListener {
            Log.e(Constants.APP_NAME, "KeywordDialog: next!")

            hideAll()
            state++
            decideState()
        }

        keyword_back.setOnClickListener {
            Log.e(Constants.APP_NAME, "KeywordDialog: back!")

            hideAll()
            state--
            decideState()
        }

        keyword_cancel.setOnClickListener {
            Log.e(Constants.APP_NAME, "KeywordDialog: cancel!")
            this.cancel()
        }

        keyword_save.setOnClickListener {
            Log.e(Constants.APP_NAME, "KeywordDialog: save!")

            val volId = getValueForVolumeRadioGroup()
            val keyword = KeywordObject(
                0,
                ALL_CALENDAR,
                keyword_textfield.text.toString(),
                volId
            )
            tfrag?.saveKeyword(context, keyword)
            this.cancel()
        }
    }

    private fun hideAll() {
        keyword_keyword_layout.visibility = View.GONE
        keyword_dialog_rb_volume.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (keyword_dialog_rb_volume.checkedRadioButtonId) {
            R.id.keyword_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.keyword_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.keyword_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun decideState() {

        if(state==0){
            keyword_back.visibility = View.INVISIBLE
            keyword_save.visibility = View.GONE
            keyword_next.visibility = View.VISIBLE
        }else if (state == 1){
            keyword_save.visibility = View.VISIBLE
            keyword_back.visibility = View.VISIBLE
            keyword_next.visibility = View.GONE
        }else {
            keyword_back.visibility = View.VISIBLE
            keyword_next.visibility = View.VISIBLE
            keyword_save.visibility = View.GONE
        }

        when (state) {
            0 -> {
                keyword_dialog_title.text = context.getText(R.string.keyword_dialog_title_title)
                keyword_keyword_layout.visibility = View.VISIBLE
            }
            1 -> {
                keyword_dialog_title.text = context.getText(R.string.schedule_dialog_title_volume)
                keyword_dialog_rb_volume.visibility = View.VISIBLE

            }

        }

    }
}