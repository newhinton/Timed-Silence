package de.felixnuesse.timedsilence.ui;

import android.content.Context
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.calculator.CalendarHandler
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import kotlinx.android.synthetic.main.adapter_calendar_list.view.*
import kotlinx.android.synthetic.main.adapter_schedules_list.view.imageView_volume_state
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
class CalendarListAdapter(private var myDataset: ArrayList<CalendarObject>, var calHandler: CalendarHandler) : RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder>() {

        fun removeAt(position: Int) {
                myDataset.removeAt(position)
                notifyDataSetChanged()
        }

        fun update(context: Context, co: CalendarObject){
                DatabaseHandler(context).updateCalendarEntry(co)
                myDataset.clear()
                myDataset = calHandler.getAllCalendarEntries()
                notifyDataSetChanged()
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarListAdapter.CalendarViewHolder {
                // create a new view
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_calendar_list, parent, false)
                // set the view's size, margins, paddings and layout parameters
                return CalendarViewHolder(view, calHandler)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                val calObject=myDataset.get(position)

                holder.calendarView.textView_calendar_row_title.text = calHandler.getCalendarName(calObject.ext_id, "")

                holder.calendarView.delete_calendar_element.setOnClickListener {
                        DatabaseHandler(holder.calendarView.context).deleteCalendarEntry(calObject.id)
                        removeAt(position)
                }

                applyTextfieldStyle(holder.calendarView.textView_calendar_row_title)

                holder.calendarView.imageView_calendar_color.setColorFilter(calHandler.getCalendarColor(calObject.ext_id))

                var imageID=R.drawable.ic_volume_up_black_24dp
                when (calObject.volume) {
                        TIME_SETTING_LOUD -> imageID=R.drawable.ic_volume_up_black_24dp
                        TIME_SETTING_VIBRATE -> imageID=R.drawable.ic_vibration_black_24dp
                        TIME_SETTING_SILENT -> imageID=R.drawable.ic_volume_off_black_24dp
                }
                holder.calendarView.imageView_volume_state.setImageDrawable(holder.calendarView.context.getDrawable(imageID))
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
        class CalendarViewHolder(val calendarView: View, var calHandler: CalendarHandler) : RecyclerView.ViewHolder(calendarView)

}


