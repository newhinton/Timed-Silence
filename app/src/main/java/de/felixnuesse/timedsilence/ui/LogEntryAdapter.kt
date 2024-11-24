package de.felixnuesse.timedsilence.ui;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterLogEntryBinding
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.util.DateUtil
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


/**
 * Copyright (C) 2024  Felix Nüsse
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
class LogEntryAdapter(private val entries: List<VolumeState>) : RecyclerView.Adapter<LogEntryAdapter.LogViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = AdapterLogEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {

        val logentry =  entries[position]
        val context = holder.logView.root.context

        var imageID = R.drawable.icon_volume_up
        when (logentry.state) {
            TIME_SETTING_LOUD -> imageID= R.drawable.icon_volume_up
            TIME_SETTING_VIBRATE -> imageID= R.drawable.icon_vibration
            TIME_SETTING_SILENT -> imageID= R.drawable.icon_volume_off
        }
        holder.logView.stateIcon.setImageDrawable(AppCompatResources.getDrawable(context, imageID))

        holder.logView.logTitle.text = logentry.reasonSource
        holder.logView.logContent.text = logentry.getReason()

        holder.logView.card.setOnClickListener { showTimestamp(logentry.id, context) }
        holder.logView.logTitle.setOnClickListener { showTimestamp(logentry.id, context) }
        holder.logView.logContent.setOnClickListener { showTimestamp(logentry.id, context) }
        holder.logView.stateIcon.setOnClickListener { showTimestamp(logentry.id, context) }
    }

    private fun showTimestamp(inMs: Long, context: Context) {
        val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val text = DateUtil.format(inMs, format)
        Toast.makeText(
            context, text,
            Toast.LENGTH_LONG
        ).show()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = entries.size

    class LogViewHolder(val logView: AdapterLogEntryBinding) : RecyclerView.ViewHolder(logView.root)

}


