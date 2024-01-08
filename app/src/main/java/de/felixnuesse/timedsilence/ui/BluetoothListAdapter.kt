package de.felixnuesse.timedsilence.ui;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterBluetoothListBinding
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


        private var db = DatabaseHandler(mContext)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
                val binding = AdapterBluetoothListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return BluetoothViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
                val context = holder.view.root.context
                val bluetoothDevice = myDataset[position]

                holder.view.rowTitle.text = bluetoothDevice.name

                val spinnerArray = listOf(
                        context.getString(R.string.volume_setting_unset),
                        context.getString(R.string.volume_setting_silent),
                        context.getString(R.string.volume_setting_vibrate),
                        context.getString(R.string.volume_setting_loud)
                )

                val spinner = holder.view.volumeSpinner
                val adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        spinnerArray
                )

                spinner.adapter = adapter

                spinner.setSelection(bluetoothDevice.volumeState)

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                               bluetoothDevice.volumeState = position
                                db.addOrUpdateBluetooth(bluetoothDevice)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                }
        }

        override fun getItemCount() = myDataset.size

        class BluetoothViewHolder(val view: AdapterBluetoothListBinding) : RecyclerView.ViewHolder(view.root)

}


