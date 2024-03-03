package de.felixnuesse.timedsilence.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.DialogBluetoothBinding
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.fragments.BluetoothFragment
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.model.data.BluetoothObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.util.WindowUtils


/**
 * Copyright (C) 2024  Felix Nüsse
 * Created on  03.03.2024
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
class BluetoothDialog(context: Context) : Dialog(context, R.style.AlertDialogCustom) {

    private var fragment: BluetoothFragment? = null

    private lateinit var binding: DialogBluetoothBinding

    private var selectedDeviceName = ""

    constructor(context: Context, fragment: BluetoothFragment) : this(context) {
        this.fragment = fragment
    }

    private var state: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.let { WindowUtils.applyDialogPaddingFixForDarkmode(context, it) }

        binding = DialogBluetoothBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)

        val devices = arrayListOf<String>()

        val existingDevices = HashMap<String, String>()
        HeadsetHandler.getPairedDevicesWithChangesInVolume(context).forEach{
            existingDevices[it.name] = it.address
        }

        HeadsetHandler.getPairedDevices(context).forEach{
            if(!existingDevices.containsKey(it.name)){
                devices.add(it.name)
            }
        }

        ArrayAdapter(context, android.R.layout.simple_list_item_1, devices).also {
                adapter -> binding.deviceSpinner.setAdapter(adapter)
        }

        binding.deviceSpinner.setOnItemClickListener{ adapter, _, position, _ ->
            selectedDeviceName = adapter.adapter.getItem(position).toString()
        }

        hideAll()
        binding.bluetoothBack.visibility = View.INVISIBLE
        binding.bluetoothDialogTitle.text = context.getText(R.string.bluetooth_dialog_title_title)
        binding.bluetoothLayout.visibility = View.VISIBLE

        binding.bluetoothNext.setOnClickListener {
            Log.e(TAG(), "BluetoothDialog: next!")

            hideAll()
            state++
            decideState()
        }

        binding.bluetoothBack.setOnClickListener {
            Log.e(TAG(), "BluetoothDialog: back!")

            hideAll()
            state--
            decideState()
        }

        binding.bluetoothCancel.setOnClickListener {
            Log.e(TAG(), "BluetoothDialog: cancel!")
            this.cancel()
        }

        binding.bluetoothSave.setOnClickListener {
            Log.e(TAG(), "BluetoothDialog: save!")

            var device = BluetoothObject("", "")
            HeadsetHandler.getPairedDevices(context).forEach{
                if(it.name == selectedDeviceName) {
                    device = BluetoothObject(it.name, it.address)
                }
            }

            if(device.address.isEmpty()) {
                this.cancel()
            }

            device.volumeState = getValueForVolumeRadioGroup()
            DatabaseHandler(context).addOrUpdateBluetooth(device)
            fragment?.notifyChange()
            this.cancel()
        }
    }

    private fun hideAll() {
        binding.bluetoothLayout.visibility = View.GONE
        binding.bluetoothDialogRbVolume.visibility = View.GONE
    }

    private fun getValueForVolumeRadioGroup(): Int{
        when (binding.bluetoothDialogRbVolume.checkedRadioButtonId) {
            R.id.bluetooth_dialog_rb_loud -> return TIME_SETTING_LOUD
            R.id.bluetooth_dialog_rb_silent -> return TIME_SETTING_SILENT
            R.id.bluetooth_dialog_rb_vibrate -> return TIME_SETTING_VIBRATE
        }
        return TIME_SETTING_VIBRATE
    }

    private fun decideState() {

        if(state==0){
            binding.bluetoothBack.visibility = View.INVISIBLE
            binding.bluetoothSave.visibility = View.GONE
            binding.bluetoothNext.visibility = View.VISIBLE
        }else if (state == 1){
            binding.bluetoothSave.visibility = View.VISIBLE
            binding.bluetoothBack.visibility = View.VISIBLE
            binding.bluetoothNext.visibility = View.GONE
        }else {
            binding.bluetoothBack.visibility = View.VISIBLE
            binding.bluetoothNext.visibility = View.VISIBLE
            binding.bluetoothSave.visibility = View.GONE
        }

        when (state) {
            0 -> {
                binding.bluetoothDialogTitle.text = context.getText(R.string.keyword_dialog_title_title)
                binding.bluetoothLayout.visibility = View.VISIBLE
            }
            1 -> {
                binding.bluetoothDialogTitle.text = context.getText(R.string.schedule_dialog_title_volume)
                binding.bluetoothDialogRbVolume.visibility = View.VISIBLE

            }

        }

    }

    fun setBluetoothObject(bluetoothDevice: BluetoothObject) {
        selectedDeviceName = bluetoothDevice.name
        binding.deviceSpinner.setText(selectedDeviceName, false)
        binding.deviceSpinner.isEnabled = false

        hideAll()
        state = 1
        decideState()

    }
}