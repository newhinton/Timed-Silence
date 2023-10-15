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
import de.felixnuesse.timedsilence.databinding.DialogWifiBinding
import de.felixnuesse.timedsilence.fragments.WifiConnectedFragment
import de.felixnuesse.timedsilence.model.data.WifiObject


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
    private lateinit var binding: DialogWifiBinding


    constructor(context: Context, tfragment: WifiConnectedFragment) : this(context) {
        tfrag=tfragment
    }


    private var state: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogWifiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        hideAll()
        binding.wifiBack.visibility = View.INVISIBLE
        binding.wifiDialogTitle.text = context.getText(R.string.calendar_dialog_title_title)
        binding.wifiSsidLayout.visibility = View.VISIBLE

        binding.wifiNext.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: next!")

            hideAll()
            state++
            decideState()
        }

        binding.wifiBack.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: back!")

            hideAll()
            state--
            decideState()
        }

        binding.wifiCancel.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: cancel!")
            this.cancel()
        }

        binding.wifiSave.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiDialog: save!")

            val volId = getValueForVolumeRadioGroup()
            val type = getValueForTypeRadioGroup()
            val wifiObj = WifiObject(
                0,//calendar_id_select.text.toString(),
                binding.wifiSsidTextfield.text.toString(),
                type,
                volId
            )
            tfrag?.saveWifi(context,wifiObj)

            this.cancel()
        }
    }

    private fun hideAll() {
        binding.wifiSsidLayout.visibility = View.GONE
        binding.wifiDialogRbVolume.visibility = View.GONE
        binding.wifiDialogRbType.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (binding.wifiDialogRbVolume.checkedRadioButtonId) {
            R.id.wifi_dialog_rb_loud -> return Constants.TIME_SETTING_LOUD
            R.id.wifi_dialog_rb_silent -> return Constants.TIME_SETTING_SILENT
            R.id.wifi_dialog_rb_vibrate -> return Constants.TIME_SETTING_VIBRATE
        }
        return Constants.TIME_SETTING_VIBRATE;
    }

    private fun getValueForTypeRadioGroup(): Int{
        when (binding.wifiDialogRbType.checkedRadioButtonId) {
            R.id.wifi_dialog_type_searching -> return Constants.WIFI_TYPE_SEARCHING
            R.id.wifi_dialog_type_connected -> return Constants.WIFI_TYPE_CONNECTED
        }
        return Constants.WIFI_TYPE_CONNECTED
    }

    private fun decideState() {

        if(state==0){
            binding.wifiBack.visibility = View.INVISIBLE
            binding.wifiSave.visibility = View.GONE
            binding.wifiNext.visibility = View.VISIBLE
        }else if (state == 2){
            binding.wifiSave.visibility = View.VISIBLE
            binding.wifiBack.visibility = View.VISIBLE
            binding.wifiNext.visibility = View.GONE
        }else {
            binding.wifiBack.visibility = View.VISIBLE
            binding.wifiNext.visibility = View.VISIBLE
            binding.wifiSave.visibility = View.GONE
        }

        when (state) {
            0 -> {
                binding.wifiDialogTitle.text = context.getText(R.string.schedule_dialog_title_title)
                binding.wifiSsidLayout.visibility = View.VISIBLE
            }
            1 -> {
                binding.wifiDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.wifiDialogRbType.visibility = View.VISIBLE

            }
            2 -> {
                binding.wifiDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.wifiDialogRbVolume.visibility = View.VISIBLE

            }

        }

    }
}