package de.felixnuesse.timedsilence.ui;

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterGraphoverviewListBinding
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import kotlin.collections.ArrayList


/**
 * Copyright (C) 2023  Felix Nüsse
 * Created on 11.12.23
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
class GraphOverviewAdapter(private val data: ArrayList<VolumeState>) :
    RecyclerView.Adapter<GraphOverviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterGraphoverviewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var context = holder.viewbinding.root.context
        var state = data[position]
        holder.viewbinding.volumeStateTime.text = state.getFormattedStartDate()
        holder.viewbinding.volumeStateTitle.text = state.getReason()

        var color = when(state.state) {
            TIME_SETTING_SILENT -> R.color.color_graph_silent
            TIME_SETTING_VIBRATE -> R.color.color_graph_vibrate
            TIME_SETTING_LOUD -> R.color.color_graph_loud
            else -> R.color.color_graph_unset
        }

        holder.viewbinding.volumeStateIndicator.setColorFilter(context.getColor(color))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size

    class ViewHolder(val viewbinding: AdapterGraphoverviewListBinding) :
        RecyclerView.ViewHolder(viewbinding.root)

}


