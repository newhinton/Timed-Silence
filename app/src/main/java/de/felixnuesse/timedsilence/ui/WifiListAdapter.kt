package de.felixnuesse.timedsilence.ui;

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.Constants.Companion.WIFI_TYPE_CONNECTED
import de.felixnuesse.timedsilence.Constants.Companion.WIFI_TYPE_SEARCHING
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterWifiListBinding
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import kotlin.collections.ArrayList


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
class WifiListAdapter(private val myDataset: ArrayList<WifiObject>) :
    RecyclerView.Adapter<WifiListAdapter.WifiViewHolder>() {

    private fun removeAt(position: Int) {
        myDataset.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, myDataset.size)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val binding =
            AdapterWifiListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WifiViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val context = holder.wifiView.root.context

        var wifiObj = myDataset[position]

        holder.wifiView.wifiTitle.text = wifiObj.ssid

        holder.wifiView.deleteWifiElement.setOnClickListener {
            DatabaseHandler(context).deleteWifiEntry(wifiObj.id)
            removeAt(position)
        }


        var string = R.string.volume_setting_silent
        when (wifiObj.type) {
            WIFI_TYPE_CONNECTED -> string = R.string.wifi_type_connected
            WIFI_TYPE_SEARCHING -> string = R.string.wifi_type_searching
        }
        holder.wifiView.wifiType.text = context.getString(string)


        var imageID=R.drawable.icon_volume_up
        when (wifiObj.volume) {
            TIME_SETTING_LOUD -> imageID=R.drawable.icon_volume_up
            TIME_SETTING_VIBRATE -> imageID=R.drawable.icon_vibration
            TIME_SETTING_SILENT -> imageID=R.drawable.icon_volume_off
        }

        holder.wifiView.wifiState.setImageDrawable(
            AppCompatResources.getDrawable(context, imageID)
        )

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    class WifiViewHolder(val wifiView: AdapterWifiListBinding) :
        RecyclerView.ViewHolder(wifiView.root)

}


