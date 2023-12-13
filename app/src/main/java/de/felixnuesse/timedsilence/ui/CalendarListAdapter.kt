package de.felixnuesse.timedsilence.ui;

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.AdapterCalendarListBinding
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.volumestate.calendar.DeviceCalendar


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
class CalendarListAdapter(private var myDataset: ArrayList<CalendarObject>, private var calHandler: DeviceCalendar) : RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder>() {

        private fun removeAt(position: Int) {
                myDataset.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, myDataset.size)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun update(context: Context, co: CalendarObject){
                DatabaseHandler(context).updateCalendarEntry(co)
                myDataset.clear()
                myDataset = calHandler.getVolumeCalendars()
                notifyDataSetChanged()
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
                val binding = AdapterCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CalendarViewHolder(binding, calHandler)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                val calObject= myDataset[position]
                val context = holder.calendarView.root.context

                holder.calendarView.textViewCalendarRowTitle.text = calObject.name
                holder.calendarView.deleteCalendarElement.setOnClickListener {
                        DatabaseHandler(context).deleteCalendarEntry(calObject.id)
                        removeAt(position)
                }

                val color = calHandler.getCalendarColor(calObject.name)
                holder.calendarView.cardView.setCardBackgroundColor(color)
                holder.calendarView.deleteCalendarElement.setColorFilter(color)

                applyTextfieldStyle(holder.calendarView.textViewCalendarRowTitle)

                if(Color.luminance(color) > 0.55) {

                        val typedValue = TypedValue()
                        val theme: Theme = context.theme
                        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
                        holder.calendarView.textViewCalendarRowTitle.setTextColor(typedValue.data)
                        holder.calendarView.volumeState.imageTintList = ColorStateList.valueOf(typedValue.data)
                }


                var imageID=R.drawable.icon_volume_up
                when (calObject.volume) {
                        TIME_SETTING_LOUD -> imageID=R.drawable.icon_volume_up
                        TIME_SETTING_VIBRATE -> imageID=R.drawable.icon_vibration
                        TIME_SETTING_SILENT -> imageID=R.drawable.icon_volume_off
                }
                holder.calendarView.volumeState.setImageDrawable(AppCompatResources.getDrawable(context, imageID))
        }

        private fun applyTextfieldStyle(view: TextView){
                //view.setTextColor(Color.BLACK)
                view.setTypeface(view.typeface, Typeface.BOLD)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class CalendarViewHolder(val calendarView: AdapterCalendarListBinding, var calHandler: DeviceCalendar) : RecyclerView.ViewHolder(calendarView.root)

}


