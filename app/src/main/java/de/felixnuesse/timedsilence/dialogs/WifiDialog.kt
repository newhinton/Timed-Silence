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
import de.felixnuesse.timedsilence.fragments.WifiConnectedFragment
import de.felixnuesse.timedsilence.model.data.WifiObject
import kotlinx.android.synthetic.main.wifi_dialog.*


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
class WifiDialog(context: Context) : Dialog(context) {


    private var tfrag: WifiConnectedFragment? = null


    private var radioMap: HashMap<Int,Long> = HashMap()

    constructor(context: Context, tfragment: WifiConnectedFragment) : this(context) {
        tfrag=tfragment
    }


    private var state: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.wifi_dialog)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        wifi_back.visibility = View.INVISIBLE
        wifi_dialog_title.text = context.getText(R.string.calendar_dialog_title_title)
        wifi_ssid_layout.visibility = View.VISIBLE

        wifi_next.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: next!")

            hideAll()
            state++
            decideState()
        }

        wifi_back.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: back!")

            hideAll()
            state--
            decideState()
        }

        wifi_cancel.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: cancel!")
            this.cancel()
        }

        wifi_save.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: save!")

            val volId = getValueForVolumeRadioGroup()
            val type = getValueForTypeRadioGroup()
            val wifiObj = WifiObject(
                0,//calendar_id_select.text.toString(),
                wifi_ssid_textfield.text.toString(),
                type,
                volId
            )
            tfrag?.saveWifi(context,wifiObj)

            this.cancel()
        }
    }

    private fun hideAll() {
        wifi_ssid_layout.visibility = View.GONE
        wifi_volume_layout.visibility = View.GONE
        wifi_type_layout.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (wifi_dialog_rb_volume.checkedRadioButtonId) {
            R.id.wifi_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.wifi_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.wifi_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun getValueForTypeRadioGroup(): Int{
        when (wifi_dialog_rb_type.checkedRadioButtonId) {
            R.id.wifi_dialog_type_searching -> return Constants.WIFI_TYPE_SEARCHING
            R.id.wifi_dialog_type_connected -> return Constants.WIFI_TYPE_CONNECTED
        }
        return Constants.WIFI_TYPE_CONNECTED
    }

    private fun decideState() {

        if(state==0){
            wifi_back.visibility = View.INVISIBLE
            wifi_save.visibility = View.GONE
            wifi_next.visibility = View.VISIBLE
        }else if (state == 2){
            wifi_save.visibility = View.VISIBLE
            wifi_back.visibility = View.VISIBLE
            wifi_next.visibility = View.GONE
        }else {
            wifi_back.visibility = View.VISIBLE
            wifi_next.visibility = View.VISIBLE
            wifi_save.visibility = View.GONE
        }

        when (state) {
            0 -> {
                wifi_dialog_title.text = context.getText(R.string.schedule_dialog_title_title)
                wifi_ssid_layout.visibility = View.VISIBLE
            }
            1 -> {
                wifi_dialog_title.text = context.getText(R.string.schedule_dialog_title_volume)
                wifi_type_layout.visibility = View.VISIBLE

            }
            2 -> {
                wifi_dialog_title.text = context.getText(R.string.schedule_dialog_title_volume)
                wifi_volume_layout.visibility = View.VISIBLE

            }

        }

    }
}