package de.felixnuesse.timedsilence.ui;

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterSchedulesListBinding
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import java.text.DateFormat
import java.util.*


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
class ScheduleListAdapter(private var myDataset: ArrayList<ScheduleObject>) :
    RecyclerView.Adapter<ScheduleListAdapter.ScheduleViewHolder>() {

    fun removeAt(position: Int) {
        myDataset.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, myDataset.size)
    }


    fun update(context: Context, so: ScheduleObject) {
        DatabaseHandler(context).updateScheduleEntry(so)
        myDataset.clear()
        myDataset = DatabaseHandler(context).getAllSchedules()
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {

        val binding =
            AdapterSchedulesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val df = DateFormat.getTimeInstance(DateFormat.SHORT)
        df.timeZone = TimeZone.getTimeZone("UTC")

        holder.scheduleView.textViewScheduleRowTitle.text = myDataset[position].name
        holder.scheduleView.scheduleStart.text =
            df.format(myDataset[position].timeStart)
        holder.scheduleView.scheduleEnd.text =
            df.format(myDataset[position].timeEnd)

        val c = holder.scheduleView.root.context

        if (myDataset[position].mon) {
            applyTextfieldStyle(holder.scheduleView.mon, c)
        }
        if (myDataset[position].tue) {
            applyTextfieldStyle(holder.scheduleView.tue, c)
        }
        if (myDataset[position].wed) {
            applyTextfieldStyle(holder.scheduleView.wed, c)
        }
        if (myDataset[position].thu) {
            applyTextfieldStyle(holder.scheduleView.thu, c)
        }
        if (myDataset[position].fri) {
            applyTextfieldStyle(holder.scheduleView.fri, c)
        }
        if (myDataset[position].sat) {
            applyTextfieldStyle(holder.scheduleView.sat, c)
        }
        if (myDataset[position].sun) {
            applyTextfieldStyle(holder.scheduleView.sun, c)
        }

        holder.scheduleView.deleteScheduleElement.setOnClickListener {
            DatabaseHandler(holder.scheduleView.root.context).deleteScheduleEntry(
                myDataset[position].id
            )
            removeAt(position)

        }

        holder.scheduleView.editScheduleElement.setOnClickListener {
            ScheduleDialog(holder.scheduleView.root.context, this, myDataset[position]).show()
        }

        var imageID = R.drawable.icon_volume_up
        when (myDataset[position].timeSetting) {
            TIME_SETTING_LOUD -> imageID = R.drawable.icon_volume_up
            TIME_SETTING_VIBRATE -> imageID = R.drawable.icon_vibration
            TIME_SETTING_SILENT -> imageID = R.drawable.icon_volume_off
        }
        holder.scheduleView.volumeState.setImageDrawable(
            AppCompatResources.getDrawable(holder.scheduleView.root.context, imageID)
        )


    }

    private fun applyTextfieldStyle(view: TextView, context: Context) {

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.colorTertiary, typedValue, true)
        @ColorInt val color = typedValue.data

        view.setTextColor(color)
        view.setTypeface(view.typeface, Typeface.BOLD)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    class ScheduleViewHolder(val scheduleView: AdapterSchedulesListBinding): RecyclerView.ViewHolder(scheduleView.root)
}


