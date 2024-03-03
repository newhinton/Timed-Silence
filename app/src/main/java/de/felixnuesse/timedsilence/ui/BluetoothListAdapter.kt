package de.felixnuesse.timedsilence.ui;

import android.bluetooth.BluetoothClass
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterBluetoothListBinding
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.data.BluetoothObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 14.04.19 - 17:18
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
class BluetoothListAdapter(private var myDataset: ArrayList<BluetoothObject>, private var mContext: Context) : RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
                val binding = AdapterBluetoothListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return BluetoothViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
                val bluetoothDevice = myDataset[position]

                holder.view.rowTitle.text = bluetoothDevice.name
                val resourceIdDeviceType = when(bluetoothDevice.type) {
                        BluetoothClass.Device.Major.COMPUTER -> R.drawable.icon_computer
                        BluetoothClass.Device.Major.PHONE -> R.drawable.icon_phone
                        BluetoothClass.Device.Major.NETWORKING -> R.drawable.icon_lan
                        BluetoothClass.Device.Major.AUDIO_VIDEO -> R.drawable.icon_audio
                        BluetoothClass.Device.Major.PERIPHERAL -> R.drawable.icon_keyboard
                        BluetoothClass.Device.Major.IMAGING -> R.drawable.icon_pause
                        BluetoothClass.Device.Major.WEARABLE -> R.drawable.icon_watch
                        BluetoothClass.Device.Major.TOY -> R.drawable.icon_toy
                        BluetoothClass.Device.Major.HEALTH -> R.drawable.icon_health
                        else -> R.drawable.icon_bluetooth
                }

                holder.view.deviceType.setImageDrawable(AppCompatResources.getDrawable(mContext, resourceIdDeviceType))

                val stringIdDeviceDescription = when(bluetoothDevice.type) {
                        BluetoothClass.Device.Major.COMPUTER -> R.string.adapter_bluetooth_device_description_computer
                        BluetoothClass.Device.Major.PHONE -> R.string.adapter_bluetooth_device_description_phone
                        BluetoothClass.Device.Major.NETWORKING -> R.string.adapter_bluetooth_device_description_networking
                        BluetoothClass.Device.Major.AUDIO_VIDEO -> R.string.adapter_bluetooth_device_description_audio_video
                        BluetoothClass.Device.Major.PERIPHERAL -> R.string.adapter_bluetooth_device_description_peripheral
                        BluetoothClass.Device.Major.IMAGING -> R.string.adapter_bluetooth_device_description_imaging
                        BluetoothClass.Device.Major.WEARABLE -> R.string.adapter_bluetooth_device_description_wearable
                        BluetoothClass.Device.Major.TOY -> R.string.adapter_bluetooth_device_description_toy
                        BluetoothClass.Device.Major.HEALTH -> R.string.adapter_bluetooth_device_description_health
                        else -> R.string.adapter_bluetooth_device_description_misc
                }

                holder.view.deviceTypeDescription.text = mContext.getString(stringIdDeviceDescription)

                val resourceIdVolumeState = when (bluetoothDevice.volumeState) {
                        VolumeState.TIME_SETTING_LOUD -> R.drawable.icon_volume_up
                        VolumeState.TIME_SETTING_VIBRATE -> R.drawable.icon_vibration
                        VolumeState.TIME_SETTING_SILENT -> R.drawable.icon_volume_off
                        else -> R.drawable.icon_volume_up
                }

                holder.view.volumeState.setImageDrawable(AppCompatResources.getDrawable(mContext, resourceIdVolumeState))

                holder.view.editElement.setOnClickListener {
                        //todo: Implement adding bluetooth device
                        Toast.makeText(mContext, "Not Implemented! Todo!", Toast.LENGTH_SHORT).show()
                        //db.addOrUpdateBluetooth(bluetoothDevice)
                }

                holder.view.deleteElement.setOnClickListener {
                        DatabaseHandler(mContext).deleteBluetoothDevice(bluetoothDevice.address)
                        removeAt(position)
                }
        }


        private fun removeAt(position: Int) {
                myDataset.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, myDataset.size)
        }

        override fun getItemCount() = myDataset.size

        class BluetoothViewHolder(val view: AdapterBluetoothListBinding) : RecyclerView.ViewHolder(view.root)

}


